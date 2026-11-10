package org.truthwatchindia.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.truthwatchindia.app.data.api.Cf7Response
import org.truthwatchindia.app.data.api.WordPressApiService
import org.truthwatchindia.app.data.local.dao.ArticleCacheDao
import org.truthwatchindia.app.data.local.dao.BookmarkDao
import org.truthwatchindia.app.data.local.entity.BookmarkEntity
import org.truthwatchindia.app.data.local.entity.CachedArticleEntity
import org.truthwatchindia.app.data.model.WpAuthor
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpEmbedded
import org.truthwatchindia.app.data.model.WpMedia
import org.truthwatchindia.app.data.model.WpPage
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.data.model.WpRendered
import org.truthwatchindia.app.data.model.WpTerm
import org.truthwatchindia.app.data.repository.NewsRepositoryImpl
import org.truthwatchindia.app.domain.repository.NewsRepository
import retrofit2.Response

class FakeBookmarkDao : BookmarkDao {
    private val storage = mutableMapOf<Long, BookmarkEntity>()
    private val flow = MutableStateFlow<List<BookmarkEntity>>(emptyList())

    private fun emit() {
        flow.value = storage.values.sortedByDescending { it.bookmarkedAt }
    }

    override suspend fun insertBookmark(bookmark: BookmarkEntity): Long {
        storage[bookmark.postId] = bookmark
        emit()
        return bookmark.postId
    }

    override suspend fun deleteBookmark(postId: Long): Int {
        val removed = storage.remove(postId)
        if (removed != null) {
            emit()
            return 1
        }
        return 0
    }

    override fun getAllBookmarks(): Flow<List<BookmarkEntity>> = flow

    override fun isBookmarked(postId: Long): Flow<Boolean> =
        flow.map { list -> list.any { it.postId == postId } }

    override suspend fun isBookmarkedSync(postId: Long): Boolean =
        storage.containsKey(postId)

    override suspend fun getBookmarkById(postId: Long): BookmarkEntity? =
        storage[postId]

    override suspend fun clearAllBookmarks(): Int {
        val size = storage.size
        storage.clear()
        emit()
        return size
    }
}

class FakeArticleCacheDao : ArticleCacheDao {
    private val storage = mutableListOf<CachedArticleEntity>()
    private val flow = MutableStateFlow<List<CachedArticleEntity>>(emptyList())

    private fun emit() {
        flow.value = storage.toList()
    }

    override suspend fun insertArticles(articles: List<CachedArticleEntity>) {
        // Replace matching postIds
        articles.forEach { newArticle ->
            storage.removeAll { it.postId == newArticle.postId }
            storage.add(newArticle)
        }
        emit()
    }

    override fun getArticlesBySection(section: String): Flow<List<CachedArticleEntity>> =
        flow.map { list -> list.filter { it.section == section } }

    override suspend fun getArticleById(postId: Long): CachedArticleEntity? =
        storage.firstOrNull { it.postId == postId }

    override suspend fun clearSection(section: String): Int {
        val before = storage.size
        storage.removeAll { it.section == section }
        emit()
        return before - storage.size
    }

    override suspend fun clearOldCache(threshold: Long): Int {
        val before = storage.size
        storage.removeAll { it.cachedAt < threshold }
        emit()
        return before - storage.size
    }

    override suspend fun clearAllCache(): Int {
        val before = storage.size
        storage.clear()
        emit()
        return before
    }
}

class NewsRepositoryTest {

    private lateinit var fakeBookmarkDao: FakeBookmarkDao
    private lateinit var fakeCacheDao: FakeArticleCacheDao
    private lateinit var repository: NewsRepository

    @Before
    fun setup() {
        fakeBookmarkDao = FakeBookmarkDao()
        fakeCacheDao = FakeArticleCacheDao()

        val dummyApi = object : WordPressApiService {
            override suspend fun getPosts(perPage: Int, page: Int, categories: Long?, search: String?, embed: String?) =
                Response.success(emptyList<WpPost>())

            override suspend fun getLatestHeadlines(perPage: Int, embed: String?) =
                Response.success(emptyList<WpPost>())

            override suspend fun getPost(id: Long, embed: String?) = Response.success(
                WpPost(id = id, date = "2026-11-04T12:00:00", title = WpRendered("शीर्षक $id"), content = WpRendered("सामग्री"))
            )

            override suspend fun getCategories(perPage: Int, orderby: String, order: String) =
                Response.success(emptyList<WpCategory>())

            override suspend fun getPage(id: Long) = Response.success(
                WpPage(id = id, title = WpRendered("पेज"), content = WpRendered("विवरण"))
            )

            override suspend fun submitFeedback(formId: Long, name: String, email: String, subject: String, message: String, unitTag: String) =
                Response.success(Cf7Response(status = "mail_sent", message = "धन्यवाद! संदेश भेजा गया।"))
        }

        repository = NewsRepositoryImpl(
            api = dummyApi,
            bookmarkDao = fakeBookmarkDao,
            articleCacheDao = fakeCacheDao
        )
    }

