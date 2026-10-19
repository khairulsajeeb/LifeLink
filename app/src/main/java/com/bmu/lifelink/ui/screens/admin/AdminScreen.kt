package com.bmu.lifelink.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bmu.lifelink.data.models.*
import com.bmu.lifelink.ui.components.*
import com.bmu.lifelink.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    if (!state.isAuthenticated) {
        AdminLoginView(
            email = state.loginEmail,
            password = state.loginPassword,
            isLoading = state.isLoggingIn,
            error = state.loginError,
            onEmailChange = viewModel::onLoginEmailChange,
            onPasswordChange = viewModel::onLoginPasswordChange,
            onSignIn = viewModel::signIn,
            modifier = modifier
        )
    } else {
        AdminDashboardView(
            state = state,
            viewModel = viewModel,
            modifier = modifier
        )
    }
}

@Composable
private fun AdminLoginView(
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LifeLinkRedBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "♥",
                        color = LifeLinkRed,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "LIFELINK ADMINISTRATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome back.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sign in with your authorized administrator account to manage donors and review requests.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                LifeLinkTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email address",
                    placeholder = "admin@example.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                LifeLinkTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    placeholder = "Your password",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ErrorRed,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                LifeLinkButton(
                    text = "Sign in",
                    onClick = onSignIn,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Admin access is managed through Firebase Authentication.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboardView(
    state: AdminUiState,
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
                .border(1.dp, BorderSubtle)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(LifeLinkRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PRIVATE ADMIN AREA",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeLinkRed,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Good day, coordinator.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            IconButton(
                onClick = viewModel::signOut,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PaleBlushPanel)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign out",
                    tint = LifeLinkRed
                )
            }
        }

        // Stats Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "♥", color = LifeLinkRed, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${state.registeredDonorsCount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Registered donors",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "↗", color = LifeLinkRed, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${state.openRequestsCount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Open requests",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Tabs
        PrimaryTabRow(
            selectedTabIndex = state.activeTab.ordinal,
            containerColor = CardBackground,
            contentColor = LifeLinkRed
        ) {
            Tab(
                selected = state.activeTab == AdminTab.DONORS,
                onClick = { viewModel.setTab(AdminTab.DONORS) },
                text = { Text("Donors (${state.registeredDonorsCount})", fontSize = 12.sp) }
            )
            Tab(
                selected = state.activeTab == AdminTab.APPLICATIONS,
                onClick = { viewModel.setTab(AdminTab.APPLICATIONS) },
                text = { Text("Applications (${state.pendingApplicationsCount})", fontSize = 12.sp) }
            )
            Tab(
                selected = state.activeTab == AdminTab.REQUESTS,
                onClick = { viewModel.setTab(AdminTab.REQUESTS) },
                text = { Text("Requests (${state.totalRequestsCount})", fontSize = 12.sp) }
            )
            Tab(
                selected = state.activeTab == AdminTab.BLOG,
                onClick = { viewModel.setTab(AdminTab.BLOG) },
                text = { Text("Blog (${state.blogPostsCount})", fontSize = 12.sp) }
            )
        }

        // Panel Content
        Box(modifier = Modifier.weight(1f)) {
            when (state.activeTab) {
                AdminTab.DONORS -> DonorsPanel(state, viewModel)
                AdminTab.APPLICATIONS -> ApplicationsPanel(state, viewModel)
                AdminTab.REQUESTS -> RequestsPanel(state, viewModel)
                AdminTab.BLOG -> BlogPanel(state, viewModel)
            }
        }
    }

    // Donor Add/Edit Dialog
    if (state.donorDialog.isOpen) {
        DonorAddEditDialog(
            dialogState = state.donorDialog,
            onDismiss = viewModel::closeDonorDialog,
            onSave = viewModel::saveDonor,
            onFieldChange = viewModel::onDonorDialogFieldChange
        )
    }
}

