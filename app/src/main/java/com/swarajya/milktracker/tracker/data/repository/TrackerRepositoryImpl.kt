package com.swarajya.milktracker.tracker.data.repository

import com.swarajya.milktracker.tracker.data.MilkLogDao
import com.swarajya.milktracker.tracker.data.MilkLogEntity
import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackerRepositoryImpl @Inject constructor(
    private val dao: MilkLogDao
) : TrackerRepository {

    override suspend fun insertOrUpdateLog(log: MilkLogEntity) {
        dao.insertOrUpdateLog(log)
    }

    override fun getLogForDate(date: String): Flow<MilkLogEntity?> {
        return dao.getLogForDate(date)
    }

    override fun getLogsForMonth(yearMonth: String): Flow<List<MilkLogEntity>> {
        return dao.getLogsForMonth(yearMonth)
    }
}
