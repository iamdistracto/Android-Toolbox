package com.toolbox.ui.tools.files

import android.content.ContentResolver
import android.database.Cursor
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FileInfoTool() {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var fileInfo by remember { mutableStateOf<String?>(null) }
    var operationState by remember { mutableStateOf<OperationState>(OperationState.Idle) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedUri = it
            fileName = it.lastPathSegment ?: "file"
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
            onPickClick = { launcher.launch("*/*") },
            label = "Select File"
        )

        selectedUri?.let { uri ->
            OutlinedButton(
                onClick = {
                    operationState = OperationState.Processing
                    try {
                        val resolver = context.contentResolver
                        val cursor = resolver.query(uri, null, null, null, null)
                        val result = StringBuilder()

                        result.append("File: ${fileName ?: "unknown"}\n")

                        cursor?.use {
                            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                            if (it.moveToFirst()) {
                                result.append("Display Name: ${it.getString(nameIndex) ?: "N/A"}\n")
                                result.append("Size: ${formatBytes(it.getLong(sizeIndex))}\n")
                            }
                        }

                        result.append("MIME Type: ${resolver.getType(uri) ?: "unknown"}\n")
                        result.append("URI: $uri\n")

                        fileInfo = result.toString()
                        operationState = OperationState.Success()
                    } catch (e: Exception) {
                        operationState = OperationState.Error("Failed to get file info: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get File Info")
            }

            fileInfo?.let { info ->
                val isSuccess = operationState is OperationState.Success
                val message = if (isSuccess) info else (operationState as OperationState.Error).message
                ResultCard(
                    title = "File Info",
                    message = message,
                    isSuccess = isSuccess
                )
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1 -> String.format("%.2f GB", gb)
        mb >= 1 -> String.format("%.2f MB", mb)
        kb >= 1 -> String.format("%.2f KB", kb)
        else -> "$bytes B"
    }
}
