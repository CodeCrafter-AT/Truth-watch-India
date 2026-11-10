package org.truthwatchindia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.truthwatchindia.app.data.local.entity.BookmarkEntity
import org.truthwatchindia.app.data.local.entity.CachedArticleEntity

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE postId = :postId")
    suspend fun deleteBookmark(postId: Long): Int

    @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE postId = :postId)")
    fun isBookmarked(postId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE postId = :postId)")
    suspend fun isBookmarkedSync(postId: Long): Boolean

    @Query("SELECT * FROM bookmarks WHERE postId = :postId LIMIT 1")
    suspend fun getBookmarkById(postId: Long): BookmarkEntity?

    @Query("DELETE FROM bookmarks")
    suspend fun clearAllBookmarks(): Int
}

@Dao
interface ArticleCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<CachedArticleEntity>)

    @Query("SELECT * FROM cached_articles WHERE section = :section ORDER BY cachedAt DESC")
    fun getArticlesBySection(section: String): Flow<List<CachedArticleEntity>>

    @Query("SELECT * FROM cached_articles WHERE postId = :postId LIMIT 1")
    suspend fun getArticleById(postId: Long): CachedArticleEntity?

    @Query("DELETE FROM cached_articles WHERE section = :section")
    suspend fun clearSection(section: String): Int

    @Query("DELETE FROM cached_articles WHERE cachedAt < :threshold")
    suspend fun clearOldCache(threshold: Long): Int

    @Query("DELETE FROM cached_articles")
    suspend fun clearAllCache(): Int
}
