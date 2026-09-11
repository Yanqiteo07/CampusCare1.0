package com.example.campuscare10.repository

import android.content.Context
import android.net.Uri
import com.example.campuscare10.datamodel.StaffReport
// Ensure you point to your actual Supabase client initialization object
// import com.example.campuscare10.data.SupabaseClient.client as supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID
import com.example.campuscare10.supabase.supabase
class ReportRepository {


    suspend fun fetchAllReports(): List<StaffReport> {
        return try {
            supabase
                .from("reports")
                .select()
                .decodeList<StaffReport>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getReportById(reportId: Long): StaffReport? {
        return try {
            val list = supabase
                .from("reports")
                .select {
                    filter {
                        eq("id", reportId)
                    }
                }
                .decodeList<StaffReport>()
            list.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun insertNewReport(newReport: StaffReport) {

        supabase
            .from("reports")
            .insert(newReport)

    }

    suspend fun uploadImageToStorage(
        context: Context,
        fileUri: Uri,
        bucketName: String = "report_photo"
    ): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
            val bytes = inputStream?.readBytes()

            if (bytes == null) return@withContext null

            val fileName = "${UUID.randomUUID()}.jpg"

            supabase.storage.from(bucketName).upload(fileName, bytes)
            val publicUrl = supabase.storage.from(bucketName).publicUrl(fileName)
            return@withContext publicUrl

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}