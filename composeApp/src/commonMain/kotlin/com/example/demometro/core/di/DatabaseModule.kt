package com.example.demometro.core.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.demometro.core.database.AppDatabase
import com.example.demometro.features.watertracker.data.local.dao.DailyGoalDao
import com.example.demometro.features.watertracker.data.local.dao.WaterIntakeDao
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@ContributesTo(AppScope::class)
interface DatabaseModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
        return builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @Provides
    fun provideWaterIntakeDao(db: AppDatabase): WaterIntakeDao = db.waterIntakeDao()

    @Provides
    fun provideDailyGoalDao(db: AppDatabase): DailyGoalDao = db.dailyGoalDao()
}