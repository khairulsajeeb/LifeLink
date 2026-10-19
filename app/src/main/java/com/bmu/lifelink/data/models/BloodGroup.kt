package com.bmu.lifelink.data.models

object BloodGroup {
    // Unicode minus (U+2212: '−') is used in database rules and web options
    const val A_POS = "A+"
    const val A_NEG = "A−"
    const val B_POS = "B+"
    const val B_NEG = "B−"
    const val AB_POS = "AB+"
    const val AB_NEG = "AB−"
    const val O_POS = "O+"
    const val O_NEG = "O−"
    const val UNKNOWN = "Unknown"

    val all: List<String> = listOf(
        A_POS, A_NEG,
        B_POS, B_NEG,
        AB_POS, AB_NEG,
        O_POS, O_NEG,
        UNKNOWN
    )

    fun normalize(input: String): String {
        val trimmed = input.trim()
        val withUnicodeMinus = trimmed.replace("-", "−")
        return when (withUnicodeMinus) {
            A_POS -> A_POS
            A_NEG -> A_NEG
            B_POS -> B_POS
            B_NEG -> B_NEG
            AB_POS -> AB_POS
            AB_NEG -> AB_NEG
            O_POS -> O_POS
            O_NEG -> O_NEG
            UNKNOWN -> UNKNOWN
            else -> trimmed
        }
    }
}

object UrgencyOption {
    const val ASAP = "As soon as possible"
    const val WITHIN_24_HOURS = "Within 24 hours"
    const val WITHIN_2_3_DAYS = "Within 2–3 days"
    const val SCHEDULED = "Scheduled / planned"

    val all: List<String> = listOf(
        ASAP,
        WITHIN_24_HOURS,
        WITHIN_2_3_DAYS,
        SCHEDULED
    )
}

object RequestStatusOption {
    const val NEW = "New"
    const val CONTACTED = "Contacted"
    const val FULFILLED = "Fulfilled"
    const val CLOSED = "Closed"

    val all: List<String> = listOf(
        NEW,
        CONTACTED,
        FULFILLED,
        CLOSED
    )
}

object ApplicationStatusOption {
    const val PENDING = "Pending"
    const val APPROVED = "Approved"
    const val DECLINED = "Declined"

    val all: List<String> = listOf(
        PENDING,
        APPROVED,
        DECLINED
    )
}

object AvailabilityOption {
    const val AVAILABLE = "Available"
    const val UNAVAILABLE = "Unavailable"

    val all: List<String> = listOf(
        AVAILABLE,
        UNAVAILABLE
    )
}
