package com.example.waterbuddy.features.insights.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HydrationInsightsTest {
    @Test
    fun `completionPercentage returns correct integer value`() {
        val insights =
            HydrationInsights(
                averageIntake = 1000,
                totalIntake = 7000,
                completionRate = 0.825f,
                longestStreak = 5,
                solsActive = 7,
                totalRituals = 14,
                averageRitualsPerSol = 2f,
                maxRitualAmount = 500,
                peakDay = null,
                peakDayIntake = 0,
                weeklyTrend = emptyList(),
                monthlyTrend = emptyList(),
            )

        insights.completionPercentage shouldBe 82
    }

    @Test
    fun `completionPercentage handles zero rate`() {
        val insights =
            HydrationInsights(
                averageIntake = 1000,
                totalIntake = 0,
                completionRate = 0f,
                longestStreak = 0,
                solsActive = 0,
                totalRituals = 0,
                averageRitualsPerSol = 0f,
                maxRitualAmount = 0,
                peakDay = null,
                peakDayIntake = 0,
                weeklyTrend = emptyList(),
                monthlyTrend = emptyList(),
            )

        insights.completionPercentage shouldBe 0
    }

    @Test
    fun `completionPercentage handles full rate`() {
        val insights =
            HydrationInsights(
                averageIntake = 1000,
                totalIntake = 7000,
                completionRate = 1f,
                longestStreak = 7,
                solsActive = 7,
                totalRituals = 21,
                averageRitualsPerSol = 3f,
                maxRitualAmount = 1000,
                peakDay = null,
                peakDayIntake = 0,
                weeklyTrend = emptyList(),
                monthlyTrend = emptyList(),
            )

        insights.completionPercentage shouldBe 100
    }
}
