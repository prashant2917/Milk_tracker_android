package com.swarajya.milktracker.common.domain.manager

interface AnalyticsManager {

    fun logEvent(
        eventName: String,
        params: Map<String, String> = emptyMap()
    )
}