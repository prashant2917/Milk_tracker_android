package com.swarajya.milktracker.splash.presentation

import androidx.lifecycle.ViewModel
import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val analyticsManager: AnalyticsManager) :
    ViewModel() {

    fun logSplashScreenOpenEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_SPLASH_SCREEN)
        )
    }

}