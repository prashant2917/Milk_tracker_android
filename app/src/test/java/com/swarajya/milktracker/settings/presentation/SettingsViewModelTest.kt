package com.swarajya.milktracker.settings.presentation

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import app.cash.turbine.test
import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.data.manager.PreferenceManager
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager
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
import org.mockito.Captor
import org.mockito.ArgumentCaptor
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

    @Mock
    private lateinit var analyticsManager: AnalyticsManager

    private lateinit var viewModel: SettingsViewModel

    private val isDarkThemeFlow = MutableStateFlow<Boolean?>(null)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(themeManager.isDarkTheme).thenReturn(isDarkThemeFlow)
        `when`(preferenceManager.isNotificationsEnabled).thenReturn(flowOf(true))
        `when`(preferenceManager.pricePerLitre).thenReturn(flowOf(60.0f))
        `when`(application.packageManager).thenReturn(packageManager)
        `when`(application.packageName).thenReturn("com.swarajya.milktracker")

        val packageInfo = PackageInfo()
        packageInfo.versionName = "1.0.0"
        doReturn(packageInfo).`when`(packageManager).getPackageInfo("com.swarajya.milktracker", 0)

        viewModel = SettingsViewModel(themeManager, preferenceManager, analyticsManager, application)
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
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_TOGGLE,
            mapOf(AnalyticsConstants.Keys.KEY_TOGGLE_STATE to "true")
        )
    }

    @Test
    fun `onThemeChanged false delegation`() {
        viewModel.onThemeChanged(false)
        verify(themeManager).setTheme(false)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_TOGGLE,
            mapOf(AnalyticsConstants.Keys.KEY_TOGGLE_STATE to "false")
        )
    }

    @Test
    fun `onNotificationToggle persistence call`() = runTest {
        viewModel.onNotificationToggle(false)
        advanceUntilIdle()
        verify(preferenceManager).setNotificationsEnabled(false)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_TOGGLE,
            mapOf(AnalyticsConstants.Keys.KEY_TOGGLE_STATE to "false")
        )
    }

    @Test
    fun `onPriceChanged persistence call`() = runTest {
        viewModel.onPriceChanged(70.0f)
        advanceUntilIdle()
        verify(preferenceManager).setPricePerLitre(70.0f)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_TEXT_CHANGE,
            mapOf(AnalyticsConstants.Keys.KEY_TEXT_VALUE to "70.0")
        )
    }

    @Test
    fun `appVersion returns correct version from package manager`() {
        assertEquals("1.0.0", viewModel.appVersion)
    }

    @Test
    fun `appVersion returns Unknown when package manager throws exception`() {
        doThrow(RuntimeException()).`when`(packageManager).getPackageInfo("com.swarajya.milktracker", 0)

        val vm = SettingsViewModel(themeManager, preferenceManager, analyticsManager, application)
        assertEquals("Unknown", vm.appVersion)
    }

    @Test
    fun `logScreenViewEvent logs correct event`() {
        viewModel.logScreenViewEvent()
        verify(analyticsManager).logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_SETTING)
        )
    }
}
