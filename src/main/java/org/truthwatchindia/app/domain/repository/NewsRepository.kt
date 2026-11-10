package org.truthwatchindia.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.truthwatchindia.app.data.local.entity.BookmarkEntity
import org.truthwatchindia.app.data.local.entity.CachedArticleEntity
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpPage
import org.truthwatchindia.app.data.model.WpPost

interface NewsRepository {

    // Remote Posts & Feeds
    suspend fun getPosts(page: Int = 1, perPage: Int = 15, categoryId: Long? = null): Result<List<WpPost>>
    suspend fun getPostById(postId: Long): Result<WpPost>
    suspend fun getCategories(perPage: Int = 50): Result<List<WpCategory>>
    suspend fun searchPosts(query: String, page: Int = 1, perPage: Int = 15): Result<List<WpPost>>
    suspend fun getPage(pageId: Long): Result<WpPage>
    suspend fun submitContactForm(name: String, email: String, subject: String, message: String): Result<Boolean>

    // Bookmarks (Local SSOT)
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    fun isBookmarked(postId: Long): Flow<Boolean>
    suspend fun isBookmarkedSync(postId: Long): Boolean
    suspend fun toggleBookmark(post: WpPost): Boolean
    suspend fun saveBookmark(entity: BookmarkEntity)
    suspend fun removeBookmark(postId: Long)

    // Offline Article Caching
    fun getCachedArticles(section: String): Flow<List<CachedArticleEntity>>
    suspend fun cacheArticles(section: String, posts: List<WpPost>)
    suspend fun getCachedArticleById(postId: Long): CachedArticleEntity?
    suspend fun clearArticleCache(): Int
}
