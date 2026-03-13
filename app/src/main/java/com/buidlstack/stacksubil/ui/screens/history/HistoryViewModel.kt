package com.buidlstack.stacksubil.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buidlstack.stacksubil.data.db.entity.HistoryEntity
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(app: Application) : AndroidViewModel(app) {

    private val historyRepo = (app as BuildStackApplication).historyRepository

    val history: StateFlow<List<HistoryEntity>> = historyRepo.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearHistory() {
        viewModelScope.launch { historyRepo.clearAll() }
    }
}
