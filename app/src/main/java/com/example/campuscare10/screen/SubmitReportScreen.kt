package com.example.campuscare10.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import com.example.campuscare10.repository.ReportRepository
import com.example.campuscare10.datamodel.StaffReport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitReportScreen(navController: NavController, studentId: String = "Unknown Student"){
    val categoryOptions = listOf("Lecture Hall", "Classroom", "Toilet", "Building", "Others")
    var selectedCategory by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val repo = ReportRepository()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        photoUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)){
        Row(modifier = Modifier.padding(bottom = 16.dp)){
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Submit Report", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Text("Category")
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categoryOptions.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = {
                        selectedCategory = cat
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Location")
        OutlinedTextField(
            value = locationText,
            onValueChange = { locationText = it },
            placeholder = { Text("e.g. Block A, Room 202") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Description")
        OutlinedTextField(
            value = descText,
            onValueChange = { if(it.length <= 200) descText = it },
            placeholder = { Text("Describe the issue...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            supportingText = { Text("${descText.length}/200") }
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Upload Photo (Optional)")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if(photoUri != null){
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "preview",
                    modifier = Modifier.fillMaxSize()
                )
            }else{
                Text(text = "Tap to upload photo", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                scope.launch {
                    var finalImageUrl: String? = null

                    if (photoUri != null) {
                        photoUri?.let { nonNullUri ->
                            finalImageUrl = repo.uploadImageToStorage(
                                context = context,
                                fileUri = nonNullUri
                            )
                        }
                    }

                    val newReport = StaffReport(
                        id = 0,
                        category = selectedCategory,
                        location = locationText,
                        description = descText,
                        submittedBy = studentId,
                        status = "Submitted",
                        imageUri = finalImageUrl,
                        note = null
                    )
                    repo.insertNewReport(newReport)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C47FF))
        ) {
            Text("Submit")
        }
    }
}
