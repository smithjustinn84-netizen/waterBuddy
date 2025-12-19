package com.example.demometro.domain.repository

import com.example.demometro.domain.model.DailyWaterStats
import com.example.demometro.domain.model.WaterIntake
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface WaterRepository {
    fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats>
    suspend fun addWaterIntake(amountMl: Int, note: String? = null): Result<Unit>
    suspend fun deleteWaterIntake(id: String): Result<Unit>
    suspend fun updateDailyGoal(goalMl: Int): Result<Unit>
    suspend fun getDailyGoal(): Int
}

