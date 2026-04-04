package com.swarajya.milktracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.swarajya.milktracker.common.constants.AppConstants
import com.swarajya.milktracker.common.notification.DailyReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MilkTrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleDailyReminder()
    }

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            AppConstants.REMINDER_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            AppConstants.DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, AppConstants.REMINDER_HOUR_OF_DAY)
            set(Calendar.MINUTE, AppConstants.REMINDER_MINUTE)
            set(Calendar.SECOND, AppConstants.REMINDER_SECOND)
        }

        val now = Calendar.getInstance()
        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis - now.timeInMillis
    }
}
