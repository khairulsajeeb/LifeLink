package com.bmu.lifelink.data.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

data class BlogPost(
    val id: String = "",
    val category: String = "Community",
    val date: String = "",
    val readTime: String = "3 min read",
    val title: String = "",
    val excerpt: String = "",
    val image: String = "",
    val imageAlt: String = "",
    val paragraphs: List<String> = emptyList(),
    val publishedAt: Long = 0L
) {
    companion object {
        val starterPosts: List<BlogPost> = listOf(
            BlogPost(
                id = "small-acts-campus",
                category = "Community",
                date = "October 16, 2026",
                readTime = "3 min read",
                title = "A small act can change the shape of someone’s day",
                excerpt = "A campus donor network is more than a list of names. It is a way to make help feel closer when a family needs it most.",
                image = "https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?auto=format&fit=crop&w=1500&q=85",
                imageAlt = "Hands joined together in a gesture of community support",
                paragraphs = listOf(
                    "When someone asks for blood, the first feeling is often urgency. The next can be uncertainty: who should we call, and where do we begin? A local network gives that moment a starting point.",
                    "LifeLink brings students and volunteers into one coordinated community. Administrators review requests privately and contact registered donors directly, so personal details are not posted for everyone to see.",
                    "Offering to donate is a personal decision, and every donation depends on eligibility and professional guidance. But letting the network know you are willing to be contacted can help a coordinator find the right next step."
                ),
                publishedAt = 1792195200000L // Approx Oct 16 2026
            ),
            BlogPost(
                id = "before-donation",
                category = "Practical guide",
                date = "October 11, 2026",
                readTime = "4 min read",
                title = "Getting ready to donate: start with the people who know best",
                excerpt = "A calm, useful checklist begins with your local blood service. Here is what to ask before making a donation appointment.",
                image = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=85",
                imageAlt = "Healthcare professional speaking with a patient",
                paragraphs = listOf(
                    "Donation guidance can vary by location and by person. Before you go, check with the blood collection service or a healthcare professional about eligibility, identification, appointment details, and anything you should do beforehand.",
                    "Plan enough time for the visit and follow the instructions the service gives you. If you feel unwell or have a question about your health, contact them before your appointment rather than relying on a general checklist.",
                    "After donation, use the collection team’s aftercare guidance. Their advice is specific to the donation process and should take priority over online tips."
                ),
                publishedAt = 1791763200000L // Approx Oct 11 2026
            ),
            BlogPost(
                id = "asking-for-help",
                category = "Student life",
                date = "October 6, 2026",
                readTime = "3 min read",
                title = "When a friend needs help, clear information matters",
                excerpt = "A few accurate details help coordinators understand a request and follow up without putting private information on public display.",
                image = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1200&q=85",
                imageAlt = "Friends talking together outdoors",
                paragraphs = listOf(
                    "In a stressful moment, it can be hard to know what to share. Start with the information a coordinator needs to understand the request: the blood group if known, where support is needed, when it is needed, and a reliable contact number.",
                    "Use a private request channel instead of posting someone else’s contact details in a public group. Ask the person or their family before sharing personal information, and make sure the request is genuine and current.",
                    "A community network can help people connect, but it is not an emergency service. For urgent medical needs, contact the hospital or local emergency services directly."
                ),
                publishedAt = 1791331200000L // Approx Oct 6 2026
            ),
            BlogPost(
                id = "donor-privacy",
                category = "How it works",
                date = "October 2, 2026",
                readTime = "3 min read",
                title = "Why donor details belong in trusted hands",
                excerpt = "Privacy is part of making a community directory useful: contact details should reach the people coordinating a request, not the whole internet.",
                image = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=1200&q=85",
                imageAlt = "Community volunteer smiling during a conversation",
                paragraphs = listOf(
                    "A donor directory works best when people can trust how it is handled. Contact information can be sensitive, so LifeLink keeps donor records available to authorized administrators rather than displaying them publicly.",
                    "Each donor application is reviewed before it becomes part of the directory. The administrator can confirm the details and decide whether the application is ready to approve.",
                    "If your phone number or availability changes, let the administrator know so the directory stays accurate and coordinators can respect your preferences."
                ),
                publishedAt = 1790985600000L // Approx Oct 2 2026
            )
        )

        fun fromFirebase(id: String, map: Map<String, Any?>): BlogPost {
            val title = map["title"] as? String ?: "Untitled post"
            val body = map["body"] as? String ?: ""
            val coverUrl = map["coverUrl"] as? String ?: ""
            val createdAt = when (val c = map["createdAt"]) {
                is Long -> c
                is Number -> c.toLong()
                else -> 0L
            }
            val paragraphs = body.split(Regex("\\n\\s*\\n"))
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val words = body.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
            val readTime = "${max(1, ceil(words / 200.0).toInt())} min read"
            val dateStr = if (createdAt > 0) {
                SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date(createdAt))
            } else {
                "Recently"
            }
            val firstPara = paragraphs.firstOrNull() ?: ""
            val excerpt = if (firstPara.length > 180) "${firstPara.substring(0, 177)}…" else firstPara

            return BlogPost(
                id = id,
                category = "Community",
                date = dateStr,
                readTime = readTime,
                title = title,
                excerpt = excerpt,
                image = coverUrl,
                imageAlt = title,
                paragraphs = paragraphs,
                publishedAt = createdAt
            )
        }

        fun mergeAndSort(publishedPosts: List<BlogPost>): List<BlogPost> {
            val combined = publishedPosts + starterPosts
            return combined.sortedByDescending { it.publishedAt }
        }
    }
}
