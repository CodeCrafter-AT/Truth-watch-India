package org.truthwatchindia.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.truthwatchindia.app.data.api.Cf7Response
import org.truthwatchindia.app.data.api.WordPressApiService
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpPage
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.data.model.WpRendered
import org.truthwatchindia.app.data.repository.NewsRepositoryImpl
import org.truthwatchindia.app.ui.screens.detail.ArticleDetailViewModel
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeBookmarkDao: FakeBookmarkDao
    private lateinit var fakeCacheDao: FakeArticleCacheDao

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeBookmarkDao = FakeBookmarkDao()
        fakeCacheDao = FakeArticleCacheDao()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadArticleSuccess() = runTest {
        val samplePost = WpPost(
            id = 5555,
            date = "2026-11-06T15:30:00",
            title = WpRendered("उत्तर प्रदेश विकास योजना"),
            content = WpRendered("<p>विस्तृत विवरण</p>"),
            link = "https://truthwatchindia.org/5555"
        )

        val api = object : WordPressApiService {
            override suspend fun getPosts(perPage: Int, page: Int, categories: Long?, search: String?, embed: String?) =
                Response.success(emptyList<WpPost>())
            override suspend fun getLatestHeadlines(perPage: Int, embed: String?) = Response.success(emptyList<WpPost>())
            override suspend fun getPost(id: Long, embed: String?) = Response.success(samplePost)
            override suspend fun getCategories(perPage: Int, orderby: String, order: String) = Response.success(emptyList<WpCategory>())
            override suspend fun getPage(id: Long) = Response.success(WpPage(id = id))
            override suspend fun submitFeedback(formId: Long, name: String, email: String, subject: String, message: String, unitTag: String) =
                Response.success(Cf7Response(status = "mail_sent"))
        }

        val repository = NewsRepositoryImpl(api, fakeBookmarkDao, fakeCacheDao)
        val viewModel = ArticleDetailViewModel(5555L, repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isOfflineCopy)
        assertNotNull(state.post)
        assertEquals(5555L, state.post?.id)
        assertEquals("उत्तर प्रदेश विकास योजना", state.post?.getCleanTitle())
    }

    @Test
    fun testLoadArticleOfflineFallbackWhenNetworkFails() = runTest {
        val cachedPost = WpPost(
            id = 7777,
            date = "2026-11-05T10:00:00",
            title = WpRendered("ऑफ़लाइन सहेजा गया समाचार"),
            content = WpRendered("सहेजी गई सामग्री"),
            link = "https://truthwatchindia.org/7777"
        )

        val failingApi = object : WordPressApiService {
            override suspend fun getPosts(perPage: Int, page: Int, categories: Long?, search: String?, embed: String?) =
                throw IOException("Network offline")
            override suspend fun getLatestHeadlines(perPage: Int, embed: String?) = throw IOException("Network offline")
            override suspend fun getPost(id: Long, embed: String?) = throw IOException("Network offline")
            override suspend fun getCategories(perPage: Int, orderby: String, order: String) = throw IOException("Network offline")
            override suspend fun getPage(id: Long) = throw IOException("Network offline")
            override suspend fun submitFeedback(formId: Long, name: String, email: String, subject: String, message: String, unitTag: String) =
                throw IOException("Network offline")
        }

        val repository = NewsRepositoryImpl(failingApi, fakeBookmarkDao, fakeCacheDao)
        // Pre-cache article
        repository.cacheArticles("featured", listOf(cachedPost))

        val viewModel = ArticleDetailViewModel(7777L, repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isOfflineCopy)
        assertNotNull(state.post)
        assertEquals(7777L, state.post?.id)
        assertEquals("ऑफ़लाइन सहेजा गया समाचार", state.post?.getCleanTitle())
    }

    @Test
    fun testToggleBookmarkUpdatesState() = runTest {
        val samplePost = WpPost(
            id = 9999,
            title = WpRendered("बुकमार्क टेस्ट"),
            content = WpRendered("सामग्री"),
            link = "https://truthwatchindia.org/9999"
        )

        val api = object : WordPressApiService {
            override suspend fun getPosts(perPage: Int, page: Int, categories: Long?, search: String?, embed: String?) =
                Response.success(emptyList<WpPost>())
            override suspend fun getLatestHeadlines(perPage: Int, embed: String?) = Response.success(emptyList<WpPost>())
            override suspend fun getPost(id: Long, embed: String?) = Response.success(samplePost)
            override suspend fun getCategories(perPage: Int, orderby: String, order: String) = Response.success(emptyList<WpCategory>())
            override suspend fun getPage(id: Long) = Response.success(WpPage(id = id))
            override suspend fun submitFeedback(formId: Long, name: String, email: String, subject: String, message: String, unitTag: String) =
                Response.success(Cf7Response(status = "mail_sent"))
        }

        val repository = NewsRepositoryImpl(api, fakeBookmarkDao, fakeCacheDao)
        val viewModel = ArticleDetailViewModel(9999L, repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isBookmarked)

        viewModel.toggleBookmark()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isBookmarked)

        viewModel.toggleBookmark()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isBookmarked)
    }
}
