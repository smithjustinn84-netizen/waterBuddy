package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class ObserveDailyWaterStatsUseCaseTest {

    private val repository = mock<WaterRepository>()
    private val useCase = ObserveDailyWaterStatsUseCase(repository)

    @Test
    fun `invoke calls repository and returns flow`() {
        val date = LocalDate(2023, 10, 27)
        val stats = DailyWaterStats(date, 0, 2000, emptyList())
        val expectedFlow = flowOf(stats)

        every { repository.observeDailyStats(date) } returns expectedFlow

        val result = useCase(date)

        result shouldBe expectedFlow
        verify {
            @Suppress("UnusedFlow")
            repository.observeDailyStats(date)
        }
    }
}
