package com.swarajya.milktracker.settings.presentation

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swarajya.milktracker.common.data.manager.PreferenceManager
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
    application: Application
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean?> = themeManager.isDarkTheme

    val isNotificationsEnabled: StateFlow<Boolean> = preferenceManager.isNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun onThemeChanged(isDark: Boolean) {
        themeManager.setTheme(isDark)
    }

    fun onNotificationToggle(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setNotificationsEnabled(enabled)
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
}
