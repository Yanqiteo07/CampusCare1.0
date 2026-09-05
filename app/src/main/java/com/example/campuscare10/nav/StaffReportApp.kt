package com.example.campuscare10.nav

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.campuscare10.datamodel.StaffReport
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
