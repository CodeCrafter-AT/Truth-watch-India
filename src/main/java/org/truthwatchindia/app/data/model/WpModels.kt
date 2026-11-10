package org.truthwatchindia.app.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class WpPost(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("link")
    val link: String = "",
    @SerializedName("title")
    val title: WpRendered = WpRendered(),
    @SerializedName("content")
    val content: WpRendered = WpRendered(),
    @SerializedName("excerpt")
    val excerpt: WpRendered = WpRendered(),
    @SerializedName("categories")
    val categories: List<Long> = emptyList(),
    @SerializedName("featured_media")
    val featuredMedia: Long = 0,
    @SerializedName("_embedded")
    val embedded: WpEmbedded? = null
) {
    fun getFeaturedImageUrl(): String? {
        val mediaList = embedded?.featuredMedia
        if (!mediaList.isNullOrEmpty()) {
            val url = mediaList.firstOrNull()?.sourceUrl
            if (!url.isNullOrBlank()) return url
        }
        // Fallback: check content for first img tag
        val regex = Regex("""<img[^>]+src=["']([^"']+)["']""", RegexOption.IGNORE_CASE)
        val match = regex.find(content.rendered)
        return match?.groupValues?.getOrNull(1)
    }

    fun getAuthorName(): String {
        val authors = embedded?.author
        if (!authors.isNullOrEmpty()) {
            val name = authors.firstOrNull()?.name
            if (!name.isNullOrBlank()) return name
        }
        return "Truth Watch India"
    }

    fun getCategoryName(): String {
        val termsGroup = embedded?.terms
        if (!termsGroup.isNullOrEmpty()) {
            for (terms in termsGroup) {
                val found = terms.firstOrNull { it.name.isNotBlank() }
                if (found != null) return found.name
            }
        }
        return "ताज़ा ख़बर"
    }

    fun getCleanTitle(): String {
        return cleanHtml(title.rendered)
    }

    fun getCleanExcerpt(): String {
        return cleanHtml(excerpt.rendered).take(140)
    }

    fun getFormattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val parsedDate = inputFormat.parse(date) ?: return date
            
            val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
            val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
            val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

            val day = dayFormat.format(parsedDate)
            val monthNum = monthFormat.format(parsedDate).toIntOrNull() ?: 1
            val year = yearFormat.format(parsedDate)

            val hindiMonths = arrayOf(
                "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून",
                "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर"
            )
            val hindiMonth = if (monthNum in 1..12) hindiMonths[monthNum - 1] else ""
            "$day $hindiMonth $year"
        } catch (_: Exception) {
            date.take(10)
        }
    }

    companion object {
        fun cleanHtml(raw: String): String {
            var text = raw.replace(Regex("<[^>]*>"), "")
            text = text.replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#8216;", "‘")
                .replace("&#8217;", "’")
                .replace("&#8220;", "“")
                .replace("&#8221;", "”")
                .replace("&#8211;", "–")
                .replace("&#8212;", "—")
                .replace("&#038;", "&")
                .replace("&#8230;", "…")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
            return text.trim()
        }
    }
}

data class WpRendered(
    @SerializedName("rendered")
    val rendered: String = ""
)

data class WpEmbedded(
    @SerializedName("author")
    val author: List<WpAuthor>? = null,
    @SerializedName("wp:featuredmedia")
    val featuredMedia: List<WpMedia>? = null,
    @SerializedName("wp:term")
    val terms: List<List<WpTerm>>? = null
)

data class WpAuthor(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("avatar_urls")
    val avatarUrls: Map<String, String>? = null
)

data class WpMedia(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("source_url")
    val sourceUrl: String = "",
    @SerializedName("media_details")
    val mediaDetails: WpMediaDetails? = null
)

data class WpMediaDetails(
    @SerializedName("width")
    val width: Int = 0,
    @SerializedName("height")
    val height: Int = 0
)

data class WpTerm(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("count")
    val count: Int? = null
)

data class WpCategory(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("link")
    val link: String = "",
    @SerializedName("parent")
    val parent: Long = 0
)

data class WpPage(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("title")
    val title: WpRendered = WpRendered(),
    @SerializedName("content")
    val content: WpRendered = WpRendered(),
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("link")
    val link: String = ""
)
