package com.example.waterbuddy.features.watertracker.data.repository

import com.example.waterbuddy.core.di.AppScope
import com.example.waterbuddy.features.watertracker.data.local.dao.DailyGoalDao
import com.example.waterbuddy.features.watertracker.data.local.dao.WaterIntakeDao
import com.example.waterbuddy.features.watertracker.data.local.entity.DailyGoalEntity
import com.example.waterbuddy.features.watertracker.data.local.entity.WaterIntakeEntity
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.model.WaterIntake
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
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
                date = date,
                totalMl = todayIntakes.sumOf { it.amountMl },
                goalMl = goal,
                entries = todayIntakes.map { it.toDomain() }
            )
        }
    }

    override fun observeStatsRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyWaterStats>> {
        return combine(
            waterIntakeDao.getAllWaterIntakes(),
            dailyGoalDao.getDailyGoal()
        ) { intakes, goalEntity ->
            val goal = goalEntity?.goalMl ?: 2000
            val days = startDate.daysUntil(endDate) + 1

            (0 until days).map { offset ->
                val currentDate = startDate.plus(offset, DateTimeUnit.DAY)
                val dayIntakes = intakes.filter { it.timestamp.date == currentDate }

                DailyWaterStats(
                    date = currentDate,
                    totalMl = dayIntakes.sumOf { it.amountMl },
                    goalMl = goal,
                    entries = dayIntakes.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun addWaterIntake(amountMl: Int, note: String?): Result<Unit> {
        return try {
            val now = Clock.System.now()
            val timestamp = now.toLocalDateTime(TimeZone.currentSystemDefault())
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