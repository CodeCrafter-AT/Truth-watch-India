package org.truthwatchindia.app.data.repository

import kotlinx.coroutines.flow.Flow
import org.truthwatchindia.app.data.api.WordPressApiService
import org.truthwatchindia.app.data.local.dao.ArticleCacheDao
import org.truthwatchindia.app.data.local.dao.BookmarkDao
import org.truthwatchindia.app.data.local.entity.BookmarkEntity
import org.truthwatchindia.app.data.local.entity.CachedArticleEntity
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpPage
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val api: WordPressApiService,
    private val bookmarkDao: BookmarkDao,
    private val articleCacheDao: ArticleCacheDao
) : NewsRepository {

    override suspend fun getPosts(
        page: Int,
        perPage: Int,
        categoryId: Long?
    ): Result<List<WpPost>> {
        return try {
            val response = api.getPosts(page = page, perPage = perPage, categories = categoryId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("Failed to fetch posts: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostById(postId: Long): Result<WpPost> {
        return try {
            val response = api.getPost(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Post not found: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(perPage: Int): Result<List<WpCategory>> {
        return try {
            val response = api.getCategories(perPage = perPage)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("Failed to fetch categories: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchPosts(
        query: String,
        page: Int,
        perPage: Int
    ): Result<List<WpPost>> {
        return try {
            val response = api.getPosts(search = query, page = page, perPage = perPage)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("Search failed: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPage(pageId: Long): Result<WpPage> {
        return try {
            val response = api.getPage(pageId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Page not found: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitContactForm(
        name: String,
        email: String,
        subject: String,
        message: String
    ): Result<Boolean> {
        return try {
            val response = api.submitFeedback(
                formId = 6,
                name = name,
                email = email,
                subject = subject,
                message = message
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "mail_sent") {
                    Result.success(true)
                } else {
                    // CF7 returned, but status was e.g. validation_failed
                    Result.failure(Exception(body?.message ?: "संदेश भेजने में विफल"))
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Bookmarks (Local SSOT)
    override fun getAllBookmarks(): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getAllBookmarks()
    }

    override fun isBookmarked(postId: Long): Flow<Boolean> {
        return bookmarkDao.isBookmarked(postId)
    }

    override suspend fun isBookmarkedSync(postId: Long): Boolean {
        return bookmarkDao.isBookmarkedSync(postId)
    }

    override suspend fun toggleBookmark(post: WpPost): Boolean {
        val currentlyBookmarked = bookmarkDao.isBookmarkedSync(post.id)
        if (currentlyBookmarked) {
            bookmarkDao.deleteBookmark(post.id)
            return false
        } else {
            val entity = BookmarkEntity(
                postId = post.id,
                title = post.getCleanTitle(),
                content = post.content.rendered,
                excerpt = post.getCleanExcerpt(),
                author = post.getAuthorName(),
                date = post.getFormattedDate(),
                category = post.getCategoryName(),
                imageUrl = post.getFeaturedImageUrl(),
                link = post.link,
                bookmarkedAt = System.currentTimeMillis()
            )
            bookmarkDao.insertBookmark(entity)
            return true
        }
    }

    override suspend fun saveBookmark(entity: BookmarkEntity) {
        bookmarkDao.insertBookmark(entity)
    }

    override suspend fun removeBookmark(postId: Long) {
        bookmarkDao.deleteBookmark(postId)
    }

    // Offline Article Caching
    override fun getCachedArticles(section: String): Flow<List<CachedArticleEntity>> {
        return articleCacheDao.getArticlesBySection(section)
    }

    override suspend fun cacheArticles(section: String, posts: List<WpPost>) {
        val entities = posts.map { post ->
            CachedArticleEntity(
                postId = post.id,
                title = post.getCleanTitle(),
                content = post.content.rendered,
                excerpt = post.getCleanExcerpt(),
                author = post.getAuthorName(),
                date = post.getFormattedDate(),
                category = post.getCategoryName(),
                imageUrl = post.getFeaturedImageUrl(),
                link = post.link,
                section = section,
                cachedAt = System.currentTimeMillis()
            )
        }
        articleCacheDao.insertArticles(entities)
    }

    override suspend fun getCachedArticleById(postId: Long): CachedArticleEntity? {
        return articleCacheDao.getArticleById(postId)
    }

    override suspend fun clearArticleCache(): Int {
        return articleCacheDao.clearAllCache()
    }
}
