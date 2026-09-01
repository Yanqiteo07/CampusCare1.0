package com.example.campuscare10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.ui.theme.DashboardScreen
import com.example.campuscare10.ui.theme.ReportDetailScreen
import com.example.campuscare10.ui.theme.StaffReportsScreen
import com.example.campuscare10.ui.theme.UpdateStatusScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(primary = Color(0xFF348C2C))
            ) {
                StaffReportApp()
            }
        }
    }
}

@Composable
fun StaffReportApp() {
    val navController = rememberNavController()

    val reports = remember {
        mutableStateListOf(
            StaffReport(
                1, "Broken Equipment", "Science Laboratory",
                "Microscope is damaged and cannot be used.",
                "Aina", "12 Aug 2026, 9:30 AM", "In Progress"
            ),
            StaffReport(
                2, "Electrical Issue", "Block B, Level 2",
                "The classroom light is flickering.",
                "Daniel", "12 Aug 2026, 8:15 AM", "Submitted"
            ),
            StaffReport(
                3, "Cleaning Request", "School Cafeteria",
                "Spilled drink near the seating area.",
                "Farah", "11 Aug 2026, 3:40 PM", "Completed"
            ),
            StaffReport(
                4, "Furniture Damage", "Meeting Room",
                "One chair has a broken leg.",
                "Hafiz", "11 Aug 2026, 11:10 AM", "Submitted"
            )
        )
    }

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(reports, navController)
        }
        composable("reports") {
            StaffReportsScreen(reports, navController)
        }
        composable("detail/{reportId}") { entry ->
            val reportId = entry.arguments?.getString("reportId")?.toIntOrNull()
            val report = reports.find { it.id == reportId }

            if (report != null) {
                ReportDetailScreen(report, navController)
            }
        }
        composable("update/{reportId}") { entry ->
            val reportId = entry.arguments?.getString("reportId")?.toIntOrNull()
            val reportIndex = reports.indexOfFirst { it.id == reportId }

            if (reportIndex >= 0) {
                UpdateStatusScreen(
                    report = reports[reportIndex],
                    onBack = { navController.popBackStack() },
                    onSave = { newStatus ->
                        reports[reportIndex] =
                            reports[reportIndex].copy(status = newStatus)
                        navController.navigate("detail/${reports[reportIndex].id}") {
                            popUpTo("detail/${reports[reportIndex].id}") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReportsScreen(x0: SnapshotStateList<StaffReport>, x1: NavHostController) {
    TODO("Not yet implemented")
}