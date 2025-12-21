package com.example.waterbuddy.features.watertracker.data.repository

import app.cash.turbine.test
import com.example.waterbuddy.features.watertracker.data.local.dao.DailyGoalDao
import com.example.waterbuddy.features.watertracker.data.local.dao.WaterIntakeDao
import com.example.waterbuddy.features.watertracker.data.local.entity.DailyGoalEntity
import com.example.waterbuddy.features.watertracker.data.local.entity.WaterIntakeEntity
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertTrue

class WaterRepositoryImplTest {

    private val waterIntakeDao = mock<WaterIntakeDao>()
    private val dailyGoalDao = mock<DailyGoalDao>()
    private val repositoryImpl = WaterRepositoryImpl(waterIntakeDao, dailyGoalDao)
    private val repository: WaterRepository = repositoryImpl

    @Test
    fun `observeDailyStats combines intakes and goal correctly`() = runTest {
        val date = LocalDate(2023, 10, 27)
        val intake1 = WaterIntakeEntity("1", 250, LocalDateTime(2023, 10, 27, 10, 0), "Note 1")
        val intake2 = WaterIntakeEntity("2", 500, LocalDateTime(2023, 10, 27, 14, 0), "Note 2")
        val intakeOtherDay =
            WaterIntakeEntity("3", 300, LocalDateTime(2023, 10, 28, 10, 0), "Other day")

        every { waterIntakeDao.getAllWaterIntakes() } returns flowOf(
            listOf(
                intake1,
                intake2,
                intakeOtherDay
            )
        )
        every { dailyGoalDao.getDailyGoal() } returns flowOf(DailyGoalEntity(goalMl = 2500))

        repository.observeDailyStats(date).test {
            val stats = awaitItem()
            stats.date shouldBe date
            stats.totalMl shouldBe 750
            stats.goalMl shouldBe 2500
            stats.entries.size shouldBe 2
            stats.entries[0].id shouldBe "1"
            stats.entries[1].id shouldBe "2"
            awaitComplete()
        }
    }

    @Test
    fun `observeDailyStats uses default goal when none exists`() = runTest {
        val date = LocalDate(2023, 10, 27)
        every { waterIntakeDao.getAllWaterIntakes() } returns flowOf(emptyList())
        every { dailyGoalDao.getDailyGoal() } returns flowOf(null)

        repository.observeDailyStats(date).test {
            val stats = awaitItem()
            stats.goalMl shouldBe 2000
            awaitComplete()
        }
    }

    @Test
    fun `observeStatsRange generates stats for each day in range`() = runTest {
        val startDate = LocalDate(2023, 10, 25)
        val endDate = LocalDate(2023, 10, 27)

        val intake = WaterIntakeEntity("1", 250, LocalDateTime(2023, 10, 26, 10, 0))

        every { waterIntakeDao.getAllWaterIntakes() } returns flowOf(listOf(intake))
        every { dailyGoalDao.getDailyGoal() } returns flowOf(DailyGoalEntity(goalMl = 2000))

        repository.observeStatsRange(startDate, endDate).test {
            val statsList = awaitItem()
            statsList.size shouldBe 3

            statsList[0].date shouldBe LocalDate(2023, 10, 25)
            statsList[0].totalMl shouldBe 0

            statsList[1].date shouldBe LocalDate(2023, 10, 26)
            statsList[1].totalMl shouldBe 250

            statsList[2].date shouldBe LocalDate(2023, 10, 27)
            statsList[2].totalMl shouldBe 0

            awaitComplete()
        }
    }

    @Test
    fun `addWaterIntake calls dao and returns success`() = runTest {
        everySuspend { waterIntakeDao.insertWaterIntake(any()) } returns Unit

        val result = repository.addWaterIntake(250, "Some note")

        assertTrue(result.isSuccess)
        verifySuspend { waterIntakeDao.insertWaterIntake(any()) }
    }

    @Test
    fun `addWaterIntake with default note parameter calls dao and returns success`() = runTest {
        everySuspend { waterIntakeDao.insertWaterIntake(any()) } returns Unit

        // Calling without the second parameter to exercise the default parameter in the interface
        val result = repository.addWaterIntake(250)

        assertTrue(result.isSuccess)
        verifySuspend {
            // We use any() because the timestamp and ID are generated inside the method
            waterIntakeDao.insertWaterIntake(any())
        }
    }

    @Test
    fun `addWaterIntake returns failure when dao throws`() = runTest {
        everySuspend { waterIntakeDao.insertWaterIntake(any()) } throws Exception("DB Error")

        val result = repository.addWaterIntake(250, "Some note")

        assertTrue(result.isFailure)
        result.exceptionOrNull()?.message shouldBe "DB Error"
    }

    @Test
    fun `deleteWaterIntake calls dao and returns success`() = runTest {
        everySuspend { waterIntakeDao.deleteWaterIntakeById(any()) } returns Unit

        val result = repository.deleteWaterIntake("1")

        assertTrue(result.isSuccess)
        verifySuspend { waterIntakeDao.deleteWaterIntakeById("1") }
    }

    @Test
    fun `updateDailyGoal calls dao and returns success`() = runTest {
        everySuspend { dailyGoalDao.insertDailyGoal(any()) } returns Unit

        val result = repository.updateDailyGoal(3000)

        assertTrue(result.isSuccess)
        verifySuspend { dailyGoalDao.insertDailyGoal(any()) }
    }

    @Test
    fun `getDailyGoal returns value from dao or default`() = runTest {
        everySuspend { dailyGoalDao.getGoalValue() } returns 1500
        repository.getDailyGoal() shouldBe 1500

        everySuspend { dailyGoalDao.getGoalValue() } returns null
        repository.getDailyGoal() shouldBe 2000
    }
}
