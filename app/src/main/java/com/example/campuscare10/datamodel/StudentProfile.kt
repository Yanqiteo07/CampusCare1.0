package com.example.campuscare10.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentProfile(
    @SerialName(value = "Studentid") val studentId: String? = null,
    @SerialName(value = "StudentName") val username: String? = null,
    @SerialName(value = "email") val email: String? = null,
    @SerialName(value = "role") val role: String? = null,
    @SerialName(value = "Department") val department: String? = null,
    @SerialName(value = "PhoneNumber") val contactNo: String? = null, // Changed from "contact_no" to "PhoneNumber" to match Supabase
    @SerialName(value = "password") val password: String? = null,
    @SerialName(value = "rating") val rating: Double? = null
)
