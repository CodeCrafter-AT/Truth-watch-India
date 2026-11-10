package org.truthwatchindia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val postId: Long,
    val title: String,
    val content: String,
    val excerpt: String,
    val author: String,
    val date: String,
    val category: String,
    val imageUrl: String?,
    val link: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_articles")
data class CachedArticleEntity(
    @PrimaryKey
    val postId: Long,
    val title: String,
    val content: String,
    val excerpt: String,
    val author: String,
    val date: String,
    val category: String,
    val imageUrl: String?,
    val link: String,
    val section: String,
    val cachedAt: Long = System.currentTimeMillis()
)
