package com.toolbox.domain.model

import com.toolbox.core.OperationState

data class Tool(
    val id: String,
    val name: String,
    val description: String,
    val category: ToolCategory,
    val iconRes: String? = null,
    val requiresInputFile: Boolean = true,
    val supportedExtensions: List<String> = emptyList(),
    val screen: ToolScreen = ToolScreen.Empty
)

sealed interface ToolScreen {
    data object Empty : ToolScreen
}

data class HistoryRecord(
    val id: Long = 0,
    val toolId: String,
    val toolName: String,
    val category: ToolCategory,
    val inputPath: String?,
    val outputPath: String?,
    val status: OperationState,
    val timestamp: Long = System.currentTimeMillis(),
    val sizeBytes: Long? = null
)
