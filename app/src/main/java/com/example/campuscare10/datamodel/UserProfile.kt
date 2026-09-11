package com.example.campuscare10.datamodel

data class UserProfile(
    val name: String,
    val email: String,
    val role: String, // "student" or "staff"
    val studentOrStaffId: String,
    val department: String,
    val phoneNumber: String
)
