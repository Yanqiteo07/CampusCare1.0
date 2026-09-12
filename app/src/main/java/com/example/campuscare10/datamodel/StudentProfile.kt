package com.example.campuscare10.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentProfile(
    @SerialName("Studentid") val studentId: String? = null,
    @SerialName("StudentName") val studentName: String,
    @SerialName("email") val email: String,
    @SerialName("Department") val department: String,
    @SerialName("password") val password: String,
    @SerialName("rating") val rating: Double,
    @SerialName("PhoneNumber") val phoneNumber: Double
)
