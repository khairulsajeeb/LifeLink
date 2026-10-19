package com.bmu.lifelink.data.validation

import java.util.Calendar

object StudentIdValidator {
    const val MIN_YEAR = 2019

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    /**
     * Sanitizes input to digits only, max 8 characters.
     */
    fun sanitize(input: String): String {
        return input.filter { it.isDigit() }.take(8)
    }

    /**
     * Validates whether a student ID is valid.
     * Must be exactly 8 digits, with first two digits representing a year between 2019 and current year.
     */
    fun isValid(studentId: String, currentYear: Int = getCurrentYear()): Boolean {
        val trimmed = studentId.trim()
        if (trimmed.length != 8) return false
        if (!trimmed.all { it.isDigit() }) return false

        val yearPrefix = trimmed.substring(0, 2).toIntOrNull() ?: return false
        val year = 2000 + yearPrefix
        return year in MIN_YEAR..currentYear
    }

    /**
     * Returns an error message if invalid, or null if valid.
     */
    fun getValidationError(studentId: String, currentYear: Int = getCurrentYear()): String? {
        if (studentId.isBlank()) {
            return "Student ID is required."
        }
        return if (isValid(studentId, currentYear)) {
            null
        } else {
            "Enter an 8-digit ID with a year from 2019 to $currentYear in its first two digits."
        }
    }
}
