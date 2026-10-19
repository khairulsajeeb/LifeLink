package com.bmu.lifelink.data.models

data class DonorApplication(
    val id: String = "",
    val studentId: String = "",
    val name: String = "",
    val phone: String = "",
    val bloodType: String = "",
    val location: String = "",
    val status: String = "Pending",
    val createdAt: Long = 0L,
    val reviewedAt: Long? = null,
    val donorId: String? = null
) {
    fun toMap(useServerTimestamp: Boolean = true): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "studentId" to studentId,
            "name" to name,
            "phone" to phone,
            "bloodType" to bloodType,
            "location" to location,
            "status" to status
        )
        if (useServerTimestamp) {
            map["createdAt"] = com.google.firebase.database.ServerValue.TIMESTAMP
        } else {
            map["createdAt"] = createdAt
        }
        if (reviewedAt != null) map["reviewedAt"] = reviewedAt
        if (donorId != null) map["donorId"] = donorId
        return map
    }
}
