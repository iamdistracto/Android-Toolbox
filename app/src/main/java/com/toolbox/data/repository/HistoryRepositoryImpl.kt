package com.toolbox.data.repository

import com.toolbox.core.OperationState
import com.toolbox.data.history.HistoryDao
import com.toolbox.data.history.HistoryEntity
import com.toolbox.domain.model.HistoryRecord
import com.toolbox.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(private val historyDao: HistoryDao) : HistoryRepository {
    override fun getAllRecords(): Flow<List<HistoryRecord>> {
        return historyDao.getAllRecords().map { entities ->
            entities.map { it.toRecord() }
        }
    }

    override suspend fun insert(record: HistoryRecord): Long {
        return historyDao.insert(record.toEntity())
    }

    override suspend fun delete(record: HistoryRecord) {
        historyDao.delete(record.toEntity())
    }

    override suspend fun deleteAll() {
        historyDao.deleteAll()
    }
}

private fun HistoryEntity.toRecord(): HistoryRecord {
    return HistoryRecord(
        id = id,
        toolId = toolId,
        toolName = toolName,
        category = category,
        inputPath = inputPath,
        outputPath = outputPath,
        status = parseOperationState(statusJson),
        timestamp = timestamp,
        sizeBytes = sizeBytes
    )
}

private fun HistoryRecord.toEntity(): HistoryEntity {
    return HistoryEntity(
        id = id,
        toolId = toolId,
        toolName = toolName,
        category = category,
        inputPath = inputPath,
        outputPath = outputPath,
        statusJson = serializeOperationState(status),
        timestamp = timestamp,
        sizeBytes = sizeBytes
    )
}

private fun parseOperationState(value: String): OperationState {
    val parts = value.split(":", limit = 2)
    return when (parts[0]) {
        "idle" -> OperationState.Idle
        "processing" -> OperationState.Processing
        "success" -> OperationState.Success(parts.getOrNull(1))
        "error" -> OperationState.Error(parts.getOrNull(1) ?: "Unknown error")
        else -> OperationState.Idle
    }
}

private fun serializeOperationState(state: OperationState): String {
    return when (state) {
        is OperationState.Idle -> "idle"
        is OperationState.Processing -> "processing"
        is OperationState.Success -> "success:${state.outputPath ?: ""}"
        is OperationState.Error -> "error:${state.message}"
    }
}
