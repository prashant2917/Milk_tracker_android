package com.swarajya.milktracker.tracker.presentation.viewmodel

import app.cash.turbine.test
import com.swarajya.milktracker.common.data.manager.PreferenceManager
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
            preferenceManager
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
}
