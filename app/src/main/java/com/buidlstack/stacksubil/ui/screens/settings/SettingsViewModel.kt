package com.buidlstack.stacksubil.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = (app as BuildStackApplication).userPreferences

    val isMetric: StateFlow<Boolean> = prefs.isMetric
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val theme: StateFlow<String> = prefs.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "dark")

    fun setIsMetric(value: Boolean) {
        viewModelScope.launch { prefs.setIsMetric(value) }
    }

    fun setTheme(value: String) {
        viewModelScope.launch { prefs.setTheme(value) }
    }
}
