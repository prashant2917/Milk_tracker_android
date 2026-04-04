package com.swarajya.milktracker.tracker.domain.usecase

import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class DeleteLogUseCaseTest {

    @Mock
    private lateinit var repository: TrackerRepository

    private lateinit var deleteLogUseCase: DeleteLogUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deleteLogUseCase = DeleteLogUseCase(repository)
    }

    @Test
    fun `invoke calls repository deleteLogForDate`() = runTest {
        val date = "2023-10-27"
        deleteLogUseCase(date)
        verify(repository).deleteLogForDate(date)
    }
}
