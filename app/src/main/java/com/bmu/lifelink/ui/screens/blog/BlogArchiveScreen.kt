package com.bmu.lifelink.ui.screens.blog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bmu.lifelink.data.models.BlogPost
import com.bmu.lifelink.ui.components.LifeLinkButton
import com.bmu.lifelink.ui.theme.*

@Composable
fun BlogArchiveScreen(
    onSelectStory: (String) -> Unit,
    viewModel: BlogViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val featured = posts.firstOrNull()
    val remaining = if (posts.size > 1) posts.subList(1, posts.size) else emptyList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text(
                    text = "COMMUNITY STORIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes from our community",
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reflections, donation guidance, and stories from the BMU Students Platform network.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        // Featured Story
        if (featured != null) {
            item {
                FeaturedStoryCard(
                    post = featured,
                    onClick = { onSelectStory(featured.id) }
                )
            }
        }

        // Remaining Stories
        if (remaining.isNotEmpty()) {
            item {
                Text(
                    text = "ALL STORIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(remaining, key = { it.id }) { post ->
                ArchiveStoryCard(
                    post = post,
                    onClick = { onSelectStory(post.id) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedStoryCard(
    post: BlogPost,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(PaleBlushPanel)
            ) {
                if (post.image.isNotBlank()) {
                    AsyncImage(
                        model = post.image,
                        contentDescription = post.imageAlt,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "FEATURED STORY · ${post.category.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.excerpt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${post.date} · ${post.readTime}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))
                LifeLinkButton(
                    text = "Read the story",
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ArchiveStoryCard(
    post: BlogPost,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PaleBlushPanel)
            ) {
                if (post.image.isNotBlank()) {
                    AsyncImage(
                        model = post.image,
                        contentDescription = post.imageAlt,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${post.category} · ${post.date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.excerpt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Read story ↗",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
