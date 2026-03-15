package com.swarajya.milktracker.tracker.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.usecase.GetLogForDateUseCase
import com.swarajya.milktracker.tracker.domain.usecase.GetLogsForMonthUseCase
import com.swarajya.milktracker.tracker.domain.usecase.InsertOrUpdateLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val insertOrUpdateLogUseCase: InsertOrUpdateLogUseCase,
    private val getLogForDateUseCase: GetLogForDateUseCase,
    private val getLogsForMonthUseCase: GetLogsForMonthUseCase
) : ViewModel() {

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()


    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val milkLogs: StateFlow<Map<LocalDate, MilkLogEntity>> = _currentMonth
        .flatMapLatest { yearMonth ->
            getLogsForMonthUseCase(yearMonth.toString())
        }
        .map { logs ->
            logs.associateBy { LocalDate.parse(it.date) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun onToggleBottomSheet(show: Boolean) {
        _showBottomSheet.value = show
    }

    fun onMonthChange(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
    }

    fun saveMilkLog(date: String, morningQty: Float, eveningQty: Float, price: Float) {
        // Guard check: Only save if at least one quantity is > 0
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

    sealed class UiEvent {
        object Success : UiEvent()
        object Error : UiEvent()
    }
}
