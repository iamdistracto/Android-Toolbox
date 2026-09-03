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
import java.io.InputStream

@Composable
fun ImageInfoTool() {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var infoResult by remember { mutableStateOf<String?>(null) }
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
                        val result = StringBuilder()
                        result.append("File: ${fileName ?: "image"}\n\n")

                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        resolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream, null, options)
                        }
                        result.append("Dimensions: ${options.outWidth} x ${options.outHeight}\n")
                        result.append("MIME Type: ${options.outMimeType ?: "?"}\n")

                        try {
                            resolver.openInputStream(uri)?.use { stream ->
                                val exif = ExifInterface(stream)
                                result.append("\n--- EXIF Data ---\n")
                                result.append("Make: ${exif.getAttribute(ExifInterface.TAG_MAKE) ?: "N/A"}\n")
                                result.append("Model: ${exif.getAttribute(ExifInterface.TAG_MODEL) ?: "N/A"}\n")
                                result.append("DateTime: ${exif.getAttribute(ExifInterface.TAG_DATETIME) ?: "N/A"}\n")
                                result.append("GPS Latitude: ${exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "N/A"}\n")
                                result.append("GPS Longitude: ${exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "N/A"}\n")
                            }
                        } catch (e: Exception) {
                            result.append("\nNo EXIF data found or unsupported format.\n")
                        }

                        infoResult = result.toString()
                        operationState = OperationState.Success()
                    } catch (e: Exception) {
                        operationState = OperationState.Error("Failed to read image info: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Image Info")
            }

            infoResult?.let { info ->
                val isSuccess = operationState is OperationState.Success
                val message = if (isSuccess) info else (operationState as OperationState.Error).message
                ResultCard(
                    title = "Image Info",
                    message = message,
                    isSuccess = isSuccess
                )
            }
        }
    }
}
