package com.swarajya.milktracker.common.presentation

import androidx.lifecycle.ViewModel
import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(private val analyticsManager: AnalyticsManager) :
    ViewModel() {

    fun logBackClickEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK, mapOf(
                AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_BACK_BUTTON
            )
        )
    }

    fun logSettingClickEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK, mapOf(
                AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_SETTING
            )
        )
    }


}