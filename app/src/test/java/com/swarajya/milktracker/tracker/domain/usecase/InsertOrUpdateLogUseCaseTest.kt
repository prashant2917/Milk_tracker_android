package com.swarajya.milktracker.tracker.domain.usecase

import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class InsertOrUpdateLogUseCaseTest {

    @Mock
    private lateinit var repository: TrackerRepository

    private lateinit var insertOrUpdateLogUseCase: InsertOrUpdateLogUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        insertOrUpdateLogUseCase = InsertOrUpdateLogUseCase(repository)
    }

    @Test
    fun `invoke calls repository insertOrUpdateLog`() = runTest {
        val log = MilkLogEntity("2023-10-27", 1.0f, 0.5f, 60.0f)
        insertOrUpdateLogUseCase(log)
        verify(repository).insertOrUpdateLog(log)
    }
}
