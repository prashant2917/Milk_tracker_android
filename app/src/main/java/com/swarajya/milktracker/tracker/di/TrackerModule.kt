package com.swarajya.milktracker.tracker.di

import android.content.Context
import androidx.room.Room
import com.swarajya.milktracker.tracker.data.MilkDatabase
import com.swarajya.milktracker.tracker.data.MilkLogDao
import com.swarajya.milktracker.tracker.data.repository.TrackerRepositoryImpl
import com.swarajya.milktracker.tracker.domain.repository.TrackerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackerModule {

    @Provides
    @Singleton
    fun provideMilkDatabase(@ApplicationContext context: Context): MilkDatabase {
        return Room.databaseBuilder(
            context,
            MilkDatabase::class.java,
            "milk_tracker_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMilkLogDao(database: MilkDatabase): MilkLogDao {
        return database.milkLogDao()
    }

    @Provides
    @Singleton
    fun provideTrackerRepository(dao: MilkLogDao): TrackerRepository {
        return TrackerRepositoryImpl(dao)
    }
}
