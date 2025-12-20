package com.example.demometro.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.demometro.core.database.converter.DateTimeConverter
import com.example.demometro.features.watertracker.data.local.dao.DailyGoalDao
import com.example.demometro.features.watertracker.data.local.dao.WaterIntakeDao
import com.example.demometro.features.watertracker.data.local.entity.DailyGoalEntity
import com.example.demometro.features.watertracker.data.local.entity.WaterIntakeEntity

@Database(entities = [WaterIntakeEntity::class, DailyGoalEntity::class], version = 1)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun dailyGoalDao(): DailyGoalDao
}