package com.example.waterbuddy.features.insights.domain.usecase

import app.cash.turbine.test
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class GetHydrationInsightsUseCaseTest {

    private val waterRepository = mock<WaterRepository>()
    private val useCase = GetHydrationInsightsUseCase(waterRepository)

    @Test
    fun `invoke returns insights based on repository stats`() = runTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val yesterday = today.minus(1, DateTimeUnit.DAY)
        val dayBeforeYesterday = today.minus(2, DateTimeUnit.DAY)

        val statsList = listOf(
            DailyWaterStats(dayBeforeYesterday, 2000, 2000, emptyList()), // Goal met
            DailyWaterStats(yesterday, 2500, 2000, emptyList()),          // Goal met
            DailyWaterStats(today, 1000, 2000, emptyList())               // Goal not met
        )

        every { waterRepository.observeStatsRange(any(), any()) } returns flowOf(statsList)

        useCase().test {
            val insights = awaitItem()

            // Average: (2000 + 2500 + 1000) / 3 = 1833 (Int division)
            insights.averageIntake shouldBe 1833
            // Completion rate: 2 / 3 = 0.666...
            insights.completionRate shouldBe (2f / 3f)
            // Longest streak: 2 (dayBeforeYesterday, yesterday)
            insights.longestStreak shouldBe 2
            // Peak day: yesterday (2500 ml)
            insights.peakDay shouldBe yesterday
            insights.peakDayIntake shouldBe 2500

            awaitComplete()
        }
    }

    @Test
    fun `calculateLongestStreak handles multiple streaks and identifies max`() = runTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        // Streak of 2, then break, then streak of 3
        val statsList = listOf(
            DailyWaterStats(today.minus(6, DateTimeUnit.DAY), 2000, 2000, emptyList()), // Yes
            DailyWaterStats(today.minus(5, DateTimeUnit.DAY), 2000, 2000, emptyList()), // Yes
            DailyWaterStats(today.minus(4, DateTimeUnit.DAY), 500, 2000, emptyList()),  // No
            DailyWaterStats(today.minus(3, DateTimeUnit.DAY), 2000, 2000, emptyList()), // Yes
            DailyWaterStats(today.minus(2, DateTimeUnit.DAY), 2000, 2000, emptyList()), // Yes
            DailyWaterStats(today.minus(1, DateTimeUnit.DAY), 2000, 2000, emptyList()), // Yes
            DailyWaterStats(today, 500, 2000, emptyList())                             // No
        )

        every { waterRepository.observeStatsRange(any(), any()) } returns flowOf(statsList)

        useCase().test {
            val insights = awaitItem()
            insights.longestStreak shouldBe 3
            awaitComplete()
        }
    }

    @Test
    fun `weeklyTrend and monthlyTrend are correctly filtered and sorted`() = runTest {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val tenDaysAgo = today.minus(10, DateTimeUnit.DAY)
        val fiveDaysAgo = today.minus(5, DateTimeUnit.DAY)

        // Input list is unsorted
        val statsList = listOf(
            DailyWaterStats(today, 2000, 2000, emptyList()),
            DailyWaterStats(tenDaysAgo, 1500, 2000, emptyList()),
            DailyWaterStats(fiveDaysAgo, 2500, 2000, emptyList())
        )

        every { waterRepository.observeStatsRange(any(), any()) } returns flowOf(statsList)

        useCase().test {
            val insights = awaitItem()

            // Monthly trend should have all 3, sorted by date
            insights.monthlyTrend.size shouldBe 3
            insights.monthlyTrend[0].date shouldBe tenDaysAgo
            insights.monthlyTrend[1].date shouldBe fiveDaysAgo
            insights.monthlyTrend[2].date shouldBe today

            // Weekly trend should only have 5 days ago and today (last 7 days)
            insights.weeklyTrend.size shouldBe 2
            insights.weeklyTrend[0].date shouldBe fiveDaysAgo
            insights.weeklyTrend[1].date shouldBe today

            awaitComplete()
        }
    }

    @Test
    fun `empty stats returns default insights with zero values`() = runTest {
        every { waterRepository.observeStatsRange(any(), any()) } returns flowOf(emptyList())

        useCase().test {
            val insights = awaitItem()
            insights.averageIntake shouldBe 0
            insights.completionRate shouldBe 0f
            insights.longestStreak shouldBe 0
            insights.peakDay shouldBe null
            insights.peakDayIntake shouldBe 0
            insights.weeklyTrend shouldBe emptyList()
            insights.monthlyTrend shouldBe emptyList()
            awaitComplete()
        }
    }
}
