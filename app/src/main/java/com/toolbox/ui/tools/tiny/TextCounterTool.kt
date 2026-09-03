package com.toolbox.ui.tools.tiny

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
import android.net.Uri

@Composable
fun TextCounterTool() {
    var text by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var countResult by remember { mutableStateOf<String?>(null) }
    var operationState by remember { mutableStateOf<OperationState>(OperationState.Idle) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedUri = it
            fileName = it.lastPathSegment ?: "text"
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    text = stream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                } ?: run { text = "" }
            } catch (e: Exception) {
                text = ""
            }
        }
    }

    fun updateCount() {
        val words = if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
        val chars = text.length
        val lines = if (text.isBlank()) 0 else text.split("\n").size
        val charsNoSpaces = text.replace("\\s".toRegex(), "").length
        val sentences = text.split(Regex("[.!?]+")).filter { it.trim().isNotEmpty() }.size

        countResult = buildString {
            append("Characters: $chars\n")
            append("Characters (no spaces): $charsNoSpaces\n")
            append("Words: $words\n")
            append("Lines: $lines\n")
            append("Sentences: $sentences\n")
        }
        operationState = OperationState.Success()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilePickerButton(
            filePath = fileName,
            onPickClick = { launcher.launch("text/*") },
            label = "Load Text File"
        )

        TextField(
            value = text,
            onValueChange = {
                text = it
                countResult = null
                operationState = OperationState.Idle
            },
            label = { Text("Enter or paste text") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = 20
        )

        OutlinedButton(
            onClick = { updateCount() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Count")
        }

        countResult?.let { result ->
            ResultCard(
                title = "Text Statistics",
                message = result,
                isSuccess = true
            )
        }
    }
}
