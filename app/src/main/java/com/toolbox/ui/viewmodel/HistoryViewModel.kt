package com.toolbox.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toolbox.ToolboxApplication
import com.toolbox.domain.repository.HistoryRepository
import com.toolbox.domain.model.HistoryRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HistoryRepository = HistoryRepositoryImpl(
        (application as ToolboxApplication).database.historyDao()
    )

    private val _records = MutableStateFlow<List<HistoryRecord>>(emptyList())
    val records: StateFlow<List<HistoryRecord>> = _records.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllRecords().collect { list ->
                _records.value = list
            }
        }
    }

    fun delete(record: HistoryRecord) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
