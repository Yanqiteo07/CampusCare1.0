package com.example.campuscare10

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch

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
    val reports = remember { mutableStateListOf<StaffReport>() }
    val coroutineScope = rememberCoroutineScope()

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
                    onSave = { updatedCategory: String, newStatus: String, newRating: Float, newNote: String?, newImageUri: String? ->
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