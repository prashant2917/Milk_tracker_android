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

class GetLogForDateUseCaseTest {

    @Mock
    private lateinit var repository: TrackerRepository

    private lateinit var getLogForDateUseCase: GetLogForDateUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getLogForDateUseCase = GetLogForDateUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val date = "2023-10-27"
        val expectedLog = MilkLogEntity(date, 1.0f, 0.5f, 60.0f)
        `when`(repository.getLogForDate(date)).thenReturn(flowOf(expectedLog))

        getLogForDateUseCase(date).test {
            assertEquals(expectedLog, awaitItem())
            awaitComplete()
        }
    }
}
