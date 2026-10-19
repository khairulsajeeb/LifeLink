package com.bmu.lifelink

import com.bmu.lifelink.data.models.BloodGroup
import com.bmu.lifelink.data.validation.FormValidator
import org.junit.Assert.*
import org.junit.Test

class FormValidationTest {

    @Test
    fun testValidBloodRequest() {
        val validation = FormValidator.validateBloodRequest(
            studentId = "24123456",
            name = "Sarah Ahmed",
            phone = "+8801700000000",
            bloodType = BloodGroup.A_POS,
            hospital = "Dhaka Medical College Hospital",
            urgency = "Within 24 hours",
            details = "Patient in surgery unit",
            consent = true
        )
        assertTrue(validation.isValid)
        assertNull(validation.studentIdError)
        assertNull(validation.nameError)
        assertNull(validation.phoneError)
        assertNull(validation.bloodTypeError)
        assertNull(validation.hospitalError)
        assertNull(validation.urgencyError)
        assertNull(validation.detailsError)
        assertNull(validation.consentError)
    }

    @Test
    fun testBloodRequestMissingConsent() {
        val validation = FormValidator.validateBloodRequest(
            studentId = "24123456",
            name = "Sarah Ahmed",
            phone = "+8801700000000",
            bloodType = BloodGroup.O_NEG,
            hospital = "Square Hospital",
            urgency = "As soon as possible",
            details = "",
            consent = false
        )
        assertFalse(validation.isValid)
        assertNotNull(validation.consentError)
    }

    @Test
    fun testBloodRequestInvalidBloodGroup() {
        val validation = FormValidator.validateBloodRequest(
            studentId = "24123456",
            name = "Sarah Ahmed",
            phone = "+8801700000000",
            bloodType = "InvalidGroup",
            hospital = "Square Hospital",
            urgency = "As soon as possible",
            details = "",
            consent = true
        )
        assertFalse(validation.isValid)
        assertNotNull(validation.bloodTypeError)
    }

    @Test
    fun testValidDonorApplication() {
        val validation = FormValidator.validateDonorApplication(
            studentId = "24123456",
            name = "Farhan Rahman",
            phone = "+8801800000000",
            bloodType = BloodGroup.B_POS,
            location = "BMU Hall, Block B",
            consent = true
        )
        assertTrue(validation.isValid)
        assertNull(validation.consentError)
    }

    @Test
    fun testDonorApplicationMissingConsent() {
        val validation = FormValidator.validateDonorApplication(
            studentId = "24123456",
            name = "Farhan Rahman",
            phone = "+8801800000000",
            bloodType = BloodGroup.B_POS,
            location = "BMU Hall, Block B",
            consent = false
        )
        assertFalse(validation.isValid)
        assertNotNull(validation.consentError)
    }

    @Test
    fun testBlogPostValidation() {
        // Valid post
        val valid = FormValidator.validateBlogPost(
            title = "A Community Together",
            coverUrl = "https://example.com/cover.jpg",
            body = "This is a meaningful community article."
        )
        assertTrue(valid.isValid)

        // Invalid HTTP URL (HTTPS required)
        val invalidHttp = FormValidator.validateBlogPost(
            title = "A Community Together",
            coverUrl = "http://example.com/cover.jpg",
            body = "This is a meaningful community article."
        )
        assertFalse(invalidHttp.isValid)
        assertNotNull(invalidHttp.coverUrlError)
        assertTrue(invalidHttp.coverUrlError!!.contains("HTTPS"))

        // Blank title and body
        val emptyPost = FormValidator.validateBlogPost(
            title = "",
            coverUrl = "https://example.com/cover.jpg",
            body = ""
        )
        assertFalse(emptyPost.isValid)
        assertNotNull(emptyPost.titleError)
        assertNotNull(emptyPost.bodyError)
    }
}
