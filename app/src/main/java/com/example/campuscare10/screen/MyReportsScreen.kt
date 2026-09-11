package com.example.campuscare10.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.repository.ReportRepository

@Composable
fun MyReportsScreen(navController: NavController) {
    val repo = ReportRepository()
    var allUserReports by remember { mutableStateOf<List<StaffReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var filter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        isLoading = true
        val cloudData = repo.fetchAllReports()
        allUserReports = cloudData
        isLoading = false
    }

    val filteredList = when (filter) {
        "Submitted" -> allUserReports.filter { it.status == "Submitted" }
        "In Progress" -> allUserReports.filter { it.status == "In Progress" }
        "Completed" -> allUserReports.filter { it.status == "Completed" }
        else -> allUserReports
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text("My Reports", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterBtn(text = "All", current = filter) { filter = "All" }
            FilterBtn(text = "Submitted", current = filter) { filter = "Submitted" }
            FilterBtn(text = "In Progress", current = filter) { filter = "In Progress" }
            FilterBtn(text = "Completed", current = filter) { filter = "Completed" }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text(text = "Loading reports...", modifier = Modifier.fillMaxWidth())
        } else if (filteredList.isEmpty()) {
            Text(text = "No reports found.", color = Color.Gray, modifier = Modifier.fillMaxWidth())
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = filteredList,
                    key = { it.id }
                ) { report ->
                    ReportRow(report = report, onClick = {
                        navController.navigate("detail/${report.id}")
                    })
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}

@Composable
fun FilterBtn(text: String, current: String, onClick: () -> Unit) {
    val selected = text == current
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF6C47FF) else Color.White,
            contentColor = if (selected) Color.White else Color.Black
        ),
        border = if (!selected) BorderStroke(1.dp, Color.Gray) else null,
        shape = RoundedCornerShape(50)
    ) {
        Text(text, fontSize = 13.sp)
    }
}