package com.toolbox.domain.repository

import com.toolbox.domain.model.HistoryRecord
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllRecords(): Flow<List<HistoryRecord>>
    suspend fun insert(record: HistoryRecord): Long
    suspend fun delete(record: HistoryRecord)
    suspend fun deleteAll()
}
