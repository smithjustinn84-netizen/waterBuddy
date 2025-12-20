package com.example.demometro.features.water.domain.model

import kotlinx.datetime.LocalDate

data class DailyWaterStats(
    val date: LocalDate,
    val totalMl: Int,
    val goalMl: Int,
    val entries: List<WaterIntake>
) {
    val progressPercentage: Float
        get() = if (goalMl > 0) (totalMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f) else 0f

    val isGoalReached: Boolean
        get() = totalMl >= goalMl

    val remainingMl: Int
        get() = (goalMl - totalMl).coerceAtLeast(0)
}