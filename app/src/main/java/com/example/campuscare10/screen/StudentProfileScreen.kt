package com.example.campuscare10.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.campuscare10.datamodel.StudentProfile
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun StudentProfileScreen(navController: NavController, student: StudentProfile) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val primaryColor = Color(0xFF303F9F) // Student Blue Theme
    val lightContainerColor = Color(0xFFE2E4FF)

    // Fixed: changed contactNo to phoneNumber and handled type correctly
    var phoneNumber by remember { mutableStateOf(student.phoneNumber.toString()) }
    var isEditingPhone by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("student_dashboard") { popUpTo("student_profile") { inclusive = true } } },
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("reports") { popUpTo("student_profile") { inclusive = true } } },
                    icon = { Text("▤") },
                    label = { Text("My Reports") }
                )
                NavigationBarItem(
                    selected = true,
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
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("student_dashboard") { popUpTo("student_dashboard") { inclusive = true } } }) {
                    Text("‹", style = MaterialTheme.typography.headlineMedium, color = Color.Gray)
                }
                Text("Profile", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(lightContainerColor),
                contentAlignment = Alignment.Center
            ) {
                // Fixed: changed username to studentName
                val displayName = student.studentName.ifEmpty { student.studentId ?: "U" }
                val initials = displayName.filter { it.isUpperCase() }.let {
                    if (it.isEmpty()) displayName.take(1).uppercase() else it.take(2)
                }
                Text(text = initials, color = primaryColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fixed: changed username to studentName
            Text(text = student.studentName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = student.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { Text(text = "★", color = Color(0xFFFFC107), fontSize = 14.sp) }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = student.rating.toString(), fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Student ID : ${student.studentId ?: "N/A"}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Department: ${student.department}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEditingPhone) {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isUpdating = true
                                        try {
                                            val phoneVal = phoneNumber.toDoubleOrNull() ?: 0.0
                                            supabase.from("Studentprofiles").update({
                                                set("PhoneNumber", phoneVal)
                                            }) {
                                                filter { eq("Studentid", student.studentId ?: "") }
                                            }
                                            isEditingPhone = false
                                            Toast.makeText(context, "Phone updated successfully", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            isUpdating = false
                                        }
                                    }
                                },
                                enabled = !isUpdating,
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                if (isUpdating) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                else Text("Save", fontSize = 12.sp)
                            }
                        } else {
                            Text(text = "Phone Number: $phoneNumber", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            IconButton(onClick = { isEditingPhone = true }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Phone", tint = primaryColor, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("student_login") { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}