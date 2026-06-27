package com.swarajya.milktracker.tracker.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.data.manager.PreferenceManager
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager
import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.usecase.DeleteLogUseCase
import com.swarajya.milktracker.tracker.domain.usecase.GetLogsForMonthUseCase
import com.swarajya.milktracker.tracker.domain.usecase.InsertOrUpdateLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val insertOrUpdateLogUseCase: InsertOrUpdateLogUseCase,
    private val getLogsForMonthUseCase: GetLogsForMonthUseCase,
    private val deleteLogUseCase: DeleteLogUseCase,
    preferenceManager: PreferenceManager,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val milkLogs: StateFlow<Map<LocalDate, MilkLogEntity>> = _currentMonth
        .flatMapLatest { yearMonth ->
            getLogsForMonthUseCase(yearMonth.toString())
        }
        .map { logs ->
            logs.associateBy { LocalDate.parse(it.date) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val defaultPrice: StateFlow<Float> = preferenceManager.pricePerLitre
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60.0f)

    fun onToggleBottomSheet(show: Boolean) {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_ADD_MILK_ENTRY_BOTTOM_SHEET)
        )
        _showBottomSheet.value = show
    }

    fun onMonthChange(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
    }

    fun saveMilkLog(date: String, morningQty: Float, eveningQty: Float, price: Float) {
        if (morningQty <= 0f && eveningQty <= 0f) return

        viewModelScope.launch {
            try {
                insertOrUpdateLogUseCase(
                    MilkLogEntity(
                        date = date,
                        morningQty = morningQty,
                        eveningQty = eveningQty,
                        pricePerLiter = price
                    )
                )
                _uiEvent.emit(UiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error)
            }
        }
    }

    fun deleteMilkLog(date: String) {
        viewModelScope.launch {
            try {
                deleteLogUseCase(date)
                _uiEvent.emit(UiEvent.Success)
                onToggleBottomSheet(false)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.Error)
            }
        }
    }

    fun logScreenViewEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_MONTHLY_CALENDAR)
        )
    }

    fun logMonthChangeEvent(paramValue: String) {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to paramValue)
        )
    }

    fun logCalendarCellClickEvent(paramValue: String) {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to paramValue)
        )
    }

    fun logSaveClickEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_BUTTON_SAVE)
        )
    }

    fun logDeleteClickEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_BUTTON_DELETE)
        )
    }

    fun logCancelClickEvent() {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_BUTTON_CANCEL)
        )
    }

    fun logQuantityChangeClickEvent(paramValue: String) {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to paramValue)
        )
    }

    fun logPriceChangeClickEvent(paramValue: String) {
        analyticsManager.logEvent(
            AnalyticsConstants.Events.EVENT_TEXT_CHANGE,
            mapOf(AnalyticsConstants.Keys.KEY_TEXT_VALUE to paramValue)
        )
    }


    sealed class UiEvent {
        data object Success : UiEvent()
        data object Error : UiEvent()
    }
}
