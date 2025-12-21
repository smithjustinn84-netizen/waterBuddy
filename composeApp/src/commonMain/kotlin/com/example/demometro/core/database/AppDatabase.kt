@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.example.demometro.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.demometro.core.database.converter.DateTimeConverter
import com.example.demometro.features.watertracker.data.local.dao.DailyGoalDao
import com.example.demometro.features.watertracker.data.local.dao.WaterIntakeDao
import com.example.demometro.features.watertracker.data.local.entity.DailyGoalEntity
import com.example.demometro.features.watertracker.data.local.entity.WaterIntakeEntity

@Database(entities = [WaterIntakeEntity::class, DailyGoalEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun dailyGoalDao(): DailyGoalDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}