    @Test
    fun testBookmarkSaveRetrieveAndDeleteWorkflow() = runBlocking {
        val samplePost = WpPost(
            id = 555,
            date = "2026-11-05T14:00:00",
            title = WpRendered("अभिषेक त्रिपाठी की विशेष रिपोर्ट"),
            content = WpRendered("<p>पूर्ण विवरण</p>"),
            excerpt = WpRendered("<p>संक्षिप्त विवरण</p>"),
            link = "https://truthwatchindia.org/special-555",
            embedded = WpEmbedded(
                author = listOf(WpAuthor(id = 1, name = "अभिषेक त्रिपाठी", slug = "at")),
                featuredMedia = listOf(WpMedia(id = 10, sourceUrl = "https://truthwatchindia.org/image555.png")),
                terms = listOf(listOf(WpTerm(id = 7, name = "उत्तर प्रदेश", slug = "up")))
            )
        )

        assertFalse(repository.isBookmarkedSync(555))

        // 1. Toggle to Save
        val isNowSaved = repository.toggleBookmark(samplePost)
        assertTrue(isNowSaved)
        assertTrue(repository.isBookmarkedSync(555))

        // 2. Retrieve All
        val bookmarks = repository.getAllBookmarks().first()
        assertEquals(1, bookmarks.size)
        assertEquals("अभिषेक त्रिपाठी की विशेष रिपोर्ट", bookmarks[0].title)
        assertEquals("उत्तर प्रदेश", bookmarks[0].category)
        assertEquals("अभिषेक त्रिपाठी", bookmarks[0].author)

        // 3. Duplicate Handling (inserting again updates cleanly)
        val duplicateBookmark = bookmarks[0].copy(title = "अद्यतन शीर्षक")
        repository.saveBookmark(duplicateBookmark)
        val updatedBookmarks = repository.getAllBookmarks().first()
        assertEquals(1, updatedBookmarks.size)
        assertEquals("अद्यतन शीर्षक", updatedBookmarks[0].title)

        // 4. Toggle to Delete
        val isNowDeleted = repository.toggleBookmark(samplePost)
        assertFalse(isNowDeleted)
        assertFalse(repository.isBookmarkedSync(555))
        assertTrue(repository.getAllBookmarks().first().isEmpty())
    }

    @Test
    fun testCacheArticlesAndRetrieval() = runBlocking {
        val posts = listOf(
            WpPost(
                id = 1001,
                date = "2026-11-06T10:00:00",
                title = WpRendered("ताजा खबर 1"),
                content = WpRendered("सामग्री 1"),
                excerpt = WpRendered("संक्षेप 1"),
                link = "https://truthwatchindia.org/1001"
            )
        )

        repository.cacheArticles("featured", posts)

        val cachedList = repository.getCachedArticles("featured").first()
        assertEquals(1, cachedList.size)
        assertEquals("ताजा खबर 1", cachedList[0].title)

        val retrievedPost = repository.getCachedArticleById(1001)
        assertNotNull(retrievedPost)
        assertEquals("ताजा खबर 1", retrievedPost?.title)
    }

    @Test
    fun testContactFormSubmissionSuccess() = runBlocking {
        val result = repository.submitContactForm(
            name = "राहुल शर्मा",
            email = "rahul@example.com",
            subject = "समाचार सुझाव",
            message = "लखनऊ में विकास कार्यों पर रिपोर्ट"
        )
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun testClearArticleCache() = runBlocking {
        val posts = listOf(
            WpPost(id = 2001, title = WpRendered("खबर 2001"))
        )
        repository.cacheArticles("breaking", posts)
        assertEquals(1, repository.getCachedArticles("breaking").first().size)

        val cleared = repository.clearArticleCache()
        assertEquals(1, cleared)
        assertTrue(repository.getCachedArticles("breaking").first().isEmpty())
    }
}
