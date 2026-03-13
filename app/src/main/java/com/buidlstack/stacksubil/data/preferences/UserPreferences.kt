package com.buidlstack.stacksubil.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val KEY_IS_METRIC = booleanPreferencesKey("is_metric")
        val KEY_THEME = stringPreferencesKey("theme")
    }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAS_SEEN_ONBOARDING] ?: false
    }

    val isMetric: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_METRIC] ?: true
    }

    val theme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME] ?: "dark"
    }

    suspend fun setHasSeenOnboarding(value: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_HAS_SEEN_ONBOARDING] = value }
    }

    suspend fun setIsMetric(value: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_IS_METRIC] = value }
    }

    suspend fun setTheme(value: String) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME] = value }
    }
}
