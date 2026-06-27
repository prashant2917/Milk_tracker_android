package com.swarajya.milktracker.common.data.manager

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager

class FirebaseAnalyticsManager(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsManager {


    override fun logEvent(
        eventName: String,
        params: Map<String, String>
    ) {

        val bundle = Bundle()

        params.forEach {
            bundle.putString(it.key, it.value)
        }


        firebaseAnalytics.logEvent(
            eventName,
            bundle
        )
    }
}