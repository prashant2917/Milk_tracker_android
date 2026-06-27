package com.swarajya.milktracker.splash.presentation

import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SplashViewModelTest {

    @Mock
    private lateinit var analyticsManager: AnalyticsManager

    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SplashViewModel(analyticsManager)
    }

    @Test
    fun `logSplashScreenOpenEvent logs correct event`() {
        viewModel.logSplashScreenOpenEvent()
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_SPLASH_SCREEN)
        )
    }

    @Test
    fun `logSplashScreenOpenEvent is called multiple times`() {
        viewModel.logSplashScreenOpenEvent()
        viewModel.logSplashScreenOpenEvent()
        viewModel.logSplashScreenOpenEvent()

        verify(analyticsManager, times(3)).logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_SPLASH_SCREEN)
        )
    }

    @Test
    fun `logSplashScreenOpenEvent logs EVENT_SCREEN_VIEW event type`() {
        viewModel.logSplashScreenOpenEvent()
        verify(analyticsManager).logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_SPLASH_SCREEN)
        )
    }

    @Test
    fun `logSplashScreenOpenEvent logs PARAM_SPLASH_SCREEN screen name`() {
        viewModel.logSplashScreenOpenEvent()
        verify(analyticsManager).logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_SPLASH_SCREEN)
        )
    }
}
