package com.toolbox.core

sealed class OperationState {
    data object Idle : OperationState()
    data object Processing : OperationState()
    data class Success(val outputPath: String? = null) : OperationState()
    data class Error(val message: String, val cause: Throwable? = null) : OperationState()
}
