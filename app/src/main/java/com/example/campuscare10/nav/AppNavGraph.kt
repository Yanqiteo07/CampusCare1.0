package com.example.campuscare10.nav

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.screen.*
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from

@Composable
fun StaffReportApp() {
    val navController = rememberNavController()
    val reports = remember { mutableStateListOf<StaffReport>() }
    val context = LocalContext.current

    // Fetch reports from Supabase when the app starts
    LaunchedEffect(Unit) {
        try {
            val fetchedReports = supabase.from("reports").select().decodeList<StaffReport>()
            Log.d("Supabase", "Fetched ${fetchedReports.size} reports")
            reports.clear()
            reports.addAll(fetchedReports)
        } catch (e: Exception) {
            Log.e("Supabase", "Error fetching reports", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    AppNavGraph(navController = navController, reports = reports)
}

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
        composable("equipment_list") {
            EquipmentListScreen(navController)
        }
        composable("add_equipment") {
            AddEquipmentScreen(navController)
        }
        composable("equipment_detail/{equipmentId}") { entry ->
            val equipmentId = entry.arguments?.getString("equipmentId")?.toLongOrNull()
            if (equipmentId != null) {
                EquipmentDetailScreen(navController, equipmentId)
            }
        }
        composable("edit_equipment/{equipmentId}") { entry ->
            val equipmentId = entry.arguments?.getString("equipmentId")?.toLongOrNull()
            if (equipmentId != null) {
                EditEquipmentScreen(navController, equipmentId)
            }
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
