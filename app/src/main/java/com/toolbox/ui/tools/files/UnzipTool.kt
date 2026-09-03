package com.toolbox.ui.tools.files

import android.content.ContentResolver
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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Composable
fun UnzipTool() {
    var zipUri by remember { mutableStateOf<Uri?>(null) }
    var zipFileName by remember { mutableStateOf<String?>(null) }
    var outputName by remember { mutableStateOf("extracted") }
    var operationState by remember { mutableStateOf<OperationState>(OperationState.Idle) }
    val context = LocalContext.current

    val zipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            zipUri = it
            zipFileName = it.lastPathSegment ?: "archive.zip"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilePickerButton(
            filePath = zipFileName,
            onPickClick = { zipLauncher.launch("application/zip") },
            label = "Select ZIP File"
        )

        TextField(
            value = outputName,
            onValueChange = { outputName = it },
            label = { Text("Output Folder Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        zipUri?.let { uri ->
            OutlinedButton(
                onClick = {
                    operationState = OperationState.Processing
                    try {
                        val tempZip = File(context.cacheDir, "temp_${System.currentTimeMillis()}.zip")
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(tempZip).use { output ->
                                input.copyTo(output)
                            }
                        }

                        val outputDir = context.getExternalFilesDir(null)
                        val outputFolder = File(outputDir, outputName)
                        if (!outputFolder.exists()) {
                            outputFolder.mkdirs()
                        }

                        ZipInputStream(FileInputStream(tempZip)).use { zis ->
                            var entry = zis.nextEntry
                            while (entry != null) {
                                val newFile = File(outputFolder, entry.name)
                                if (entry.isDirectory) {
                                    newFile.mkdirs()
                                } else {
                                    newFile.parentFile?.mkdirs()
                                    FileOutputStream(newFile).use { fos ->
                                        zis.copyTo(fos)
                                    }
                                }
                                zis.closeEntry()
                                entry = zis.nextEntry
                            }
                        }

                        tempZip.delete()
                        operationState = OperationState.Success(outputFolder.absolutePath)
                    } catch (e: Exception) {
                        operationState = OperationState.Error("Failed to extract ZIP: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Extract ZIP")
            }

            val success = operationState as? OperationState.Success
            if (success != null) {
                ResultCard(
                    title = "Extraction Complete",
                    message = "Files extracted successfully",
                    isSuccess = true,
                    outputPath = success.outputPath
                )
            } else if (operationState is OperationState.Error) {
                val error = operationState as OperationState.Error
                ResultCard(
                    title = "Extraction Failed",
                    message = error.message,
                    isSuccess = false
                )
            }
        }
    }
}
