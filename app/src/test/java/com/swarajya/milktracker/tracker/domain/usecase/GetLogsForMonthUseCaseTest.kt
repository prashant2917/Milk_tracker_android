package com.swarajya.milktracker.tracker.domain.usecase

import app.cash.turbine.test
import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetLogsForMonthUseCaseTest {

    @Mock
    private lateinit var repository: TrackerRepository

    private lateinit var getLogsForMonthUseCase: GetLogsForMonthUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getLogsForMonthUseCase = GetLogsForMonthUseCase(repository)
    }

    @Test
    fun `invoke returns list from repository`() = runTest {
        val yearMonth = "2023-10"
        val expectedLogs = listOf(
            MilkLogEntity("2023-10-01", 1.0f, 0.5f, 60.0f),
            MilkLogEntity("2023-10-02", 1.0f, 1.0f, 60.0f)
        )
        `when`(repository.getLogsForMonth(yearMonth)).thenReturn(flowOf(expectedLogs))

        getLogsForMonthUseCase(yearMonth).test {
            assertEquals(expectedLogs, awaitItem())
            awaitComplete()
        }
    }
}