// ---------------- PANEL 1: DONORS ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DonorsPanel(
    state: AdminUiState,
    viewModel: AdminViewModel
) {
    val donors = state.donors.values.filter { donor ->
        val query = state.donorSearchQuery.trim().lowercase()
        val text = "${donor.name} ${donor.phone} ${donor.location} ${donor.bloodType}".lowercase()
        val matchesQuery = query.isEmpty() || text.contains(query)
        val matchesBlood = state.donorBloodFilter.isEmpty() || donor.bloodType == state.donorBloodFilter
        val matchesAvail = state.donorAvailabilityFilter.isEmpty() || donor.availability == state.donorAvailabilityFilter
        matchesQuery && matchesBlood && matchesAvail
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registered donors",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Button(
                    onClick = viewModel::openAddDonorDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = LifeLinkRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("+ Add donor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            // Search & Filter
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.donorSearchQuery,
                    onValueChange = viewModel::onDonorSearchChange,
                    placeholder = { Text("Search name, phone or location", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = LifeLinkRed,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (state.isLoadingDonors) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LifeLinkRed)
                }
            }
        } else if (state.donorsError != null) {
            item {
                EmptyState(
                    title = "Could not load donor directory.",
                    message = "Check Firebase database rules and permissions.",
                    icon = "!"
                )
            }
        } else if (donors.isEmpty()) {
            item {
                EmptyState(
                    title = if (state.donors.isNotEmpty()) "No donors match these filters." else "Your donor directory is ready.",
                    message = if (state.donors.isNotEmpty()) "Try changing your search or filters." else "Add a donor to start building your network."
                )
            }
        } else {
            items(donors, key = { it.id }) { donor ->
                DonorRowCard(
                    donor = donor,
                    onEdit = { viewModel.openEditDonorDialog(donor) },
                    onDelete = { viewModel.deleteDonor(donor.id) }
                )
            }
        }
    }
}

@Composable
private fun DonorRowCard(
    donor: Donor,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LifeLinkRedBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (donor.name.firstOrNull() ?: '?').uppercase(),
                    color = LifeLinkRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = donor.name.ifBlank { "Unnamed donor" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Text(
                    text = "${donor.phone} · ${donor.location.ifBlank { "—" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BloodGroupBadge(donor.bloodType)
                    AvailabilityBadge(donor.availability)
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit donor", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Remove donor", tint = ErrorRed, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ---------------- PANEL 2: APPLICATIONS ----------------
@Composable
private fun ApplicationsPanel(
    state: AdminUiState,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    val sorted = state.applications.values.sortedWith(
        compareBy<DonorApplication> { if (it.status == "Pending") 0 else 1 }
            .thenByDescending { it.createdAt }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.isLoadingApplications) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LifeLinkRed)
                }
            }
        } else if (state.applicationsError != null) {
            item {
                EmptyState(
                    title = "Could not load donor applications.",
                    message = "Check Firebase database rules and administrator authorization.",
                    icon = "!"
                )
            }
        } else if (sorted.isEmpty()) {
            item {
                EmptyState(
                    title = "No donor applications yet.",
                    message = "New applications will appear here for administrator review."
                )
            }
        } else {
            items(sorted, key = { it.id }) { app ->
                DonorApplicationCard(
                    application = app,
                    onApprove = { viewModel.reviewApplication(app, true) },
                    onDecline = { viewModel.reviewApplication(app, false) },
                    onCall = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${app.phone.filter { c -> c.isDigit() || c == '+' }}"))
                        context.startActivity(dialIntent)
                    }
                )
            }
        }
    }
}

