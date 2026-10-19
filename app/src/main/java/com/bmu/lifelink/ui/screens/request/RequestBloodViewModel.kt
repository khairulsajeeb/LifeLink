package com.bmu.lifelink.ui.screens.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmu.lifelink.data.models.BloodGroup
import com.bmu.lifelink.data.models.BloodRequest
import com.bmu.lifelink.data.models.UrgencyOption
import com.bmu.lifelink.data.repository.FirebaseRepository
import com.bmu.lifelink.data.validation.FormValidator
import com.bmu.lifelink.data.validation.StudentIdValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RequestBloodUiState(
    val studentId: String = "",
    val name: String = "",
    val phone: String = "",
    val bloodType: String = "",
    val units: String = "1",
    val hospital: String = "",
    val urgency: String = "",
    val details: String = "",
    val consent: Boolean = false,
    val shareContactOnCard: Boolean = false,

    val validation: FormValidator.BloodRequestValidation = FormValidator.BloodRequestValidation(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val submittedRequest: BloodRequest? = null
)

class RequestBloodViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestBloodUiState())
    val uiState: StateFlow<RequestBloodUiState> = _uiState.asStateFlow()

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

    fun onUnitsChange(value: String) {
        _uiState.update { it.copy(units = value) }
    }

    fun onHospitalChange(value: String) {
        _uiState.update {
            it.copy(
                hospital = value.take(120),
                validation = it.validation.copy(hospitalError = null),
                errorMessage = null
            )
        }
    }

    fun onUrgencyChange(value: String) {
        _uiState.update {
            it.copy(
                urgency = value,
                validation = it.validation.copy(urgencyError = null),
                errorMessage = null
            )
        }
    }

    fun onDetailsChange(value: String) {
        _uiState.update {
            it.copy(
                details = value.take(500),
                validation = it.validation.copy(detailsError = null),
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

    fun onShareContactChange(value: Boolean) {
        _uiState.update { it.copy(shareContactOnCard = value) }
    }

    fun submitRequest() {
        val state = _uiState.value
        val validation = FormValidator.validateBloodRequest(
            studentId = state.studentId,
            name = state.name,
            phone = state.phone,
            bloodType = state.bloodType,
            hospital = state.hospital,
            urgency = state.urgency,
            details = state.details,
            consent = state.consent
        )

        if (!validation.isValid) {
            _uiState.update { it.copy(validation = validation) }
            return
        }

        val request = BloodRequest(
            studentId = state.studentId.trim(),
            name = state.name.trim(),
            phone = state.phone.trim(),
            bloodType = state.bloodType,
            units = state.units,
            hospital = state.hospital.trim(),
            urgency = state.urgency,
            details = state.details.trim(),
            status = "New"
        )

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val result = repository.submitBloodRequest(request)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Your request has been sent privately. A LifeLink administrator will follow up using the contact number you provided.",
                        submittedRequest = request,
                        // Reset inputs but preserve share card details
                        studentId = "",
                        name = "",
                        phone = "",
                        hospital = "",
                        details = "",
                        consent = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "We could not send your request. Please check your connection and try again."
                    )
                }
            }
        }
    }
}
