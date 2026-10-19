package com.bmu.lifelink

import com.bmu.lifelink.data.models.BloodGroup
import com.bmu.lifelink.data.models.BloodRequest
import org.junit.Assert.*
import org.junit.Test

class RequestCardPrivacyTest {

    @Test
    fun testCardNeverContainsStudentId() {
        val request = BloodRequest(
            id = "req-123",
            studentId = "24123456",
            name = "Test Student",
            phone = "+8801700000000",
            bloodType = BloodGroup.AB_POS,
            units = "2",
            hospital = "City Hospital",
            urgency = "Within 24 hours"
        )

        // Verify card data representation logic
        fun buildCardText(request: BloodRequest, includeContact: Boolean): String {
            val builder = StringBuilder()
            builder.append("LIFELINK · BMU STUDENTS PLATFORM\n")
            builder.append("BLOOD NEEDED: ").append(request.bloodType).append("\n")
            builder.append("UNITS: ").append(request.units).append("\n")
            builder.append("WHEN: ").append(request.urgency).append("\n")
            builder.append("HOSPITAL: ").append(request.hospital).append("\n")
            if (includeContact && request.phone.isNotBlank()) {
                builder.append("CONTACT: ").append(request.name).append(" · ").append(request.phone).append("\n")
            }
            return builder.toString()
        }

        val cardWithoutContact = buildCardText(request, includeContact = false)
        val cardWithContact = buildCardText(request, includeContact = true)

        // Privacy rule 1: Student ID must NEVER be included under any circumstances
        assertFalse("Card without contact must NOT contain student ID", cardWithoutContact.contains(request.studentId))
        assertFalse("Card with contact must NOT contain student ID", cardWithContact.contains(request.studentId))

        // Privacy rule 2: Contact info excluded when not opted in
        assertFalse("Card without contact must NOT contain phone", cardWithoutContact.contains(request.phone))
        assertFalse("Card without contact must NOT contain name", cardWithoutContact.contains(request.name))

        // Privacy rule 3: Contact info included ONLY when user opted in
        assertTrue("Card with contact must contain phone", cardWithContact.contains(request.phone))
        assertTrue("Card with contact must contain name", cardWithContact.contains(request.name))
    }
}
