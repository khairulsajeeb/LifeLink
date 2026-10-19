package com.bmu.lifelink.ui.screens.blog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bmu.lifelink.ui.components.LifeLinkOutlineButton
import com.bmu.lifelink.ui.theme.*

@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBack: () -> Unit,
    onOfferToDonate: () -> Unit,
    viewModel: BlogViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val post = posts.find { it.id == articleId }
    val scrollState = rememberScrollState()

    if (post == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(WarmBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Story not found.",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                LifeLinkOutlineButton(
                    text = "← Back to all stories",
                    onClick = onBack
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Back link
        TextButton(
            onClick = onBack,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = LifeLinkRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "All stories",
                color = LifeLinkRed,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category kicker
        Text(
            text = post.category.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = LifeLinkRed,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Byline
        Text(
            text = "${post.date} · ${post.readTime}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Cover Image
        if (post.image.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PaleBlushPanel)
            ) {
                AsyncImage(
                    model = post.image,
                    contentDescription = post.imageAlt,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Paragraphs
        post.paragraphs.forEach { paragraph ->
            Text(
                text = paragraph,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                lineHeight = 26.sp,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Endnote Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PaleBlushPanel),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIFELINK · BMU STUDENTS PLATFORM",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeLinkRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Help when it matters most.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onOfferToDonate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LifeLinkRed,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Offer to donate ↗",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
