package com.example.waterbuddy.features.insights.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HydrationInsightsTest {

    @Test
    fun `completionPercentage returns correct integer value`() {
        val insights = HydrationInsights(
            averageIntake = 1000,
            completionRate = 0.825f,
            longestStreak = 5,
            peakDay = null,
            peakDayIntake = 0,
            weeklyTrend = emptyList(),
            monthlyTrend = emptyList()
        )

        insights.completionPercentage shouldBe 82
    }

    @Test
    fun `completionPercentage handles zero rate`() {
        val insights = HydrationInsights(
            averageIntake = 1000,
            completionRate = 0f,
            longestStreak = 5,
            peakDay = null,
            peakDayIntake = 0,
            weeklyTrend = emptyList(),
            monthlyTrend = emptyList()
        )

        insights.completionPercentage shouldBe 0
    }

    @Test
    fun `completionPercentage handles full rate`() {
        val insights = HydrationInsights(
            averageIntake = 1000,
            completionRate = 1f,
            longestStreak = 5,
            peakDay = null,
            peakDayIntake = 0,
            weeklyTrend = emptyList(),
            monthlyTrend = emptyList()
        )

        insights.completionPercentage shouldBe 100
    }
}
