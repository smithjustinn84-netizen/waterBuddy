package com.example.demometro.features.water.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.demometro.features.water.data.local.entity.DailyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Query("SELECT * FROM daily_goal WHERE id = 1")
    fun getDailyGoal(): Flow<DailyGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertDailyGoal(dailyGoal: DailyGoalEntity)

    @Query("SELECT goalMl FROM daily_goal WHERE id = 1")
    suspend fun getGoalValue(): Int?
}