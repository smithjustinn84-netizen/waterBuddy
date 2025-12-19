package com.example.demometro.features.water.data.repository

import com.example.demometro.features.water.data.local.dao.DailyGoalDao
import com.example.demometro.features.water.data.local.dao.WaterIntakeDao
import com.example.demometro.features.water.data.local.entity.DailyGoalEntity
import com.example.demometro.features.water.data.local.entity.WaterIntakeEntity
import com.example.demometro.core.di.AppScope
import com.example.demometro.features.water.domain.model.DailyWaterStats
import com.example.demometro.features.water.domain.model.WaterIntake
import com.example.demometro.features.water.domain.repository.WaterRepository
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class WaterRepositoryImpl(
    private val waterIntakeDao: WaterIntakeDao,
    private val dailyGoalDao: DailyGoalDao
) : WaterRepository {

    override fun observeDailyStats(date: LocalDate): Flow<DailyWaterStats> {
        return combine(
            waterIntakeDao.getAllWaterIntakes(),
            dailyGoalDao.getDailyGoal()
        ) { intakes, goalEntity ->
            val todayIntakes = intakes.filter { it.timestamp.date == date }
            val goal = goalEntity?.goalMl ?: 2000

            DailyWaterStats(
                totalMl = todayIntakes.sumOf { it.amountMl },
                goalMl = goal,
                entries = todayIntakes.map { it.toDomain() }
            )
        }
    }

    override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> {
        return try {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.Companion.currentSystemDefault())
            val entity = WaterIntakeEntity(
                id = now.toEpochMilliseconds().toString(),
                amountMl = amountMl,
                timestamp = timestamp,
                note = note
            )
            waterIntakeDao.insertWaterIntake(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWaterIntake(id: String): Result<Unit> {
        return try {
            waterIntakeDao.deleteWaterIntakeById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDailyGoal(goalMl: Int): Result<Unit> {
        return try {
            dailyGoalDao.insertDailyGoal(DailyGoalEntity(goalMl = goalMl))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyGoal(): Int {
        return dailyGoalDao.getGoalValue() ?: 2000
    }

    private fun WaterIntakeEntity.toDomain(): WaterIntake {
        return WaterIntake(
            id = id,
            amountMl = amountMl,
            timestamp = timestamp,
            note = note
        )
    }
}