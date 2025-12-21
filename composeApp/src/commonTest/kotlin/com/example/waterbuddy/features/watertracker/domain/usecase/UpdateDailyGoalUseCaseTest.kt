package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdateDailyGoalUseCaseTest {
    private val repository = mock<WaterRepository>()
    private val useCase = UpdateDailyGoalUseCase(repository)

    @Test
    fun `invoke calls repository and returns result`() =
        runTest {
            val goalMl = 2500
            val expectedResult = Result.success(Unit)

            everySuspend { repository.updateDailyGoal(goalMl) } returns expectedResult

            val result = useCase(goalMl)

            result shouldBe expectedResult
            verifySuspend { repository.updateDailyGoal(goalMl) }
        }
}
