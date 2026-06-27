package com.swarajya.milktracker.settings.presentation

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.data.manager.PreferenceManager
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager
import com.swarajya.milktracker.common.domain.manager.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val preferenceManager: PreferenceManager,
    private val analyticsManager: AnalyticsManager,
    application: Application
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean?> = themeManager.isDarkTheme

    val isNotificationsEnabled: StateFlow<Boolean> = preferenceManager.isNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val defaultPrice: StateFlow<Float> = preferenceManager.pricePerLitre
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60.0f)

    fun onThemeChanged(isDark: Boolean) {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_TOGGLE,
            mapOf(AnalyticsConstants.Keys.KEY_TOGGLE_STATE to isDark.toString())
        )
        themeManager.setTheme(isDark)
    }

    fun onNotificationToggle(enabled: Boolean) {
        viewModelScope.launch {
            analyticsManager.logEvent(
                AnalyticsConstants.Events.EVENT_TOGGLE,
                mapOf(AnalyticsConstants.Keys.KEY_TOGGLE_STATE to enabled.toString())
            )
            preferenceManager.setNotificationsEnabled(enabled)
        }
    }

    fun onPriceChanged(price: Float) {
        viewModelScope.launch {
            analyticsManager.logEvent(
                AnalyticsConstants.Events.EVENT_TEXT_CHANGE,
                mapOf(AnalyticsConstants.Keys.KEY_TEXT_VALUE to price.toString())
            )
            preferenceManager.setPricePerLitre(price)
        }
    }

    val appVersion: String = try {
        val packageManager = application.packageManager
        val packageName = application.packageName
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName ?: "Unknown"
    } catch (e: Exception) {
        "Unknown"
    }

    fun logScreenViewEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_SETTING)
        )
    }
}
