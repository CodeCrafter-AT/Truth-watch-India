package org.truthwatchindia.app.domain.model

data class Article(
    val id: Long,
    val title: String,
    val content: String,
    val excerpt: String,
    val author: String,
    val date: String,
    val category: String,
    val imageUrl: String?,
    val link: String,
    val isBookmarked: Boolean = false
)
