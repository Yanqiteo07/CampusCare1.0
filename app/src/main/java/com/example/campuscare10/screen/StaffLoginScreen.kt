package com.example.campuscare10.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuscare10.datamodel.StaffProfile
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun StaffLoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var staffId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val staffGreen = Color(0xFF388E3C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Staff Login",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = staffGreen
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = staffId,
            onValueChange = { staffId = it },
            label = { Text("Staff ID") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (staffId.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                coroutineScope.launch {
                    isLoading = true
                    try {
                        // Table name "Staffprofile" as shown in screenshot
                        val result = supabase.from("Staffprofile")
                            .select {
                                filter {
                                    eq("Staffid", staffId)
                                    eq("password", password)
                                }
                            }.decodeSingleOrNull<StaffProfile>()

                        if (result != null) {
                            Toast.makeText(context, "Welcome, ${result.staffId}", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "Invalid Staff ID or Password", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("Login", "Error", e)
                        Toast.makeText(context, "Login Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = staffGreen),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Back to Splash", color = Color.Gray)
        }
    }
}
