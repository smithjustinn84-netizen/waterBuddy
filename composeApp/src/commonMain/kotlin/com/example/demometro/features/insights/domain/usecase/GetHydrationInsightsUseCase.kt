package com.example.demometro.features.insights.domain.usecase

import com.example.demometro.features.insights.domain.model.HydrationInsights
import com.example.demometro.features.water.domain.model.DailyWaterStats
import com.example.demometro.features.water.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Inject
class GetHydrationInsightsUseCase(
    private val waterRepository: WaterRepository
) {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<HydrationInsights> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val oneMonthAgo = today.minus(1, DateTimeUnit.MONTH)

        return waterRepository.observeStatsRange(oneMonthAgo, today).map { statsList ->
            calculateInsights(statsList, today)
        }
    }

    private fun calculateInsights(statsList: List<DailyWaterStats>, today: LocalDate): HydrationInsights {
        if (statsList.isEmpty()) {
            return HydrationInsights(0, 0f, 0, null, 0, emptyList(), emptyList())
        }

        val totalIntake = statsList.sumOf { it.totalMl }
        val averageIntake = if (statsList.isNotEmpty()) totalIntake / statsList.size else 0

        val daysMetGoal = statsList.count { it.isGoalReached }
        val completionRate = if (statsList.isNotEmpty()) daysMetGoal.toFloat() / statsList.size else 0f

        val longestStreak = calculateLongestStreak(statsList)

        val peakDayStat = statsList.maxByOrNull { it.totalMl }
        val peakDay = peakDayStat?.date
        val peakDayIntake = peakDayStat?.totalMl ?: 0

        val oneWeekAgo = today.minus(1, DateTimeUnit.WEEK)
        val weeklyTrend = statsList.filter { it.date >= oneWeekAgo }.sortedBy { it.date }
        val monthlyTrend = statsList.sortedBy { it.date }

        return HydrationInsights(
            averageIntake = averageIntake,
            completionRate = completionRate,
            longestStreak = longestStreak,
            peakDay = peakDay,
            peakDayIntake = peakDayIntake,
            weeklyTrend = weeklyTrend,
            monthlyTrend = monthlyTrend
        )
    }

    private fun calculateLongestStreak(statsList: List<DailyWaterStats>): Int {
        var maxStreak = 0
        var currentStreak = 0

        // Sort by date to ensure a correct streak calculation
        val sortedStats = statsList.sortedBy { it.date }

        for (stat in sortedStats) {
            if (stat.isGoalReached) {
                currentStreak++
            } else {
                maxStreak = maxOf(maxStreak, currentStreak)
                currentStreak = 0
            }
        }
        maxStreak = maxOf(maxStreak, currentStreak)

        return maxStreak
    }
}