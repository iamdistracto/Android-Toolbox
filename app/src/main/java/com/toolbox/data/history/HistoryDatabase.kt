package com.toolbox.data.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.toolbox.core.OperationStateConverter
import com.toolbox.domain.model.ToolCategory

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(OperationStateConverter::class, ToolCategoryConverter::class)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    com.toolbox.core.Constants.HISTORY_DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
