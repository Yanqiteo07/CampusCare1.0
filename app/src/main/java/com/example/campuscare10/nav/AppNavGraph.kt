package com.example.campuscare10.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.screen.DashboardScreen
import com.example.campuscare10.screen.ReportDetailScreen
import com.example.campuscare10.screen.StaffReportsScreen
import com.example.campuscare10.screen.UpdateStatusScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    reports: SnapshotStateList<StaffReport>
) {
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
                    onSave = { updatedCategory, newStatus, newRating, newNote, newImageUri ->
                        // Update local state list immediately
                        val updatedReport = reports[reportIndex].copy(
                            category = updatedCategory,
                            status = newStatus,
                            rating = newRating,
                            note = newNote,
                            imageUri = newImageUri
                        )
                        reports[reportIndex] = updatedReport
                        Log.d("State", "Local state updated for report ${updatedReport.id}")

                        navController.navigate("detail/${updatedReport.id}") {
                            popUpTo("detail/${updatedReport.id}") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
