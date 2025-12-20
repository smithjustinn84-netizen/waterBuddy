package com.example.demometro.features.insights.domain.model

import com.example.demometro.features.watertracker.domain.model.DailyWaterStats
import kotlinx.datetime.LocalDate

data class HydrationInsights(
    val averageIntake: Int,
    val completionRate: Float,
    val longestStreak: Int,
    val peakDay: LocalDate?,
    val peakDayIntake: Int,
    val weeklyTrend: List<DailyWaterStats>,
    val monthlyTrend: List<DailyWaterStats>
)