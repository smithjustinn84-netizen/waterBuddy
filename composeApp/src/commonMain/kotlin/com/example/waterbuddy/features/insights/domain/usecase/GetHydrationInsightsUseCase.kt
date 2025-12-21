package com.example.waterbuddy.features.insights.domain.usecase

import com.example.waterbuddy.features.insights.domain.model.HydrationInsights
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Inject
class GetHydrationInsightsUseCase(
    private val waterRepository: WaterRepository,
) {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<HydrationInsights> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val oneMonthAgo = today.minus(1, DateTimeUnit.MONTH)

        return waterRepository.observeStatsRange(oneMonthAgo, today).map { statsList ->
            calculateInsights(statsList, today)
        }
    }

    private fun calculateInsights(
        statsList: List<DailyWaterStats>,
        today: LocalDate,
    ): HydrationInsights {
        if (statsList.isEmpty()) {
            return HydrationInsights(0, 0f, 0, null, 0, emptyList(), emptyList())
        }

        val totalIntake = statsList.sumOf { it.totalMl }
        val averageIntake = totalIntake / statsList.size

        val daysMetGoal = statsList.count { it.isGoalReached }
        val completionRate = daysMetGoal.toFloat() / statsList.size

        val longestStreak = calculateLongestStreak(statsList)

        // Using maxBy instead of maxByOrNull because statsList is guaranteed non-empty here.
        // This avoids "impossible" null branches that lower code coverage.
        val peakDayStat = statsList.maxBy { it.totalMl }
        val peakDay = peakDayStat.date
        val peakDayIntake = peakDayStat.totalMl

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
            monthlyTrend = monthlyTrend,
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
