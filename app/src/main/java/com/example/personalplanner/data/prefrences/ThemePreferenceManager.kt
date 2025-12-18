package com.example.personalplanner.data.prefrences

// In a new file, e.g., ThemePreferenceManager.kt


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Create DataStore instance using a delegate
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemePreferenceManager(context: Context) {

    // 2. Define the key for the dark theme boolean
    private val DARK_THEME_KEY = booleanPreferencesKey("is_dark_theme")
    private val dataStore = context.dataStore

    /**
     * Get the dark theme preference as a Flow.
     * Defaults to true (Dark Mode) if not set.
     */
    val isDarkThemeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: true // Default to true (Dark Mode)
        }

    /**
     * Save the dark theme preference.
     */
    suspend fun setDarkThemeEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isEnabled
        }
    }
}