package com.example.demometro.features.water.domain.repository

import com.example.demometro.features.water.domain.model.DailyWaterStats
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface WaterRepository {
    fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats>
    suspend fun addWaterIntake(amountMl: Int, note: String? = null): Result<Unit>
    suspend fun deleteWaterIntake(id: String): Result<Unit>
    suspend fun updateDailyGoal(goalMl: Int): Result<Unit>
    suspend fun getDailyGoal(): Int
}