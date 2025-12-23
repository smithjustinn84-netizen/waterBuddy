package com.example.waterbuddy.features.watertracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.waterbuddy.features.watertracker.data.local.entity.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM water_intake ORDER BY timestamp DESC")
    fun getAllWaterIntakes(): Flow<List<WaterIntakeEntity>>

    @Query("SELECT * FROM water_intake WHERE date(timestamp) = date('now') ORDER BY timestamp DESC")
    fun getTodayWaterIntakes(): Flow<List<WaterIntakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterIntake(waterIntake: WaterIntakeEntity)

    @Delete
    suspend fun deleteWaterIntake(waterIntake: WaterIntakeEntity)

    @Query("DELETE FROM water_intake WHERE id = :id")
    suspend fun deleteWaterIntakeById(id: String)

    @Query("UPDATE water_intake SET amountMl = :amountMl WHERE id = :id")
    suspend fun updateWaterIntakeAmount(
        id: String,
        amountMl: Int,
    )

    @Query("SELECT SUM(amountMl) FROM water_intake WHERE date(timestamp) = date('now')")
    fun getTodayTotalWaterIntake(): Flow<Int?>
}
