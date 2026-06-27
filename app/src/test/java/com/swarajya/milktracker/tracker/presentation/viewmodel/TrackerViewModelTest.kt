package com.swarajya.milktracker.tracker.presentation.viewmodel

import app.cash.turbine.test
import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.data.manager.PreferenceManager
import com.swarajya.milktracker.common.domain.manager.AnalyticsManager
import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.usecase.DeleteLogUseCase
import com.swarajya.milktracker.tracker.domain.usecase.GetLogsForMonthUseCase
import com.swarajya.milktracker.tracker.domain.usecase.InsertOrUpdateLogUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var insertOrUpdateLogUseCase: InsertOrUpdateLogUseCase
    @Mock
    private lateinit var getLogsForMonthUseCase: GetLogsForMonthUseCase
    @Mock
    private lateinit var deleteLogUseCase: DeleteLogUseCase
    @Mock
    private lateinit var preferenceManager: PreferenceManager

    @Mock
    private lateinit var analyticsManager: AnalyticsManager

    private lateinit var viewModel: TrackerViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Default mock behaviors
        whenever(getLogsForMonthUseCase(any())).thenReturn(flowOf(emptyList()))
        whenever(preferenceManager.pricePerLitre).thenReturn(flowOf(60.0f))

        viewModel = TrackerViewModel(
            insertOrUpdateLogUseCase,
            getLogsForMonthUseCase,
            deleteLogUseCase,
            preferenceManager,
            analyticsManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onToggleBottomSheet updates showBottomSheet state`() = runTest {
        viewModel.onToggleBottomSheet(true)
        assertEquals(true, viewModel.showBottomSheet.value)

        viewModel.onToggleBottomSheet(false)
        assertEquals(false, viewModel.showBottomSheet.value)
    }

    @Test
    fun `onMonthChange updates currentMonth state`() = runTest {
        val newMonth = YearMonth.of(2024, 1)
        viewModel.onMonthChange(newMonth)
        assertEquals(newMonth, viewModel.currentMonth.value)
    }

    @Test
    fun `saveMilkLog emits Success event on successful save`() = runTest {
        val date = "2023-10-27"
        val morning = 1.0f
        val evening = 0.5f
        val price = 60.0f

        viewModel.uiEvent.test {
            viewModel.saveMilkLog(date, morning, evening, price)
            advanceUntilIdle()
            
            val expectedLog = MilkLogEntity(date, morning, evening, price)
            verify(insertOrUpdateLogUseCase).invoke(expectedLog)
            assertEquals(TrackerViewModel.UiEvent.Success, awaitItem())
        }
    }

    @Test
    fun `saveMilkLog emits Error event on exception`() = runTest {
        val date = "2023-10-27"
        whenever(insertOrUpdateLogUseCase.invoke(any())).thenThrow(RuntimeException())

        viewModel.uiEvent.test {
            viewModel.saveMilkLog(date, 1.0f, 1.0f, 60.0f)
            advanceUntilIdle()
            assertEquals(TrackerViewModel.UiEvent.Error, awaitItem())
        }
    }

    @Test
    fun `saveMilkLog does nothing if quantities are zero`() = runTest {
        viewModel.saveMilkLog("2023-10-27", 0f, 0f, 60.0f)
        advanceUntilIdle()
        verify(insertOrUpdateLogUseCase, never()).invoke(any())
    }

    @Test
    fun `deleteMilkLog emits Success event and hides sheet on success`() = runTest {
        val date = "2023-10-27"
        viewModel.onToggleBottomSheet(true)

        viewModel.uiEvent.test {
            viewModel.deleteMilkLog(date)
            advanceUntilIdle()
            
            verify(deleteLogUseCase).invoke(date)
            assertEquals(false, viewModel.showBottomSheet.value)
            assertEquals(TrackerViewModel.UiEvent.Success, awaitItem())
        }
    }

    @Test
    fun `milkLogs reflects transformed logs from use case`() = runTest {
        val dateStr = "2023-10-27"
        val date = LocalDate.parse(dateStr)
        val log = MilkLogEntity(dateStr, 1.0f, 1.0f, 60.0f)
        
        whenever(getLogsForMonthUseCase(any())).thenReturn(flowOf(listOf(log)))

        viewModel.milkLogs.test {
            // Initial emission
            assertEquals(emptyMap<LocalDate, MilkLogEntity>(), awaitItem())
            
            // Force refresh by changing month
            viewModel.onMonthChange(YearMonth.now().plusMonths(1))
            
            val result = awaitItem()
            assertEquals(log, result[date])
        }
    }

    @Test
    fun `defaultPrice reflects value from preferenceManager`() = runTest {
        viewModel.defaultPrice.test {
            assertEquals(60.0f, awaitItem())
        }
    }

    @Test
    fun `onToggleBottomSheet logs analytics event`() {
        viewModel.onToggleBottomSheet(true)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_ADD_MILK_ENTRY_BOTTOM_SHEET)
        )
    }

    @Test
    fun `deleteMilkLog emits Error event on exception`() = runTest {
        val date = "2023-10-27"
        whenever(deleteLogUseCase.invoke(date)).thenThrow(RuntimeException())

        viewModel.uiEvent.test {
            viewModel.deleteMilkLog(date)
            advanceUntilIdle()
            assertEquals(TrackerViewModel.UiEvent.Error, awaitItem())
        }
    }

    @Test
    fun `logScreenViewEvent logs correct event`() {
        viewModel.logScreenViewEvent()
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_SCREEN_VIEW,
            mapOf(AnalyticsConstants.Keys.KEY_SCREEN_NAME to AnalyticsConstants.Params.PARAM_MONTHLY_CALENDAR)
        )
    }

    @Test
    fun `logMonthChangeEvent logs correct button click event`() {
        val paramValue = AnalyticsConstants.Params.PARAM_PREV_MONTH
        viewModel.logMonthChangeEvent(paramValue)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to paramValue)
        )
    }

    @Test
    fun `logCalendarCellClickEvent logs correct button click event`() {
        val paramValue = AnalyticsConstants.Params.PARAM_DAY
        viewModel.logCalendarCellClickEvent(paramValue)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to paramValue)
        )
    }

    @Test
    fun `logSaveClickEvent logs save button click event`() {
        viewModel.logSaveClickEvent()
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_BUTTON_SAVE)
        )
    }

    @Test
    fun `logDeleteClickEvent logs delete button click event`() {
        viewModel.logDeleteClickEvent()
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_BUTTON_DELETE)
        )
    }

    @Test
    fun `logCancelClickEvent logs cancel button click event`() {
        viewModel.logCancelClickEvent()
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to AnalyticsConstants.Params.PARAM_BUTTON_CANCEL)
        )
    }

    @Test
    fun `logQuantityChangeClickEvent logs correct button click event`() {
        val paramValue = AnalyticsConstants.Params.PARAM_BUTTON_PLUS
        viewModel.logQuantityChangeClickEvent(paramValue)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_BUTTON_CLICK,
            mapOf(AnalyticsConstants.Keys.KEY_BUTTON_NAME to paramValue)
        )
    }

    @Test
    fun `logPriceChangeClickEvent logs text change event`() {
        val paramValue = "70.0"
        viewModel.logPriceChangeClickEvent(paramValue)
        verify(analyticsManager, times(1)).logEvent(
            AnalyticsConstants.Events.EVENT_TEXT_CHANGE,
            mapOf(AnalyticsConstants.Keys.KEY_TEXT_VALUE to paramValue)
        )
    }
}
