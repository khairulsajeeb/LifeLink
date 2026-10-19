package com.bmu.lifelink

import com.bmu.lifelink.data.models.BlogPost
import org.junit.Assert.*
import org.junit.Test

class BlogOrderTest {

    @Test
    fun testStarterPostsExist() {
        assertEquals(4, BlogPost.starterPosts.size)
        assertTrue(BlogPost.starterPosts.any { it.id == "small-acts-campus" })
        assertTrue(BlogPost.starterPosts.any { it.id == "before-donation" })
        assertTrue(BlogPost.starterPosts.any { it.id == "asking-for-help" })
        assertTrue(BlogPost.starterPosts.any { it.id == "donor-privacy" })
    }

    @Test
    fun testMergeAndNewestFirstOrdering() {
        val newerAdminPost = BlogPost(
            id = "admin-post-1",
            title = "A Brand New Post",
            body = "This is a new update.",
            image = "https://example.com/image.jpg",
            publishedAt = 1900000000000L // In the future / newer than starter posts
        )

        val olderAdminPost = BlogPost(
            id = "admin-post-old",
            title = "An Older Post",
            body = "This is an older update.",
            image = "",
            publishedAt = 1000000000000L // Older than starter posts
        )

        val merged = BlogPost.mergeAndSort(listOf(newerAdminPost, olderAdminPost))

        // Total count should be 4 starter posts + 2 admin posts = 6
        assertEquals(6, merged.size)

        // First post must be the newest
        assertEquals(newerAdminPost.id, merged.first().id)

        // Last post must be the oldest
        assertEquals(olderAdminPost.id, merged.last().id)

        // Verify strictly descending order
        for (i in 0 until merged.size - 1) {
            assertTrue(merged[i].publishedAt >= merged[i + 1].publishedAt)
        }
    }

    @Test
    fun testMissingOrInvalidImageGracefulHandling() {
        val map = mapOf<String, Any?>(
            "title" to "Post with empty image",
            "body" to "First paragraph.\n\nSecond paragraph.",
            "coverUrl" to "",
            "createdAt" to 1700000000000L
        )

        val post = BlogPost.fromFirebase("test-post", map)
        assertEquals("test-post", post.id)
        assertEquals("Post with empty image", post.title)
        assertEquals("", post.image)
        assertEquals(2, post.paragraphs.size)
        assertEquals("First paragraph.", post.excerpt)
        assertNotNull(post.readTime)
    }
}
