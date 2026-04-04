package com.swarajya.milktracker.tracker.data.repository

import com.swarajya.milktracker.tracker.data.MilkLogDao
import com.swarajya.milktracker.tracker.data.MilkLogEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import app.cash.turbine.test
import org.junit.Assert.assertEquals

class TrackerRepositoryImplTest {

    @Mock
    private lateinit var dao: MilkLogDao

    private lateinit var repository: TrackerRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = TrackerRepositoryImpl(dao)
    }

    @Test
    fun `insertOrUpdateLog calls dao insertOrUpdateLog`() = runTest {
        val log = MilkLogEntity("2023-10-27", 1.0f, 0.5f, 60.0f)
        repository.insertOrUpdateLog(log)
        verify(dao).insertOrUpdateLog(log)
    }

    @Test
    fun `getLogForDate returns log from dao`() = runTest {
        val date = "2023-10-27"
        val expectedLog = MilkLogEntity(date, 1.0f, 0.5f, 60.0f)
        `when`(dao.getLogForDate(date)).thenReturn(flowOf(expectedLog))

        repository.getLogForDate(date).test {
            assertEquals(expectedLog, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getLogsForMonth returns list from dao`() = runTest {
        val yearMonth = "2023-10"
        val logs = listOf(
            MilkLogEntity("2023-10-01", 1.0f, 0.0f, 60.0f),
            MilkLogEntity("2023-10-02", 1.0f, 1.0f, 60.0f)
        )
        `when`(dao.getLogsForMonth(yearMonth)).thenReturn(flowOf(logs))

        repository.getLogsForMonth(yearMonth).test {
            assertEquals(logs, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `deleteLogForDate calls dao deleteLogForDate`() = runTest {
        val date = "2023-10-27"
        repository.deleteLogForDate(date)
        verify(dao).deleteLogForDate(date)
    }
}
