package com.swarajya.milktracker.tracker.domain.usecase

import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import javax.inject.Inject

class InsertOrUpdateLogUseCase @Inject constructor(
    private val repository: TrackerRepository
) {
    suspend operator fun invoke(log: MilkLogEntity) {
        repository.insertOrUpdateLog(log)
    }
}
