package com.swarajya.milktracker.common.data.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val notificationsEnabled = booleanPreferencesKey("notifications_enabled")

    val isNotificationsEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[notificationsEnabled] ?: true
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[notificationsEnabled] = enabled
        }
    }
}
