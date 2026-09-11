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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuscare10.datamodel.StudentProfile
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun StudentLoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit
) {
    val primaryPurple = Color(0xFF6C47FF)
    val backgroundTint = Color(0xFFF8F8FC)

    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    var studentIdError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundTint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Student Login",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryPurple,
                        fontSize = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sign in to access your reports",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = studentId,
                    onValueChange = { 
                        studentId = it
                        studentIdError = null 
                    },
                    label = { 
                        Text(
                            text = studentIdError ?: "Student ID / Email",
                            color = if (studentIdError != null) MaterialTheme.colorScheme.error else Color.Unspecified
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = studentIdError != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = null
                    },
                    label = { 
                        Text(
                            text = passwordError ?: "Password",
                            color = if (passwordError != null) MaterialTheme.colorScheme.error else Color.Unspecified
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryPurple,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red
                    ),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = passwordVisible,
                        onCheckedChange = { passwordVisible = it },
                        colors = CheckboxDefaults.colors(checkedColor = primaryPurple)
                    )
                    Text(text = "Show Password", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        studentIdError = null
                        passwordError = null

                        if (studentId.isBlank()) {
                            studentIdError = "Field required"
                        }
                        if (password.isBlank()) {
                            passwordError = "Password required"
                        }

                        if (studentIdError != null || passwordError != null) return@Button

                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val result = supabase.from("Studentprofiles")
                                    .select {
                                        filter {
                                            eq("email", studentId)
                                            eq("password", password)
                                        }
                                    }.decodeSingleOrNull<StudentProfile>()

                                if (result != null) {
                                    Toast.makeText(context, "Welcome, ${result.username ?: studentId}", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    studentIdError = "Invalid Login"
                                    passwordError = "Invalid Login"
                                    Toast.makeText(context, "Invalid ID or Password", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("StudentLogin", "Error", e)
                                Toast.makeText(context, "Login Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Login",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Don't have an account?", color = Color.Gray, fontSize = 14.sp)
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "Register",
                            color = primaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBackClick) {
                    Text("Back to Splash", color = Color.Gray)
                }
            }
        }
    }
}
