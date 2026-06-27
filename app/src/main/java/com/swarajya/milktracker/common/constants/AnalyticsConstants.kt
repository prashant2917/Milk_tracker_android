package com.swarajya.milktracker.common.constants

object AnalyticsConstants {
    object Events {

        const val EVENT_SCREEN_VIEW = "screen_view"
        const val EVENT_BUTTON_CLICK = "button_click"
        const val EVENT_TOGGLE = "toggle"
        const val EVENT_TEXT_CHANGE = "text_change"
    }

    object Keys {
        const val KEY_SCREEN_NAME = "screen_name"
        const val KEY_BUTTON_NAME = "button_name"
        const val KEY_TOGGLE_STATE = "toggle_state"
        const val KEY_TEXT_VALUE = "text_value"

    }

    object Params {

        const val PARAM_SPLASH_SCREEN = "splash"

        const val PARAM_ADD_MILK_ENTRY_BOTTOM_SHEET = "add_milk_entry_bottom_sheet"
        const val PARAM_MONTHLY_CALENDAR = "monthly_calendar"
        const val PARAM_SETTING = "setting"

        const val PARAM_BUTTON_SAVE = "save"

        const val PARAM_BUTTON_CANCEL = "cancel"
        const val PARAM_BUTTON_DELETE = "delete"

        const val PARAM_BUTTON_MINUS = "minus"
        const val PARAM_BUTTON_PLUS = "plus"
        const val PARAM_DAY = "day"
        const val PARAM_PREV_MONTH = "prev_month"
        const val PARAM_NEXT_MONTH = "next_month"

        const val PARAM_BACK_BUTTON = "back_button"

    }


}