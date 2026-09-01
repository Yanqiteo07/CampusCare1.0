package com.example.campuscare10.datamodel

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffReport(
    val id: Int,
    val category: String,
    val location: String,
    val description: String,
    @SerialName("submitted_by") val submittedBy: String,
    @SerialName("submitted_time") val submittedTime: String,
    val status: String
)

fun statusColor(status: String): Color {
    return when (status) {
        "In Progress" -> Color(0xFFFF8500)
        "Completed" -> Color(0xFF2E8B28)
        "Submitted" -> Color(0xFF4057D6)
        else -> Color(0xFF6B6B6B)
    }
}