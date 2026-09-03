package com.toolbox.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.toolbox.core.OperationState
import com.toolbox.domain.model.ToolCategory

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toolId: String,
    val toolName: String,
    val category: ToolCategory,
    val inputPath: String?,
    val outputPath: String?,
    val statusJson: String,
    val timestamp: Long,
    val sizeBytes: Long?
) {
    companion object {
        const val TABLE_NAME = "history"
    }
}
