package com.example.campuscare10.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.campuscare10.datamodel.StaffProfile
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun StaffProfileScreen(navController: NavController, staff: StaffProfile) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val primaryColor = Color(0xFF2E7D32) // Staff Green Theme
    val lightContainerColor = Color(0xFFDDEFD9)

    var staffDetails by remember { mutableStateOf(staff) }
    var phoneNumber by remember { mutableStateOf(staff.phoneNumber ?: "") }
    var isEditingPhone by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val latestProfile = supabase.from("Staffprofile")
                .select {
                    filter { eq("Staffid", staff.staffId) }
                }.decodeSingleOrNull<StaffProfile>()

            if (latestProfile != null) {
                staffDetails = latestProfile
                phoneNumber = latestProfile.phoneNumber ?: ""
            }
        } catch (e: Exception) {
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("staff_dashboard") {
                            popUpTo("staff_profile") { inclusive = true }
                        }
                    },
                    icon = { Text("⌂") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("reports") {
                            popUpTo("staff_profile") { inclusive = true }
                        }
                    },
                    icon = { Text("▤") },
                    label = { Text("Reports") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("equipment_list") {
                            popUpTo("staff_profile") { inclusive = true }
                        }
                    },
                    icon = { Text("☰") },
                    label = { Text("Equipment") }
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
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(lightContainerColor),
                contentAlignment = Alignment.Center
            ) {
                val displayName = staffDetails.staffName ?: staffDetails.staffId
                val initials = displayName.filter { it.isUpperCase() }.let {
                    if (it.isEmpty()) displayName.take(1).uppercase() else it.take(2)
                }
                Text(text = initials, color = primaryColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = staffDetails.staffName ?: staffDetails.staffId, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = staffDetails.email ?: "No email provided", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(35.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Staff ID : ${staffDetails.staffId}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Department: ${staffDetails.department ?: "N/A"}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isUpdating = true
                                        try {
                                            supabase.from("Staffprofile").update({
                                                set("PhoneNumber", phoneNumber)
                                            }) {
                                                filter { eq("Staffid", staffDetails.staffId) }
                                            }

                                            staffDetails = staffDetails.copy(phoneNumber = phoneNumber)
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
                            val displayPhone = if (phoneNumber.isBlank()) "Not provided" else phoneNumber
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Phone Number:", fontSize = 11.sp, color = Color.Gray)
                                Text(text = displayPhone, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
                            }
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
                    navController.navigate("splash") { popUpTo(0) { inclusive = true } }
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