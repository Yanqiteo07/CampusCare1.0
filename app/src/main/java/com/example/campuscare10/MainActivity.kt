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
import com.example.campuscare10.supabase.supabase
import com.example.campuscare10.ui.theme.DashboardScreen
import com.example.campuscare10.ui.theme.ReportDetailScreen
import com.example.campuscare10.ui.theme.StaffReportsScreen
import com.example.campuscare10.ui.theme.UpdateStatusScreen
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    var reports by remember { mutableStateOf<List<StaffReport>>(listOf())}
    val scope = rememberCoroutineScope()

    // Fetch reports from Supabase when the app starts
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                reports = supabase.from("reports")
                    .select()
                    .decodeList<StaffReport>()
            } catch (e: Exception) {
                // Handle or log connection errors if needed
            }
        }
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
                        scope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    supabase.from("reports").update({
                                        set("status", newStatus)
                                    }) {
                                        filter { eq("id", reportId!!) }
                                    }
                                }
                                // Update local state after successful database write
                                reports = reports.mapIndexed { index, report ->
                                    if (index == reportIndex) report.copy(status = newStatus) else report
                                }
                                navController.navigate("detail/${reports[reportIndex].id}") {
                                    popUpTo("detail/${reports[reportIndex].id}") {
                                        inclusive = true
                                    }
                                }
                            } catch (e: Exception) {
                                // Handle error here if needed
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