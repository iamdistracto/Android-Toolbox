package com.toolbox.data.history

import androidx.room.TypeConverter

class OperationStateConverter {
    @TypeConverter
    fun fromOperationState(state: com.toolbox.core.OperationState): String {
        return when (state) {
            is com.toolbox.core.OperationState.Idle -> "idle"
            is com.toolbox.core.OperationState.Processing -> "processing"
            is com.toolbox.core.OperationState.Success -> "success:${state.outputPath ?: ""}"
            is com.toolbox.core.OperationState.Error -> "error:${state.message}"
        }
    }

    @TypeConverter
    fun toOperationState(value: String): com.toolbox.core.OperationState {
        val parts = value.split(":", limit = 2)
        return when (parts[0]) {
            "idle" -> com.toolbox.core.OperationState.Idle
            "processing" -> com.toolbox.core.OperationState.Processing
            "success" -> com.toolbox.core.OperationState.Success(parts.getOrNull(1))
            "error" -> com.toolbox.core.OperationState.Error(parts.getOrNull(1) ?: "Unknown error")
            else -> com.toolbox.core.OperationState.Idle
        }
    }
}
