package com.toolbox.ui.tools.images

import android.content.ContentResolver
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.toolbox.ui.components.FilePickerButton
import com.toolbox.ui.components.ResultCard
import com.toolbox.core.OperationState
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun ImageExifTool() {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var beforeInfo by remember { mutableStateOf<String?>(null) }
    var afterInfo by remember { mutableStateOf<String?>(null) }
    var outputPath by remember { mutableStateOf<String?>(null) }
    var operationState by remember { mutableStateOf<OperationState>(OperationState.Idle) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedUri = it
            fileName = it.lastPathSegment ?: "image"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilePickerButton(
            filePath = fileName,
            onPickClick = { launcher.launch("image/*") },
            label = "Select Image"
        )

        selectedUri?.let { uri ->
            OutlinedButton(
                onClick = {
                    operationState = OperationState.Processing
                    try {
                        val resolver = context.contentResolver

                        val inputStream = resolver.openInputStream(uri)
                            ?: throw Exception("Failed to open image")

                        val exif = ExifInterface(inputStream)
                        inputStream.close()

                        beforeInfo = buildString {
                            append("BEFORE (Metadata present)\n")
                            append("Make: ${exif.getAttribute(ExifInterface.TAG_MAKE) ?: "N/A"}\n")
                            append("Model: ${exif.getAttribute(ExifInterface.TAG_MODEL) ?: "N/A"}\n")
                            append("DateTime: ${exif.getAttribute(ExifInterface.TAG_DATETIME) ?: "N/A"}\n")
                            append("GPS Latitude: ${exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "N/A"}\n")
                            append("GPS Longitude: ${exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "N/A"}\n")
                        }

                        val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        } ?: throw Exception("Failed to decode image")

                        val outputDir = context.getExternalFilesDir(null)
                        val outputFile = File(outputDir, "cleaned_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(outputFile).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        }
                        bitmap.recycle()

                        val cleanedExif = ExifInterface(outputFile.absolutePath)
                        cleanedExif.setAttribute(ExifInterface.TAG_MAKE, null)
                        cleanedExif.setAttribute(ExifInterface.TAG_MODEL, null)
                        cleanedExif.setAttribute(ExifInterface.TAG_DATETIME, null)
                        cleanedExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, null)
                        cleanedExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, null)
                        cleanedExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, null)
                        cleanedExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, null)
                        cleanedExif.saveAttributes()

                        afterInfo = buildString {
                            append("AFTER (Metadata removed)\n")
                            append("Make: ${cleanedExif.getAttribute(ExifInterface.TAG_MAKE) ?: "Removed"}\n")
                            append("Model: ${cleanedExif.getAttribute(ExifInterface.TAG_MODEL) ?: "Removed"}\n")
                            append("DateTime: ${cleanedExif.getAttribute(ExifInterface.TAG_DATETIME) ?: "Removed"}\n")
                            append("GPS Latitude: ${cleanedExif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "Removed"}\n")
                            append("GPS Longitude: ${cleanedExif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "Removed"}\n")
                        }

                        outputPath = outputFile.absolutePath
                        operationState = OperationState.Success(outputFile.absolutePath)
                    } catch (e: Exception) {
                        operationState = OperationState.Error("Failed to clean EXIF: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Remove EXIF Metadata")
            }

            beforeInfo?.let { before ->
                ResultCard(
                    title = "Before",
                    message = before,
                    isSuccess = true
                )
            }

            afterInfo?.let { after ->
                ResultCard(
                    title = "After",
                    message = after,
                    isSuccess = true
                )
            }

            if (operationState is OperationState.Error) {
                val error = operationState as OperationState.Error
                ResultCard(
                    title = "EXIF Clean Failed",
                    message = error.message,
                    isSuccess = false
                )
            }
        }
    }
}
