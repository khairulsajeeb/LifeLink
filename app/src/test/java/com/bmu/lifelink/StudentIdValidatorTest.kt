package com.bmu.lifelink

import com.bmu.lifelink.data.validation.StudentIdValidator
import org.junit.Assert.*
import org.junit.Test

class StudentIdValidatorTest {

    private val currentYear = 2026

    @Test
    fun testValidStudentIdBoundaries() {
        // Lower bound: 2019
        assertTrue(StudentIdValidator.isValid("19000001", currentYear))
        assertTrue(StudentIdValidator.isValid("19999999", currentYear))

        // Intermediate years: 2020..2025
        assertTrue(StudentIdValidator.isValid("20123456", currentYear))
        assertTrue(StudentIdValidator.isValid("24123456", currentYear))
        assertTrue(StudentIdValidator.isValid("25123456", currentYear))

        // Upper bound: 2026
        assertTrue(StudentIdValidator.isValid("26000000", currentYear))
        assertTrue(StudentIdValidator.isValid("26999999", currentYear))
    }

    @Test
    fun testInvalidYearPrefixes() {
        // Below 2019
        assertFalse(StudentIdValidator.isValid("18123456", currentYear))
        assertFalse(StudentIdValidator.isValid("10123456", currentYear))
        assertFalse(StudentIdValidator.isValid("00123456", currentYear))

        // Above current year (2026)
        assertFalse(StudentIdValidator.isValid("27123456", currentYear))
        assertFalse(StudentIdValidator.isValid("30123456", currentYear))
        assertFalse(StudentIdValidator.isValid("99123456", currentYear))
    }

    @Test
    fun testInvalidLengthsAndCharacters() {
        assertFalse(StudentIdValidator.isValid("2412345", currentYear)) // 7 digits
        assertFalse(StudentIdValidator.isValid("241234567", currentYear)) // 9 digits
        assertFalse(StudentIdValidator.isValid("", currentYear)) // empty
        assertFalse(StudentIdValidator.isValid("24abc456", currentYear)) // non-digits
        assertFalse(StudentIdValidator.isValid("24 12345", currentYear)) // whitespace
    }

    @Test
    fun testSanitizeInput() {
        assertEquals("24123456", StudentIdValidator.sanitize("24-123-456"))
        assertEquals("24123456", StudentIdValidator.sanitize("abc24123456xyz"))
        assertEquals("24123456", StudentIdValidator.sanitize("241234567899")) // max 8 chars
        assertEquals("", StudentIdValidator.sanitize("no digits here"))
    }

    @Test
    fun testValidationErrorMessage() {
        assertNull(StudentIdValidator.getValidationError("24123456", currentYear))
        val error = StudentIdValidator.getValidationError("18123456", currentYear)
        assertNotNull(error)
        assertTrue(error!!.contains("2019 to $currentYear"))
    }
}
