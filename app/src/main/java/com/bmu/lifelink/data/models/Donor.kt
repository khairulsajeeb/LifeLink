package com.bmu.lifelink.data.models

data class Donor(
    val id: String = "",
    val name: String = "",
    val bloodType: String = "",
    val phone: String = "",
    val location: String = "",
    val availability: String = "Available",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val applicationId: String? = null
) {
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "name" to name,
            "bloodType" to bloodType,
            "phone" to phone,
            "location" to location,
            "availability" to availability,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
        if (applicationId != null) map["applicationId"] = applicationId
        return map
    }
}
