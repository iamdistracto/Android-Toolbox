package com.toolbox.ui.tools.video

import android.content.Context
import android.media.MediaMetadataRetriever
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
import com.toolbox.ui.components.ResultCard
import com.toolbox.core.OperationState
import android.net.Uri

@Composable
fun VideoInfoTool() {
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
            fileName = it.lastPathSegment ?: "video"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        com.toolbox.ui.components.FilePickerButton(
            filePath = fileName,
            onPickClick = { launcher.launch("video/*") },
            label = "Select Video"
        )

        selectedUri?.let { uri ->
            OutlinedButton(
                onClick = {
                    operationState = OperationState.Processing
                    try {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(context, uri)
                        val result = StringBuilder()
                        result.append("File: ${fileName ?: "video"}\n\n")
                        result.append("Duration: ${formatDuration(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L)}\n")
                        result.append("Resolution: ${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "?"}x${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "?"}\n")
                        result.append("Bitrate: ${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLongOrNull()?.div(1000) ?: "?"} kbps\n")
                        result.append("MIME Type: ${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "?"}\n")
                        result.append("Rotation: ${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION) ?: "0"} degrees\n")
                        result.append("Frame Count: ${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT) ?: "?"}\n")
                        retriever.release()
                        infoResult = result.toString()
                        operationState = OperationState.Success()
                    } catch (e: Exception) {
                        operationState = OperationState.Error("Failed to read video info: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Video Info")
            }

            infoResult?.let { info ->
                val isSuccess = operationState is OperationState.Success
                val message = if (isSuccess) info else (operationState as OperationState.Error).message
                ResultCard(
                    title = "Video Info",
                    message = message,
                    isSuccess = isSuccess
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
