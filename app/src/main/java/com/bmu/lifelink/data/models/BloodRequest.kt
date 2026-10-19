package com.bmu.lifelink.data.models

data class BloodRequest(
    val id: String = "",
    val studentId: String = "",
    val name: String = "",
    val phone: String = "",
    val bloodType: String = "",
    val units: String = "1",
    val hospital: String = "",
    val urgency: String = "",
    val details: String = "",
    val status: String = "New",
    val createdAt: Long = 0L
) {
    fun toMap(useServerTimestamp: Boolean = true): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "studentId" to studentId,
            "name" to name,
            "phone" to phone,
            "bloodType" to bloodType,
            "units" to units,
            "hospital" to hospital,
            "urgency" to urgency,
            "details" to details,
            "status" to status
        )
        if (useServerTimestamp) {
            map["createdAt"] = com.google.firebase.database.ServerValue.TIMESTAMP
        } else {
            map["createdAt"] = createdAt
        }
        return map
    }
}
