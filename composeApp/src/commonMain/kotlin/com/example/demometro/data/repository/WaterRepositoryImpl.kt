package com.example.demometro.data.repository

import com.example.demometro.domain.model.DailyWaterStats
import com.example.demometro.domain.model.WaterIntake
import com.example.demometro.domain.repository.WaterRepository
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import com.example.demometro.di.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class WaterRepositoryImpl : WaterRepository {

    // In-memory storage for demo purposes
    // In production, this would use Room database
    private val waterIntakes = MutableStateFlow<List<WaterIntake>>(emptyList())
    private var dailyGoalMl = 2000 // Default goal: 2 liters

    override fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats> {
        return waterIntakes.map { intakes ->
            val todayIntakes = intakes.filter { intake ->
                val intakeDate = intake.timestamp.date
                intakeDate == date
            }

            DailyWaterStats(
                totalMl = todayIntakes.sumOf { it.amountMl },
                goalMl = dailyGoalMl,
                entries = todayIntakes.sortedByDescending { it.timestamp }
            )
        }
    }

    override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> {
        return try {
            val now = Clock.System.now()
            val newIntake = WaterIntake(
                id = now.toEpochMilliseconds().toString(),
                amountMl = amountMl,
                timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault()),
                note = note
            )
            waterIntakes.value += newIntake
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWaterIntake(id: String): Result<Unit> {
        return try {
            waterIntakes.value = waterIntakes.value.filterNot { it.id == id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDailyGoal(goalMl: Int): Result<Unit> {
        return try {
            dailyGoalMl = goalMl
            // Trigger refresh by emitting current value
            waterIntakes.value = waterIntakes.value.toList()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyGoal(): Int = dailyGoalMl
}
