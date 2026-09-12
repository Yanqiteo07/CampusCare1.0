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
import com.example.campuscare10.datamodel.StaffProfile
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.datamodel.StudentProfiles
import com.example.campuscare10.screen.*
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from

@Composable
fun StaffReportApp() {
    val navController = rememberNavController()
    val reports = remember { mutableStateListOf<StaffReport>() }

    AppNavGraph(navController = navController, reports = reports)
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    reports: SnapshotStateList<StaffReport>
) {
    var currentStaff by remember { mutableStateOf<StaffProfile?>(null) }
    var currentStudent by remember { mutableStateOf<StudentProfiles?>(null) }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToStudent = { navController.navigate("student_login") },
                onNavigateToStaff = { navController.navigate("staff_login") }
            )
        }

        composable("student_login") {
            StudentLoginScreen(
                onLoginSuccess = { student ->
                    currentStudent = student
                    navController.navigate("student_dashboard") {
                        popUpTo("student_login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegistrationSuccess = {
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("staff_login") {
            StaffLoginScreen(
                onLoginSuccess = { staff ->
                    currentStaff = staff
                    navController.navigate("staff_dashboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("staff_dashboard") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                try {
                    val fetchedReports = supabase.from("reports").select().decodeList<StaffReport>()
                    reports.clear()
                    reports.addAll(fetchedReports)
                    Log.d("Supabase", "Fetched ${fetchedReports.size} reports")
                } catch (e: Exception) {
                    Log.e("Supabase", "Error fetching reports", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            DashboardScreen(reports, navController, currentStaff)
        }

        composable("student_dashboard") {
            StudentDashboardScreen(reports, navController, currentStudent?.studentName ?: "Student")
        }

        // Added route for Submitting a Report (referenced from student dashboard)
        composable("submit_report") {
            SubmitReportScreen(navController, currentStudent?.studentId ?: "Unknown")
        }

        // Connected Student Profile screen
        composable("student_profile") {
            currentStudent?.let { student ->
                StudentProfileScreen(navController, student)
            }
        }

        // Connected Student Notifications / Alerts screen
        composable("student_notifications") {
            StudentNotificationsScreen(navController)
        }

        composable("staff_profile") {
            currentStaff?.let { staff ->
                StaffProfileScreen(navController, staff)
            }
        }

        composable("reports") {
            MyReportsScreen(navController)
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
                        val updatedReport = reports[reportIndex].copy(
                            category = updatedCategory,
                            status = newStatus,
                            rating = newRating,
                            note = newNote,
                            imageUri = newImageUri
                        )
                        reports[reportIndex] = updatedReport
                        navController.navigate("detail/${updatedReport.id}") {
                            popUpTo("detail/${updatedReport.id}") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}