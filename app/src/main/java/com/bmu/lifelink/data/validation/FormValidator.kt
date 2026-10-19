package com.bmu.lifelink.data.validation

import com.bmu.lifelink.data.models.BloodGroup
import com.bmu.lifelink.data.models.UrgencyOption

object FormValidator {

    data class BloodRequestValidation(
        val studentIdError: String? = null,
        val nameError: String? = null,
        val phoneError: String? = null,
        val bloodTypeError: String? = null,
        val hospitalError: String? = null,
        val urgencyError: String? = null,
        val detailsError: String? = null,
        val consentError: String? = null
    ) {
        val isValid: Boolean
            get() = studentIdError == null &&
                    nameError == null &&
                    phoneError == null &&
                    bloodTypeError == null &&
                    hospitalError == null &&
                    urgencyError == null &&
                    detailsError == null &&
                    consentError == null
    }

    data class DonorApplicationValidation(
        val studentIdError: String? = null,
        val nameError: String? = null,
        val phoneError: String? = null,
        val bloodTypeError: String? = null,
        val locationError: String? = null,
        val consentError: String? = null
    ) {
        val isValid: Boolean
            get() = studentIdError == null &&
                    nameError == null &&
                    phoneError == null &&
                    bloodTypeError == null &&
                    locationError == null &&
                    consentError == null
    }

    data class BlogPostValidation(
        val titleError: String? = null,
        val coverUrlError: String? = null,
        val bodyError: String? = null
    ) {
        val isValid: Boolean
            get() = titleError == null && coverUrlError == null && bodyError == null
    }

    fun validateBloodRequest(
        studentId: String,
        name: String,
        phone: String,
        bloodType: String,
        hospital: String,
        urgency: String,
        details: String,
        consent: Boolean
    ): BloodRequestValidation {
        val studentIdError = StudentIdValidator.getValidationError(studentId)
        val nameError = when {
            name.isBlank() -> "Please enter your name."
            name.length > 80 -> "Name must be 80 characters or fewer."
            else -> null
        }
        val phoneError = when {
            phone.isBlank() -> "Please enter a contact number."
            phone.length > 24 -> "Contact number must be 24 characters or fewer."
            else -> null
        }
        val bloodTypeError = if (bloodType.isBlank() || !BloodGroup.all.contains(BloodGroup.normalize(bloodType))) {
            "Please select a blood group."
        } else null

        val hospitalError = when {
            hospital.isBlank() -> "Please enter the hospital or area."
            hospital.length > 120 -> "Hospital/location must be 120 characters or fewer."
            else -> null
        }
        val urgencyError = if (urgency.isBlank() || !UrgencyOption.all.contains(urgency)) {
            "Please select urgency."
        } else null

        val detailsError = if (details.length > 500) {
            "Additional details must be 500 characters or fewer."
        } else null

        val consentError = if (!consent) {
            "You must agree to the request conditions and administrator contact."
        } else null

        return BloodRequestValidation(
            studentIdError = studentIdError,
            nameError = nameError,
            phoneError = phoneError,
            bloodTypeError = bloodTypeError,
            hospitalError = hospitalError,
            urgencyError = urgencyError,
            detailsError = detailsError,
            consentError = consentError
        )
    }

    fun validateDonorApplication(
        studentId: String,
        name: String,
        phone: String,
        bloodType: String,
        location: String,
        consent: Boolean
    ): DonorApplicationValidation {
        val studentIdError = StudentIdValidator.getValidationError(studentId)
        val nameError = when {
            name.isBlank() -> "Please enter your name."
            name.length > 80 -> "Name must be 80 characters or fewer."
            else -> null
        }
        val phoneError = when {
            phone.isBlank() -> "Please enter a contact number."
            phone.length > 24 -> "Contact number must be 24 characters or fewer."
            else -> null
        }
        val bloodTypeError = if (bloodType.isBlank() || !BloodGroup.all.contains(BloodGroup.normalize(bloodType))) {
            "Please select a blood group."
        } else null

        val locationError = when {
            location.isBlank() -> "Please enter your location or hall."
            location.length > 100 -> "Location must be 100 characters or fewer."
            else -> null
        }
        val consentError = if (!consent) {
            "You must agree to be contacted about donating blood."
        } else null

        return DonorApplicationValidation(
            studentIdError = studentIdError,
            nameError = nameError,
            phoneError = phoneError,
            bloodTypeError = bloodTypeError,
            locationError = locationError,
            consentError = consentError
        )
    }

    fun validateBlogPost(
        title: String,
        coverUrl: String,
        body: String
    ): BlogPostValidation {
        val titleError = when {
            title.isBlank() -> "Enter a title for your post."
            title.length > 120 -> "Title must be 120 characters or fewer."
            else -> null
        }

        val trimmedCover = coverUrl.trim()
        val coverUrlError = when {
            trimmedCover.isBlank() -> "Enter a cover image URL."
            !trimmedCover.startsWith("https://") -> "Cover images must use HTTPS."
            trimmedCover.length > 2048 -> "Cover URL must be 2048 characters or fewer."
            !isValidHttpsUrl(trimmedCover) -> "Enter a valid HTTPS image URL."
            else -> null
        }

        val bodyError = when {
            body.isBlank() -> "Enter a text body for your post."
            body.length > 12000 -> "Body must be 12000 characters or fewer."
            else -> null
        }

        return BlogPostValidation(
            titleError = titleError,
            coverUrlError = coverUrlError,
            bodyError = bodyError
        )
    }

    private fun isValidHttpsUrl(url: String): Boolean {
        return try {
            val uri = java.net.URI(url)
            uri.scheme == "https" && !uri.host.isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }
}
