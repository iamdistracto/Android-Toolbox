package com.toolbox.ui.tools.tiny

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.ui.components.ResultCard
import com.toolbox.core.OperationState

@Composable
fun ColorInfoTool() {
    var hexInput by remember { mutableStateOf("#1976D2") }
    var colorResult by remember { mutableStateOf<String?>(null) }
    var operationState by remember { mutableStateOf<OperationState>(OperationState.Idle) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = hexInput,
            onValueChange = {
                hexInput = it
                colorResult = null
                operationState = OperationState.Idle
            },
            label = { Text("Hex Color (e.g. #RRGGBB)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        OutlinedButton(
            onClick = {
                operationState = OperationState.Processing
                try {
                    val hex = hexInput.trim()
                        val colorInt = android.graphics.Color.parseColor(hex)
                        val color = androidx.compose.ui.graphics.Color(
                            android.graphics.Color.red(colorInt) / 255f,
                            android.graphics.Color.green(colorInt) / 255f,
                            android.graphics.Color.blue(colorInt) / 255f,
                            android.graphics.Color.alpha(colorInt) / 255f
                        )
                        val r = (color.red * 255).toInt()
                        val g = (color.green * 255).toInt()
                        val b = (color.blue * 255).toInt()
                        val a = (color.alpha * 255).toInt()

                    colorResult = buildString {
                        append("Hex: $hex\n")
                        append("RGB: rgb($r, $g, $b)\n")
                        append("RGBA: rgba($r, $g, $b, ${String.format("%.2f", color.alpha)})\n")
                        append("ARGB: #${String.format("%02X", a)}${String.format("%02X", r)}${String.format("%02X", g)}${String.format("%02X", b)}\n")
                    }
                    operationState = OperationState.Success()
                } catch (e: Exception) {
                    colorResult = null
                    operationState = OperationState.Error("Invalid hex color: ${e.message}", e)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert Color")
        }

        colorResult?.let { result ->
            val isSuccess = operationState is OperationState.Success
            val message = if (isSuccess) result else (operationState as OperationState.Error).message
            ResultCard(
                title = "Color Info",
                message = message,
                isSuccess = isSuccess
            )
        }
    }
}
