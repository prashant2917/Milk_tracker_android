package com.swarajya.milktracker.tracker.domain.usecase

import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLogsForMonthUseCase @Inject constructor(
    private val repository: TrackerRepository
) {
    operator fun invoke(yearMonth: String): Flow<List<MilkLogEntity>> {
        return repository.getLogsForMonth(yearMonth)
    }
}
