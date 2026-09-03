package com.toolbox.ui.tools.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.material3.TextField
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
import java.io.File
import java.io.FileOutputStream

@Composable
fun ImageResizeTool() {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var widthText by remember { mutableStateOf("1920") }
    var heightText by remember { mutableStateOf("1080") }
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

        TextField(
            value = widthText,
            onValueChange = { widthText = it.filter { c -> c.isDigit() } },
            label = { Text("Width") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        TextField(
            value = heightText,
            onValueChange = { heightText = it.filter { c -> c.isDigit() } },
            label = { Text("Height") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        selectedUri?.let { uri ->
            OutlinedButton(
                onClick = {
                    operationState = OperationState.Processing
                    try {
                        val width = widthText.toIntOrNull() ?: 1920
                        val height = heightText.toIntOrNull() ?: 1080

                        val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        } ?: throw Exception("Failed to open image")

                        val resized = Bitmap.createScaledBitmap(bitmap, width, height, true)

                        val outputDir = context.getExternalFilesDir(null)
                        val outputFile = File(outputDir, "resized_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(outputFile).use { out ->
                            resized.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        }

                        bitmap.recycle()
                        resized.recycle()

                        outputPath = outputFile.absolutePath
                        operationState = OperationState.Success(outputFile.absolutePath)
                    } catch (e: Exception) {
                        operationState = OperationState.Error("Failed to resize image: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resize Image")
            }

            val success = operationState as? OperationState.Success
            if (success != null) {
                ResultCard(
                    title = "Resize Complete",
                    message = "Image resized to ${widthText}x${heightText}",
                    isSuccess = true,
                    outputPath = success.outputPath
                )
            } else if (operationState is OperationState.Error) {
                val error = operationState as OperationState.Error
                ResultCard(
                    title = "Resize Failed",
                    message = error.message,
                    isSuccess = false
                )
            }
        }
    }
}
