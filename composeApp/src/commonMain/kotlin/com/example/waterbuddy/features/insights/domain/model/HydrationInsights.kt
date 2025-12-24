package com.example.waterbuddy.features.insights.domain.model

import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import kotlinx.datetime.LocalDate

data class HydrationInsights(
    val averageIntake: Int,
    val totalIntake: Int,
    val completionRate: Float,
    val longestStreak: Int,
    val solsActive: Int,
    val totalRituals: Int,
    val averageRitualsPerSol: Float,
    val maxRitualAmount: Int,
    val peakDay: LocalDate?,
    val peakDayIntake: Int,
    val weeklyTrend: List<DailyWaterStats>,
    val monthlyTrend: List<DailyWaterStats>,
) {
    val completionPercentage: Int
        get() = (completionRate * 100).toInt()
}
