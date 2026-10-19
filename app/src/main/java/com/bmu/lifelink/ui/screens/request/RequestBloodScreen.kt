package com.bmu.lifelink.ui.screens.request

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bmu.lifelink.data.models.BloodGroup
import com.bmu.lifelink.data.models.UrgencyOption
import com.bmu.lifelink.ui.components.*
import com.bmu.lifelink.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBloodScreen(
    viewModel: RequestBloodViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var bloodDropdownExpanded by remember { mutableStateOf(false) }
    var unitsDropdownExpanded by remember { mutableStateOf(false) }
    var urgencyDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Sidebar / Header
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
                text = "PRIVATE REQUEST FORM",
                style = MaterialTheme.typography.labelSmall,
                color = LifeLinkRed,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "We’re here to help.",
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tell us a little about what you need. Your request will be sent securely to the LifeLink administrator.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency medical note
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
                    text = "BLOOD SUPPORT",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Request details",
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

                // Section 2: About the Request
                Text(
                    text = "02 · About the request",
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

                // Units Needed dropdown
                Column {
                    Text(
                        text = "Units needed",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = unitsDropdownExpanded,
                        onExpandedChange = { unitsDropdownExpanded = !unitsDropdownExpanded }
                    ) {
                        val unitsDisplay = if (state.units == "4") "4+ units" else "${state.units} unit${if (state.units == "1") "" else "s"}"
                        OutlinedTextField(
                            value = unitsDisplay,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitsDropdownExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = LifeLinkRed,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = unitsDropdownExpanded,
                            onDismissRequest = { unitsDropdownExpanded = false }
                        ) {
                            listOf("1", "2", "3", "4").forEach { u ->
                                val label = if (u == "4") "4+ units" else "$u unit${if (u == "1") "" else "s"}"
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.onUnitsChange(u)
                                        unitsDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LifeLinkTextField(
                    value = state.hospital,
                    onValueChange = viewModel::onHospitalChange,
                    label = "Hospital / location",
                    placeholder = "Hospital or area",
                    isRequired = true,
                    error = state.validation.hospitalError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Urgency dropdown
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "When is it needed?",
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
                        expanded = urgencyDropdownExpanded,
                        onExpandedChange = { urgencyDropdownExpanded = !urgencyDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.urgency.ifBlank { "Select urgency" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = urgencyDropdownExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedBorderColor = LifeLinkRed,
                                unfocusedBorderColor = if (state.validation.urgencyError != null) ErrorRed else BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = urgencyDropdownExpanded,
                            onDismissRequest = { urgencyDropdownExpanded = false }
                        ) {
                            UrgencyOption.all.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        viewModel.onUrgencyChange(opt)
                                        urgencyDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (state.validation.urgencyError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.validation.urgencyError!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ErrorRed,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LifeLinkTextField(
                    value = state.details,
                    onValueChange = viewModel::onDetailsChange,
                    label = "Additional details",
                    placeholder = "Anything else the coordinator should know? (optional)",
                    maxLines = 3,
                    error = state.validation.detailsError,
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
                        text = "I confirm this is a genuine request for a BMU Hall student, and I agree that the LifeLink administrator may contact me about it. *",
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

                Spacer(modifier = Modifier.height(10.dp))

                // Optional Share Contact Checkbox (unchecked by default)
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onShareContactChange(!state.shareContactOnCard) }
                ) {
                    Checkbox(
                        checked = state.shareContactOnCard,
                        onCheckedChange = viewModel::onShareContactChange,
                        colors = CheckboxDefaults.colors(checkedColor = LifeLinkRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Include my name and phone number on the downloadable card so people I share it with can contact me. Optional; unchecked by default.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
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

                // If submitted, show photo card download/share action
                if (state.submittedRequest != null) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PaleBlushPanel),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            LifeLinkOutlineButton(
                                text = "Download / Share request photo card",
                                onClick = {
                                    RequestCardGenerator.shareRequestCard(
                                        context = context,
                                        request = state.submittedRequest!!,
                                        includeContact = state.shareContactOnCard
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = LifeLinkRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (state.shareContactOnCard) {
                                    "This card includes your name and phone number because you opted in. Your student ID is never included."
                                } else {
                                    "The card includes your request details only. Your name, phone number, and student ID are not included."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Submit Button
                LifeLinkButton(
                    text = "Send private request",
                    onClick = viewModel::submitRequest,
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
                        text = "♢ Your request is private and visible to administrators only.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
