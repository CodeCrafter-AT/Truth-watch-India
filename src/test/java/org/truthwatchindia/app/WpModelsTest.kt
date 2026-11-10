package org.truthwatchindia.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.truthwatchindia.app.data.model.WpAuthor
import org.truthwatchindia.app.data.model.WpEmbedded
import org.truthwatchindia.app.data.model.WpMedia
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.data.model.WpRendered
import org.truthwatchindia.app.data.model.WpTerm

class WpModelsTest {

    @Test
    fun testCleanHtmlUnescapesEntitiesAndStripsTags() {
        val raw = "<span>लखनऊ में &quot;अखिलेश यादव&quot; के खिलाफ &amp; &#8216;बॉयकॉट&#8217;</span>"
        val cleaned = WpPost.cleanHtml(raw)
        assertEquals("लखनऊ में \"अखिलेश यादव\" के खिलाफ & ‘बॉयकॉट’", cleaned)
    }

    @Test
    fun testFormattedDateConvertsToHindiFormat() {
        val post = WpPost(
            id = 8802,
            date = "2026-11-04T12:46:16",
            title = WpRendered("शीर्षक"),
            content = WpRendered("<p>सामग्री</p>")
        )
        val formatted = post.getFormattedDate()
        assertTrue("Formatted date should contain 04 and 2026", formatted.contains("04") && formatted.contains("2026"))
        assertTrue("Formatted date should contain नवंबर", formatted.contains("नवंबर"))
    }

    @Test
    fun testEmbeddedExtractionForAuthorMediaCategory() {
        val post = WpPost(
            id = 101,
            date = "2026-11-04T10:00:00",
            title = WpRendered("परीक्षण"),
            embedded = WpEmbedded(
                author = listOf(WpAuthor(id = 2, name = "अभिषेक त्रिपाठी", slug = "abhishektripathi")),
                featuredMedia = listOf(WpMedia(id = 55, sourceUrl = "https://truthwatchindia.org/wp-content/uploads/2026/11/lio.webp")),
                terms = listOf(listOf(WpTerm(id = 3, name = "लखनऊ", slug = "lucknow")))
            )
        )

        assertEquals("अभिषेक त्रिपाठी", post.getAuthorName())
        assertEquals("https://truthwatchindia.org/wp-content/uploads/2026/11/lio.webp", post.getFeaturedImageUrl())
        assertEquals("लखनऊ", post.getCategoryName())
    }
}
