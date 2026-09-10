package com.example.campuscare10.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    onNavigateToStudent: () -> Unit,
    onNavigateToStaff: () -> Unit
) {
    val primaryPurple = Color(0xFF6C47FF)
    val staffGreen = Color(0xFF388E3C)
    val backgroundTint = Color(0xFFF8F8FC)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundTint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Icon Placeholder (Replace with Image if you have a drawable)
            Box(
                modifier = Modifier
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home, // Change to your campus icon/drawable resource
                    contentDescription = "Campus Logo",
                    tint = primaryPurple,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Title
            Text(
                text = "CampusCare",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = primaryPurple,
                    fontSize = 32.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle Texts
            Text(
                text = "Report, Track, Improve.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Together, we build\na better campus.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.DarkGray,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Student Entry Button (Purple)
            Button(
                onClick = onNavigateToStudent,
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Student",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Staff Entry Button (Green)
            Button(
                onClick = onNavigateToStaff,
                colors = ButtonDefaults.buttonColors(containerColor = staffGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Staff",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onNavigateToStudent = {}, onNavigateToStaff = {})
}