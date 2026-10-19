package com.bmu.lifelink.ui.screens.donor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmu.lifelink.data.models.BloodGroup
import com.bmu.lifelink.data.models.DonorApplication
import com.bmu.lifelink.data.repository.FirebaseRepository
import com.bmu.lifelink.data.validation.FormValidator
import com.bmu.lifelink.data.validation.StudentIdValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DonorApplicationUiState(
    val studentId: String = "",
    val name: String = "",
    val phone: String = "",
    val bloodType: String = "",
    val location: String = "",
    val consent: Boolean = false,

    val validation: FormValidator.DonorApplicationValidation = FormValidator.DonorApplicationValidation(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class DonorApplicationViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorApplicationUiState())
    val uiState: StateFlow<DonorApplicationUiState> = _uiState.asStateFlow()

    fun onStudentIdChange(value: String) {
        val sanitized = StudentIdValidator.sanitize(value)
        _uiState.update {
            it.copy(
                studentId = sanitized,
                validation = it.validation.copy(studentIdError = null),
                errorMessage = null
            )
        }
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(
                name = value.take(80),
                validation = it.validation.copy(nameError = null),
                errorMessage = null
            )
        }
    }

    fun onPhoneChange(value: String) {
        _uiState.update {
            it.copy(
                phone = value.take(24),
                validation = it.validation.copy(phoneError = null),
                errorMessage = null
            )
        }
    }

    fun onBloodTypeChange(value: String) {
        _uiState.update {
            it.copy(
                bloodType = BloodGroup.normalize(value),
                validation = it.validation.copy(bloodTypeError = null),
                errorMessage = null
            )
        }
    }

    fun onLocationChange(value: String) {
        _uiState.update {
            it.copy(
                location = value.take(100),
                validation = it.validation.copy(locationError = null),
                errorMessage = null
            )
        }
    }

    fun onConsentChange(value: Boolean) {
        _uiState.update {
            it.copy(
                consent = value,
                validation = it.validation.copy(consentError = null),
                errorMessage = null
            )
        }
    }

    fun submitApplication() {
        val state = _uiState.value
        val validation = FormValidator.validateDonorApplication(
            studentId = state.studentId,
            name = state.name,
            phone = state.phone,
            bloodType = state.bloodType,
            location = state.location,
            consent = state.consent
        )

        if (!validation.isValid) {
            _uiState.update { it.copy(validation = validation) }
            return
        }

        val application = DonorApplication(
            studentId = state.studentId.trim(),
            name = state.name.trim(),
            phone = state.phone.trim(),
            bloodType = state.bloodType,
            location = state.location.trim(),
            status = "Pending"
        )

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val result = repository.submitDonorApplication(application)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Your donor application has been submitted. The administrator will review it before you are added to the donor directory.",
                        studentId = "",
                        name = "",
                        phone = "",
                        location = "",
                        consent = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "We could not send your application. Please check your connection and try again."
                    )
                }
            }
        }
    }
}
