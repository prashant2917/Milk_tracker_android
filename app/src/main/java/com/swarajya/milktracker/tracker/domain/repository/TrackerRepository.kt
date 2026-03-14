package com.swarajya.milktracker.tracker.domain.repository

import com.swarajya.milktracker.tracker.data.MilkLogEntity
import kotlinx.coroutines.flow.Flow

interface TrackerRepository {
    suspend fun insertOrUpdateLog(log: MilkLogEntity)
    fun getLogForDate(date: String): Flow<MilkLogEntity?>
    fun getLogsForMonth(yearMonth: String): Flow<List<MilkLogEntity>>
}
