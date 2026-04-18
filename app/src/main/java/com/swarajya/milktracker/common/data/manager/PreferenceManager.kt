package com.swarajya.milktracker.common.data.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
    private val defaultPrice = floatPreferencesKey("default_price")

    val isNotificationsEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[notificationsEnabled] ?: true
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[notificationsEnabled] = enabled
        }
    }

    val pricePerLitre: Flow<Float> = dataStore.data
        .map { preferences ->
            preferences[defaultPrice] ?: 60.0f
        }

    suspend fun setPricePerLitre(price: Float) {
        dataStore.edit { preferences ->
            preferences[defaultPrice] = price
        }
    }
}
