@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.example.waterbuddy.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.waterbuddy.core.database.converter.DateTimeConverter
import com.example.waterbuddy.features.preferences.data.local.dao.ReminderSettingsDao
import com.example.waterbuddy.features.preferences.data.local.entity.ReminderSettingsEntity
import com.example.waterbuddy.features.watertracker.data.local.dao.DailyGoalDao
import com.example.waterbuddy.features.watertracker.data.local.dao.WaterIntakeDao
import com.example.waterbuddy.features.watertracker.data.local.entity.DailyGoalEntity
import com.example.waterbuddy.features.watertracker.data.local.entity.WaterIntakeEntity

@Database(
    entities = [
        WaterIntakeEntity::class,
        DailyGoalEntity::class,
        ReminderSettingsEntity::class,
    ],
    version = 2, // Incremented version
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao

    abstract fun dailyGoalDao(): DailyGoalDao

    abstract fun reminderSettingsDao(): ReminderSettingsDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
