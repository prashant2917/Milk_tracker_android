package com.swarajya.milktracker.settings.presentation

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import app.cash.turbine.test
import com.swarajya.milktracker.common.data.manager.PreferenceManager
import com.swarajya.milktracker.common.domain.manager.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var themeManager: ThemeManager

    @Mock
    private lateinit var preferenceManager: PreferenceManager

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var packageManager: PackageManager

    private lateinit var viewModel: SettingsViewModel

    private val isDarkThemeFlow = MutableStateFlow<Boolean?>(null)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Default mock behaviors
        `when`(themeManager.isDarkTheme).thenReturn(isDarkThemeFlow)
        `when`(preferenceManager.isNotificationsEnabled).thenReturn(flowOf(true))
        `when`(application.packageManager).thenReturn(packageManager)
        `when`(application.packageName).thenReturn("com.swarajya.milktracker")

        val packageInfo = PackageInfo().apply { versionName = "1.0.0" }
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(packageInfo)

        viewModel = SettingsViewModel(themeManager, preferenceManager, application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isDarkTheme emission of true state`() = runTest {
        isDarkThemeFlow.value = true
        viewModel.isDarkTheme.test {
            assertEquals(true, awaitItem())
        }
    }

    @Test
    fun `isDarkTheme emission of false state`() = runTest {
        isDarkThemeFlow.value = false
        viewModel.isDarkTheme.test {
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `isDarkTheme emission of null state`() = runTest {
        isDarkThemeFlow.value = null
        viewModel.isDarkTheme.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `isNotificationsEnabled initial value check`() = runTest {
        viewModel.isNotificationsEnabled.test {
            assertEquals(true, awaitItem())
        }
    }

    @Test
    fun `onThemeChanged true delegation`() {
        viewModel.onThemeChanged(true)
        verify(themeManager).setTheme(true)
    }

    @Test
    fun `onThemeChanged false delegation`() {
        viewModel.onThemeChanged(false)
        verify(themeManager).setTheme(false)
    }

    @Test
    fun `onNotificationToggle persistence call`() = runTest {
        viewModel.onNotificationToggle(false)
        advanceUntilIdle()
        verify(preferenceManager).setNotificationsEnabled(false)
    }

    @Test
    fun `appVersion returns correct version from package manager`() {
        assertEquals("1.0.0", viewModel.appVersion)
    }

    @Test
    fun `appVersion returns Unknown when package manager throws exception`() {
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenThrow(RuntimeException())
        
        // Re-instantiate to trigger init block
        val vm = SettingsViewModel(themeManager, preferenceManager, application)
        assertEquals("Unknown", vm.appVersion)
    }
}
