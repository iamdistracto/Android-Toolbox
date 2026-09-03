package com.toolbox.data.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.toolbox.domain.model.HistoryRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM ${HistoryEntity.TABLE_NAME} ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity): Long

    @Delete
    suspend fun delete(entity: HistoryEntity)

    @Query("DELETE FROM ${HistoryEntity.TABLE_NAME}")
    suspend fun deleteAll()
}
