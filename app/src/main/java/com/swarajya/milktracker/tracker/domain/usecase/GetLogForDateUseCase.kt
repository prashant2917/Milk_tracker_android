package com.swarajya.milktracker.tracker.domain.usecase

import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLogForDateUseCase @Inject constructor(
    private val repository: TrackerRepository
) {
    operator fun invoke(date: String): Flow<MilkLogEntity?> {
        return repository.getLogForDate(date)
    }
}
