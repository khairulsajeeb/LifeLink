package com.bmu.lifelink.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmu.lifelink.data.models.BlogPost
import com.bmu.lifelink.data.models.BloodRequest
import com.bmu.lifelink.data.models.Donor
import com.bmu.lifelink.data.models.DonorApplication
import com.bmu.lifelink.data.repository.FirebaseRepository
import com.bmu.lifelink.data.validation.FormValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AdminTab {
    DONORS,
    APPLICATIONS,
    REQUESTS,
    BLOG
}

data class DonorDialogState(
    val isOpen: Boolean = false,
    val id: String = "",
    val name: String = "",
    val bloodType: String = "",
    val phone: String = "",
    val location: String = "",
    val availability: String = "Available",
    val isSaving: Boolean = false,
    val error: String? = null
)

data class AdminUiState(
    // Auth
    val isAuthenticated: Boolean = false,
    val userEmail: String? = null,
    val loginEmail: String = "",
    val loginPassword: String = "",
    val isLoggingIn: Boolean = false,
    val loginError: String? = null,

    // Active Tab
    val activeTab: AdminTab = AdminTab.DONORS,

    // Donors Panel
    val donors: Map<String, Donor> = emptyMap(),
    val isLoadingDonors: Boolean = false,
    val donorsError: String? = null,
    val donorSearchQuery: String = "",
    val donorBloodFilter: String = "",
    val donorAvailabilityFilter: String = "",
    val donorDialog: DonorDialogState = DonorDialogState(),

    // Applications Panel
    val applications: Map<String, DonorApplication> = emptyMap(),
    val isLoadingApplications: Boolean = false,
    val applicationsError: String? = null,

    // Requests Panel
    val requests: Map<String, BloodRequest> = emptyMap(),
    val isLoadingRequests: Boolean = false,
    val requestsError: String? = null,
    val requestSearchQuery: String = "",
    val requestStatusFilter: String = "",

    // Blog Panel
    val blogPosts: List<BlogPost> = emptyList(),
    val isLoadingBlog: Boolean = false,
    val blogTitle: String = "",
    val blogCoverUrl: String = "",
    val blogBody: String = "",
    val isPublishingBlog: Boolean = false,
    val blogPublishError: String? = null,
    val blogPublishSuccess: String? = null
) {
    val registeredDonorsCount: Int
        get() = donors.size

    val openRequestsCount: Int
        get() = requests.values.count { it.status !in listOf("Fulfilled", "Closed") }

    val pendingApplicationsCount: Int
        get() = applications.values.count { it.status == "Pending" }

    val totalRequestsCount: Int
        get() = requests.size

    val blogPostsCount: Int
        get() = blogPosts.filter { it.id !in BlogPost.starterPosts.map { s -> s.id } }.size
}

class AdminViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            repository.authStateFlow.collect { user ->
                val isAuth = user != null
                _uiState.update {
                    it.copy(
                        isAuthenticated = isAuth,
                        userEmail = user?.email,
                        isLoggingIn = false,
                        loginError = null
                    )
                }
                if (isAuth) {
                    startDashboardSync()
                }
            }
        }
    }

    fun onLoginEmailChange(value: String) {
        _uiState.update { it.copy(loginEmail = value, loginError = null) }
    }

    fun onLoginPasswordChange(value: String) {
        _uiState.update { it.copy(loginPassword = value, loginError = null) }
    }

    fun signIn() {
        val state = _uiState.value
        if (state.loginEmail.isBlank() || state.loginPassword.isBlank()) {
            _uiState.update { it.copy(loginError = "Please enter your email and password.") }
            return
        }

        _uiState.update { it.copy(isLoggingIn = true, loginError = null) }
        viewModelScope.launch {
            val result = repository.signInAdmin(state.loginEmail, state.loginPassword)
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoggingIn = false,
                        loginError = error.message ?: "Sign-in failed. Check your credentials."
                    )
                }
            }
        }
    }

    fun signOut() {
        repository.signOutAdmin()
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                userEmail = null,
                donors = emptyMap(),
                applications = emptyMap(),
                requests = emptyMap()
            )
        }
    }

    fun setTab(tab: AdminTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    private fun startDashboardSync() {
        // Observe Donors
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDonors = true, donorsError = null) }
            repository.getDonorsFlow().collect { result ->
                result.onSuccess { donors ->
                    _uiState.update { it.copy(donors = donors, isLoadingDonors = false, donorsError = null) }
                }.onFailure { err ->
                    _uiState.update { it.copy(isLoadingDonors = false, donorsError = err.message) }
                }
            }
        }

        // Observe Requests
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRequests = true, requestsError = null) }
            repository.getBloodRequestsFlow().collect { result ->
                result.onSuccess { requests ->
                    _uiState.update { it.copy(requests = requests, isLoadingRequests = false, requestsError = null) }
                }.onFailure { err ->
                    _uiState.update { it.copy(isLoadingRequests = false, requestsError = err.message) }
                }
            }
        }

        // Observe Applications
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingApplications = true, applicationsError = null) }
            repository.getDonorApplicationsFlow().collect { result ->
                result.onSuccess { apps ->
                    _uiState.update { it.copy(applications = apps, isLoadingApplications = false, applicationsError = null) }
                }.onFailure { err ->
                    _uiState.update { it.copy(isLoadingApplications = false, applicationsError = err.message) }
                }
            }
        }

        // Observe Blog Posts
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBlog = true) }
            repository.getBlogPostsFlow().collect { posts ->
                _uiState.update { it.copy(blogPosts = posts, isLoadingBlog = false) }
            }
        }
    }

    // --- Donor Directory Actions ---
    fun onDonorSearchChange(query: String) {
        _uiState.update { it.copy(donorSearchQuery = query) }
    }

    fun onDonorBloodFilterChange(blood: String) {
        _uiState.update { it.copy(donorBloodFilter = blood) }
    }

    fun onDonorAvailabilityFilterChange(avail: String) {
        _uiState.update { it.copy(donorAvailabilityFilter = avail) }
    }

    fun openAddDonorDialog() {
        _uiState.update {
            it.copy(
                donorDialog = DonorDialogState(
                    isOpen = true,
                    id = "",
                    name = "",
                    bloodType = "",
                    phone = "",
                    location = "",
                    availability = "Available"
                )
            )
        }
    }

    fun openEditDonorDialog(donor: Donor) {
        _uiState.update {
            it.copy(
                donorDialog = DonorDialogState(
                    isOpen = true,
                    id = donor.id,
                    name = donor.name,
                    bloodType = donor.bloodType,
                    phone = donor.phone,
                    location = donor.location,
                    availability = donor.availability
                )
            )
        }
    }

    fun closeDonorDialog() {
        _uiState.update { it.copy(donorDialog = it.donorDialog.copy(isOpen = false)) }
    }

    fun onDonorDialogFieldChange(
        name: String? = null,
        bloodType: String? = null,
        phone: String? = null,
        location: String? = null,
        availability: String? = null
    ) {
        _uiState.update {
            val d = it.donorDialog
            it.copy(
                donorDialog = d.copy(
                    name = name ?: d.name,
                    bloodType = bloodType ?: d.bloodType,
                    phone = phone ?: d.phone,
                    location = location ?: d.location,
                    availability = availability ?: d.availability,
                    error = null
                )
            )
        }
    }

    fun saveDonor() {
        val d = _uiState.value.donorDialog
        if (d.name.isBlank() || d.bloodType.isBlank() || d.phone.isBlank()) {
            _uiState.update { it.copy(donorDialog = d.copy(error = "Please fill in all required fields.")) }
            return
        }

        _uiState.update { it.copy(donorDialog = d.copy(isSaving = true, error = null)) }
        viewModelScope.launch {
            val donor = Donor(
                id = d.id,
                name = d.name.trim(),
                bloodType = d.bloodType,
                phone = d.phone.trim(),
                location = d.location.trim(),
                availability = d.availability
            )
            val result = repository.saveDonor(donor)
            result.onSuccess {
                _uiState.update { it.copy(donorDialog = DonorDialogState(isOpen = false)) }
            }.onFailure { err ->
                _uiState.update { it.copy(donorDialog = d.copy(isSaving = false, error = err.message ?: "Failed to save donor.")) }
            }
        }
    }

    fun deleteDonor(id: String) {
        viewModelScope.launch {
            repository.deleteDonor(id)
        }
    }

    // --- Donor Applications Actions ---
    fun reviewApplication(application: DonorApplication, approve: Boolean) {
        viewModelScope.launch {
            repository.reviewDonorApplication(application, approve)
        }
    }

    // --- Blood Requests Actions ---
    fun onRequestSearchChange(query: String) {
        _uiState.update { it.copy(requestSearchQuery = query) }
    }

    fun onRequestStatusFilterChange(status: String) {
        _uiState.update { it.copy(requestStatusFilter = status) }
    }

    fun updateRequestStatus(id: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(id, newStatus)
        }
    }

    fun deleteRequest(id: String) {
        viewModelScope.launch {
            repository.deleteRequest(id)
        }
    }

    // --- Blog Panel Actions ---
    fun onBlogTitleChange(value: String) {
        _uiState.update { it.copy(blogTitle = value, blogPublishError = null, blogPublishSuccess = null) }
    }

    fun onBlogCoverUrlChange(value: String) {
        _uiState.update { it.copy(blogCoverUrl = value, blogPublishError = null, blogPublishSuccess = null) }
    }

    fun onBlogBodyChange(value: String) {
        _uiState.update { it.copy(blogBody = value, blogPublishError = null, blogPublishSuccess = null) }
    }

    fun publishBlogPost() {
        val state = _uiState.value
        val validation = FormValidator.validateBlogPost(
            title = state.blogTitle,
            coverUrl = state.blogCoverUrl,
            body = state.blogBody
        )

        if (!validation.isValid) {
            val err = validation.titleError ?: validation.coverUrlError ?: validation.bodyError
            _uiState.update { it.copy(blogPublishError = err) }
            return
        }

        _uiState.update { it.copy(isPublishingBlog = true, blogPublishError = null, blogPublishSuccess = null) }

        viewModelScope.launch {
            val result = repository.publishBlogPost(state.blogTitle, state.blogBody, state.blogCoverUrl)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isPublishingBlog = false,
                        blogPublishSuccess = "Your post is published and now appears on the blog and homepage.",
                        blogTitle = "",
                        blogCoverUrl = "",
                        blogBody = ""
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isPublishingBlog = false,
                        blogPublishError = "Could not publish this post. Check database rules and try again."
                    )
                }
            }
        }
    }

    fun deleteBlogPost(id: String) {
        viewModelScope.launch {
            repository.deleteBlogPost(id)
        }
    }
}
