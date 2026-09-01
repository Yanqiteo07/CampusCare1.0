package com.example.campuscare10.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.campuscare10.datamodel.StaffReport
import com.example.campuscare10.datamodel.statusColor

@Composable
fun DashboardScreen(reports: List<StaffReport>, navController: NavController) {
    val inProgress = reports.count { it.status == "In Progress" }
    val completed = reports.count { it.status == "Completed" }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("reports") },
                    icon = { Text("▤") },
                    label = { Text("Reports") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("☰") },
                    label = { Text("Equipment") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("◉") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(padding)
        ) {
            Text("Hi, Staff!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Here's the overview today.", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(20.dp))
            Text("Overview", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewCard("Total Reports", reports.size.toString(), Color(0xFFE2E4FF), Modifier.weight(1f))
                OverviewCard("In Progress", inProgress.toString(), Color(0xFFFFE9D7), Modifier.weight(1f))
                OverviewCard("Completed", completed.toString(), Color(0xFFDDEFD9), Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            Text("Recent Reports", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            reports.take(3).forEach { report ->
                ReportCard(report) {
                    navController.navigate("detail/${report.id}")
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add New Equipment")
            }
        }
    }
}

@Composable
fun OverviewCard(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun StaffReportsScreen(reports: List<StaffReport>, navController: NavController) {
    var selectedStatus by remember { mutableStateOf("All Status") }
    val statuses = listOf("All Status", "Submitted", "In Progress", "Completed")

    val filteredReports = reports.filter {
        selectedStatus == "All Status" || it.status == selectedStatus
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(false, { navController.navigate("dashboard") }, { Text("⌂") }, label = { Text("Home") })
                NavigationBarItem(true, {}, { Text("▤") }, label = { Text("Reports") })
                NavigationBarItem(false, {}, { Text("☰") }, label = { Text("Equipment") })
                NavigationBarItem(false, {}, { Text("◉") }, label = { Text("Profile") })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(padding)
        ) {
            Text("All Reports", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))
            StatusDropdown(selectedStatus, statuses) { selectedStatus = it }

            Spacer(Modifier.height(14.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredReports) { report ->
                    ReportCard(report) {
                        navController.navigate("detail/${report.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: StaffReport, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(report.category, fontWeight = FontWeight.Bold)
            Text(report.location, style = MaterialTheme.typography.bodySmall)
            Text(
                "Status: ${report.status}",
                style = MaterialTheme.typography.bodySmall,
                color = statusColor(report.status),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReportDetailScreen(report: StaffReport, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "‹",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(end = 22.dp)
                    .clickable { navController.popBackStack() }
            )
            Text("Report Detail", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(18.dp))
        AssistChip(
            onClick = {},
            label = { Text(report.status) },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = statusColor(report.status)
            )
        )

        Spacer(Modifier.height(14.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("Photo")
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("Report Detail", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        DetailItem("Location", report.location)
        DetailItem("Description", report.description)
        DetailItem("Submitted by", report.submittedBy)
        DetailItem("Submitted time", report.submittedTime)
        DetailItem("Report ID", "#${report.id}")
        DetailItem("Category", report.category)

        Spacer(Modifier.weight(1f))
        Button(
            onClick = { navController.navigate("update/${report.id}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Status")
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun UpdateStatusScreen(
    report: StaffReport,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var category by remember { mutableStateOf(report.category) }
    var selectedStatus by remember { mutableStateOf(report.status) }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "‹",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(end = 22.dp)
                    .clickable { onBack() }
            )
            Text("Update Status", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(18.dp))
        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        StatusDropdown(category, listOf(
            "Broken Equipment", "Electrical Issue", "Cleaning Request", "Furniture Damage"
        )) { category = it }

        Spacer(Modifier.height(18.dp))
        Text("Update to", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            border = BorderStroke(1.dp, Color(0xFFE5E5E5))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                listOf("Under Review", "In Progress", "Completed").forEach { status ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Text(status)
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("Note (Optional)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text("Add internal note...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        )

        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onSave(selectedStatus) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}