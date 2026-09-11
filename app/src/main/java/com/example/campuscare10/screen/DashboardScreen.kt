package com.example.campuscare10.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.repository.ReportRepository

const val CURRENT_USER = "student"

@Composable
fun StudentDashboardScreen(
    reports: List<StaffReport>,
    navController: NavController
) {
    val repo = ReportRepository()
    var reportList by remember { mutableStateOf(reports) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(reports) {
        if (reports.isNotEmpty()) {
            reportList = reports
        } else {
            isLoading = true
            val allReports = repo.fetchAllReports()
            reportList = allReports
            isLoading = false
        }
    }

    val total = reportList.size
    val inProgress = reportList.count { it.status == "In Progress" }
    val completed = reportList.count { it.status == "Completed" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text("Hi, $CURRENT_USER!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("How can we help today?", color = Color.Gray)
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("submit_report") },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C47FF))
        ) {
            Text("+ Submit New Report")
        }

        Spacer(Modifier.height(20.dp))
        Text("Overview", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard("$total", "Total Reports", Color(0xFFE3E5FE))
            StatCard("$inProgress", "In Progress", Color(0xFFFFE8D6))
            StatCard("$completed", "Completed", Color(0xFFD9F2E4))
        }

        Spacer(Modifier.height(20.dp))
        Text("Recent Reports", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        if (isLoading) {
            Text(text = "Loading reports...", modifier = Modifier.fillMaxWidth())
        } else if (reportList.isEmpty()) {
            Text(text = "No reports found.", color = Color.Gray, modifier = Modifier.fillMaxWidth())
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = reportList,
                    key = { it.id }
                ) { report ->
                    ReportRow(report = report, onClick = {
                        navController.navigate("detail/${report.id}")
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, bg: Color) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp)
            .background(bg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = when (label) {
                "Total Reports" -> Color(0xFF6C47FF)
                "In Progress" -> Color(0xFFFF7700)
                "Completed" -> Color(0xFF009944)
                else -> Color.Gray
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = when (label) {
                "Total Reports" -> Color(0xFF6C47FF)
                "In Progress" -> Color(0xFFFF7700)
                "Completed" -> Color(0xFF009944)
                else -> Color.Gray
            }
        )
    }
}

@Composable
fun ReportRow(report: StaffReport, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text("Category: ${report.category}", fontWeight = FontWeight.Medium)
        Text("Location: ${report.location}", color = Color.DarkGray)
        val statusColor = when (report.status) {
            "Submitted" -> Color(0xFF6C47FF)
            "In Progress" -> Color(0xFFFF7700)
            "Completed" -> Color(0xFF009944)
            else -> Color.Gray
        }
        Text("Status: ${report.status}", color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
