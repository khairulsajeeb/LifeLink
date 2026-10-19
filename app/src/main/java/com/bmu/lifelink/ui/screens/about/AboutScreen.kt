package com.bmu.lifelink.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmu.lifelink.ui.components.EmergencyCallout
import com.bmu.lifelink.ui.components.LifeLinkButton
import com.bmu.lifelink.ui.components.PrivacyNoticeCard
import com.bmu.lifelink.ui.theme.*

@Composable
fun AboutScreen(
    onRequestBlood: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Heading
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(LifeLinkRedBg)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(LifeLinkRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ABOUT LIFELINK",
                style = MaterialTheme.typography.labelSmall,
                color = LifeLinkRed,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Good things happen\nwhen we show up.",
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "LifeLink connects students who need blood with the people in our university community who are willing to help.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Our Purpose Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "♥",
                    color = LifeLinkRed,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "OUR PURPOSE",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A caring community, connected in time.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Finding a donor during an emergency can be overwhelming. LifeLink gives BMU Hall students a clear way to request help, while our administrators coordinate privately with registered donors.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3 Process Steps
        Text(
            text = "HOW IT WORKS",
            style = MaterialTheme.typography.labelSmall,
            color = LifeLinkRed,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        StepCard(
            stepNumber = "01",
            title = "Verify your student ID",
            description = "Enter your 8-digit university student ID. We check that it follows the required year and format before accepting a request."
        )

        Spacer(modifier = Modifier.height(12.dp))

        StepCard(
            stepNumber = "02",
            title = "Tell us what’s needed",
            description = "Share the blood group, hospital, contact details and urgency so our administrator can understand the situation."
        )

        Spacer(modifier = Modifier.height(12.dp))

        StepCard(
            stepNumber = "03",
            title = "Our team follows up",
            description = "An administrator reviews your request and coordinates directly. Donor details are never listed publicly."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy note
        PrivacyNoticeCard(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency medical note
        EmergencyCallout(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(28.dp))

        // Bottom CTA
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PaleBlushPanel),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "READY TO GET STARTED?",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Request the support you need.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                LifeLinkButton(
                    text = "Request blood",
                    onClick = onRequestBlood,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Built with care for the BMU Hall community.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Administrator console",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onAdminClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun StepCard(
    stepNumber: String,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LifeLinkRedBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
