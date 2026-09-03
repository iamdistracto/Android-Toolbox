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
import java.util.zip.ZipOutputStream

@Composable
fun ZipTool() {
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var outputName by remember { mutableStateOf("archive.zip") }
    var outputPath by remember { mutableStateOf<String?>(null) }
    var operationState by remember { mutableStateOf<OperationState>(OperationState.Idle) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedUris = uris
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilePickerButton(
            filePath = if (selectedUris.size == 1) selectedUris.first().lastPathSegment else "${selectedUris.size} files selected",
            onPickClick = { launcher.launch("*/*") },
            label = "Select Files to Zip"
        )

        TextField(
            value = outputName,
            onValueChange = { outputName = it },
            label = { Text("Output ZIP Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (selectedUris.isNotEmpty()) {
            OutlinedButton(
                onClick = {
                    operationState = OperationState.Processing
                    try {
                        val outputDir = context.getExternalFilesDir(null)
                        val outputFile = File(outputDir, outputName)
                        var itemCount = 0

                        ZipOutputStream(FileOutputStream(outputFile)).use { zos ->
                            selectedUris.forEachIndexed { index, uri ->
                                val fileName = uri.lastPathSegment ?: "file_$index"
                                val tempFile = File(context.cacheDir, "zip_src_$index")
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    FileOutputStream(tempFile).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                zipFile(tempFile, fileName, zos)
                                tempFile.delete()
                                itemCount++
                            }
                        }

                        outputPath = outputFile.absolutePath
                        operationState = OperationState.Success(outputFile.absolutePath)
                    } catch (e: Exception) {
                        operationState = OperationState.Error("Failed to create ZIP: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create ZIP Archive")
            }

            val success = operationState as? OperationState.Success
            if (success != null) {
                ResultCard(
                    title = "ZIP Created",
                    message = "Archive created with ${selectedUris.size} items",
                    isSuccess = true,
                    outputPath = success.outputPath
                )
            } else if (operationState is OperationState.Error) {
                val error = operationState as OperationState.Error
                ResultCard(
                    title = "ZIP Failed",
                    message = error.message,
                    isSuccess = false
                )
            }
        }
    }
}

private fun zipFile(file: File, entryName: String, zos: ZipOutputStream) {
    if (file.isDirectory) {
        val children = file.listFiles() ?: return
        for (child in children) {
            zipFile(child, "$entryName/${child.name}", zos)
        }
    } else {
        FileInputStream(file).use { fis ->
            val entry = ZipEntry(entryName)
            zos.putNextEntry(entry)
            fis.copyTo(zos)
            zos.closeEntry()
        }
    }
}