@Composable
private fun DonorApplicationCard(
    application: DonorApplication,
    onApprove: () -> Unit,
    onDecline: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BloodGroupBadge(application.bloodType)
                    StatusBadge(application.status)
                }
                Text(
                    text = if (application.createdAt > 0) SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(application.createdAt)) else "Recently",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${application.name} · ID ${application.studentId}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "☎ ${application.phone}   ⌖ ${application.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (application.status == "Pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onCall) {
                        Text("Call applicant ↗", color = LifeLinkRed, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onDecline,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Decline", fontSize = 12.sp, color = ErrorRed)
                        }
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = LifeLinkRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Approve donor →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (application.status == "Approved") {
                Text(
                    text = "Added to the donor directory.",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "Application declined.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ---------------- PANEL 3: REQUESTS ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequestsPanel(
    state: AdminUiState,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    val sorted = state.requests.values.sortedByDescending { it.createdAt }.filter { req ->
        val query = state.requestSearchQuery.trim().lowercase()
        val text = "${req.studentId} ${req.name} ${req.bloodType} ${req.hospital}".lowercase()
        val matchesQuery = query.isEmpty() || text.contains(query)
        val matchesStatus = state.requestStatusFilter.isEmpty() || req.status == state.requestStatusFilter
        matchesQuery && matchesStatus
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = state.requestSearchQuery,
                onValueChange = viewModel::onRequestSearchChange,
                placeholder = { Text("Search ID, name, blood group", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = LifeLinkRed,
                    unfocusedBorderColor = BorderSubtle
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.isLoadingRequests) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LifeLinkRed)
                }
            }
        } else if (state.requestsError != null) {
            item {
                EmptyState(
                    title = "Could not load requests.",
                    message = "Check Firebase database rules and administrator authorization.",
                    icon = "!"
                )
            }
        } else if (sorted.isEmpty()) {
            item {
                EmptyState(
                    title = if (state.requests.isNotEmpty()) "No requests match these filters." else "No requests yet.",
                    message = if (state.requests.isNotEmpty()) "Try another search or status." else "New student requests will appear here."
                )
            }
        } else {
            items(sorted, key = { it.id }) { req ->
                AdminRequestCard(
                    request = req,
                    onUpdateStatus = { viewModel.updateRequestStatus(req.id, it) },
                    onDelete = { viewModel.deleteRequest(req.id) },
                    onCall = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${req.phone.filter { c -> c.isDigit() || c == '+' }}"))
                        context.startActivity(dialIntent)
                    },
                    onShareCard = {
                        RequestCardGenerator.shareRequestCard(
                            context = context,
                            request = req,
                            includeContact = true
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminRequestCard(
    request: BloodRequest,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onShareCard: () -> Unit
) {
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BloodGroupBadge(request.bloodType)
                    StatusBadge(request.status)
                }
                Text(
                    text = if (request.createdAt > 0) SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(request.createdAt)) else "Recently",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${request.name} · ID ${request.studentId}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "☎ ${request.phone}   ⌖ ${request.hospital}   ◷ ${request.urgency}   Units: ${request.units}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 12.sp
            )

            if (request.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = request.details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onCall, contentPadding = PaddingValues(horizontal = 6.dp)) {
                        Text("Call student ↗", color = LifeLinkRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onShareCard,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("↓ Share card", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box {
                        OutlinedButton(
                            onClick = { statusDropdownExpanded = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(request.status, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                        }
                        DropdownMenu(
                            expanded = statusDropdownExpanded,
                            onDismissRequest = { statusDropdownExpanded = false }
                        ) {
                            RequestStatusOption.all.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        onUpdateStatus(status)
                                        statusDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Close, contentDescription = "Delete request", tint = ErrorRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ---------------- PANEL 4: BLOG ----------------
@Composable
private fun BlogPanel(
    state: AdminUiState,
    viewModel: AdminViewModel
) {
    val publishedAdminPosts = state.blogPosts.filter { it.id !in BlogPost.starterPosts.map { s -> s.id } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "PUBLISH TO THE COMMUNITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeLinkRed,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Write a blog post",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LifeLinkTextField(
                        value = state.blogTitle,
                        onValueChange = viewModel::onBlogTitleChange,
                        label = "Title",
                        placeholder = "Give your story a title",
                        isRequired = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LifeLinkTextField(
                        value = state.blogCoverUrl,
                        onValueChange = viewModel::onBlogCoverUrlChange,
                        label = "Cover picture URL",
                        placeholder = "https://example.com/cover.jpg",
                        helperText = "Use a direct, publicly accessible HTTPS image URL.",
                        isRequired = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Cover preview if valid HTTPS URL
                    if (state.blogCoverUrl.startsWith("https://") && state.blogCoverUrl.length > 10) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PaleBlushPanel)
                        ) {
                            AsyncImage(
                                model = state.blogCoverUrl,
                                contentDescription = "Blog cover preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LifeLinkTextField(
                        value = state.blogBody,
                        onValueChange = viewModel::onBlogBodyChange,
                        label = "Text body",
                        placeholder = "Write your post here. Separate paragraphs with a blank line.",
                        isRequired = true,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.blogPublishError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = state.blogPublishError!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ErrorRed,
                            fontSize = 12.sp
                        )
                    }

                    if (state.blogPublishSuccess != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = state.blogPublishSuccess!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessGreen,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LifeLinkButton(
                        text = "Publish blog post",
                        onClick = viewModel::publishBlogPost,
                        isLoading = state.isPublishingBlog,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Text(
                text = "Published posts (${publishedAdminPosts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (publishedAdminPosts.isEmpty()) {
            item {
                EmptyState(
                    title = "No published posts yet.",
                    message = "Your published stories will appear here."
                )
            }
        } else {
            items(publishedAdminPosts, key = { it.id }) { post ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PaleBlushPanel)
                        ) {
                            if (post.image.isNotBlank()) {
                                AsyncImage(
                                    model = post.image,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = post.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        IconButton(onClick = { viewModel.deleteBlogPost(post.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete post", tint = ErrorRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ---------------- ADD / EDIT DONOR DIALOG ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DonorAddEditDialog(
    dialogState: DonorDialogState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onFieldChange: (name: String?, bloodType: String?, phone: String?, location: String?, availability: String?) -> Unit
) {
    var bloodExpanded by remember { mutableStateOf(false) }
    var availExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (dialogState.id.isNotBlank()) "Edit donor" else "Add a donor",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LifeLinkTextField(
                    value = dialogState.name,
                    onValueChange = { onFieldChange(it, null, null, null, null) },
                    label = "Full name",
                    placeholder = "Donor name",
                    isRequired = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Blood Group dropdown
                Column {
                    Text("Blood group *", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = bloodExpanded,
                        onExpandedChange = { bloodExpanded = !bloodExpanded }
                    ) {
                        OutlinedTextField(
                            value = dialogState.bloodType.ifBlank { "Select" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = bloodExpanded,
                            onDismissRequest = { bloodExpanded = false }
                        ) {
                            BloodGroup.all.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g) },
                                    onClick = {
                                        onFieldChange(null, g, null, null, null)
                                        bloodExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LifeLinkTextField(
                    value = dialogState.phone,
                    onValueChange = { onFieldChange(null, null, it, null, null) },
                    label = "Phone",
                    placeholder = "Contact number",
                    isRequired = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LifeLinkTextField(
                    value = dialogState.location,
                    onValueChange = { onFieldChange(null, null, null, it, null) },
                    label = "Location",
                    placeholder = "Area / hall",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Availability dropdown
                Column {
                    Text("Availability", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = availExpanded,
                        onExpandedChange = { availExpanded = !availExpanded }
                    ) {
                        OutlinedTextField(
                            value = dialogState.availability,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = availExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = availExpanded,
                            onDismissRequest = { availExpanded = false }
                        ) {
                            AvailabilityOption.all.forEach { av ->
                                DropdownMenuItem(
                                    text = { Text(av) },
                                    onClick = {
                                        onFieldChange(null, null, null, null, av)
                                        availExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (dialogState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(dialogState.error, color = ErrorRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onSave,
                        enabled = !dialogState.isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = LifeLinkRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (dialogState.isSaving) "Saving…" else "Save donor →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
