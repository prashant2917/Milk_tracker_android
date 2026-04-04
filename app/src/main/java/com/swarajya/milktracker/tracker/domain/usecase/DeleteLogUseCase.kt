package com.swarajya.milktracker.tracker.domain.usecase

import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import javax.inject.Inject

class DeleteLogUseCase @Inject constructor(
    private val repository: TrackerRepository
) {
    suspend operator fun invoke(date: String) {
        repository.deleteLogForDate(date)
    }
}
