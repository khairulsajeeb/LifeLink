package com.bmu.lifelink.ui.screens.donor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bmu.lifelink.data.models.BloodGroup
import com.bmu.lifelink.ui.components.EmergencyCallout
import com.bmu.lifelink.ui.components.LifeLinkButton
import com.bmu.lifelink.ui.components.LifeLinkTextField
import com.bmu.lifelink.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorApplicationScreen(
    viewModel: DonorApplicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var bloodDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
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
                text = "DONOR APPLICATION",
                style = MaterialTheme.typography.labelSmall,
                color = LifeLinkRed,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Give someone\nanother chance.",
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Offer to donate blood for the BMU Hall community. A LifeLink administrator will review your details before adding you to the donor directory.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PaleBlushPanel),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = LifeLinkRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Your details stay private",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Your application is visible only to the LifeLink administrator. You will be listed as a donor only after approval.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick reminder
        EmergencyCallout(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        // Form Card
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
                    text = "JOIN THE DONOR NETWORK",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Donor details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Fields marked * are required.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 1: Student Verification
                Text(
                    text = "01 · Student verification",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LifeLinkTextField(
                    value = state.studentId,
                    onValueChange = viewModel::onStudentIdChange,
                    label = "8-digit student ID",
                    placeholder = "e.g. 24123456",
                    isRequired = true,
                    helperText = "The first two digits must represent a year from 2019 through the current year.",
                    error = state.validation.studentIdError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 2: How we can reach you
                Text(
                    text = "02 · How we can reach you",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                LifeLinkTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Your name",
                    placeholder = "Full name",
                    isRequired = true,
                    error = state.validation.nameError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                LifeLinkTextField(
                    value = state.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = "Contact number",
                    placeholder = "A number we can reach",
                    isRequired = true,
                    error = state.validation.phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Blood Group dropdown
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Blood group",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = " *",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = LifeLinkRed
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = bloodDropdownExpanded,
                        onExpandedChange = { bloodDropdownExpanded = !bloodDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.bloodType.ifBlank { "Select a group" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodDropdownExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = LifeLinkRed,
                                unfocusedBorderColor = if (state.validation.bloodTypeError != null) ErrorRed else BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = bloodDropdownExpanded,
                            onDismissRequest = { bloodDropdownExpanded = false }
                        ) {
                            BloodGroup.all.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group) },
                                    onClick = {
                                        viewModel.onBloodTypeChange(group)
                                        bloodDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (state.validation.bloodTypeError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.validation.bloodTypeError!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ErrorRed,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LifeLinkTextField(
                    value = state.location,
                    onValueChange = viewModel::onLocationChange,
                    label = "Location",
                    placeholder = "Area or hall",
                    isRequired = true,
                    error = state.validation.locationError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Required Consent Checkbox
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onConsentChange(!state.consent) }
                ) {
                    Checkbox(
                        checked = state.consent,
                        onCheckedChange = viewModel::onConsentChange,
                        colors = CheckboxDefaults.colors(checkedColor = LifeLinkRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I am willing to be contacted about donating blood, and I agree that the LifeLink administrator may review my details. *",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
                if (state.validation.consentError != null) {
                    Text(
                        text = state.validation.consentError!!,
                        color = ErrorRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 42.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Success or Error Message
                if (state.successMessage != null) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SuccessBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.successMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SuccessGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (state.errorMessage != null) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ErrorBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ErrorRed,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Submit Button
                LifeLinkButton(
                    text = "Submit for review",
                    onClick = viewModel::submitApplication,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "♢ Your donor listing is created only after administrator approval.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
