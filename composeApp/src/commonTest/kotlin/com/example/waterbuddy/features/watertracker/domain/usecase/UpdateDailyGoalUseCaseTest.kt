package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdateDailyGoalUseCaseTest {
    private val repository = mock<WaterRepository>()
    private val useCase = UpdateDailyGoalUseCase(repository)

    @Test
    fun `invoke calls repository when goal is within range`() =
        runTest {
            val goalMl = 2500
            val expectedResult = Result.success(Unit)

            everySuspend { repository.updateDailyGoal(goalMl) } returns expectedResult

            val result = useCase(goalMl)

            result shouldBe expectedResult
            verifySuspend { repository.updateDailyGoal(goalMl) }
        }

    @Test
    fun `invoke returns failure when goal is below minimum`() =
        runTest {
            val goalMl = UpdateDailyGoalUseCase.MIN_GOAL - 1

            val result = useCase(goalMl)

            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeInstanceOf<IllegalArgumentException>()
            verifySuspend(mode = VerifyMode.not) { repository.updateDailyGoal(any()) }
        }

    @Test
    fun `invoke returns failure when goal is above maximum`() =
        runTest {
            val goalMl = UpdateDailyGoalUseCase.MAX_GOAL + 1

            val result = useCase(goalMl)

            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeInstanceOf<IllegalArgumentException>()
            verifySuspend(mode = VerifyMode.not) { repository.updateDailyGoal(any()) }
        }

    @Test
    fun `invoke calls repository when goal is exactly minimum`() =
        runTest {
            val goalMl = UpdateDailyGoalUseCase.MIN_GOAL
            everySuspend { repository.updateDailyGoal(goalMl) } returns Result.success(Unit)

            val result = useCase(goalMl)

            result.isSuccess shouldBe true
            verifySuspend { repository.updateDailyGoal(goalMl) }
        }

    @Test
    fun `invoke calls repository when goal is exactly maximum`() =
        runTest {
            val goalMl = UpdateDailyGoalUseCase.MAX_GOAL
            everySuspend { repository.updateDailyGoal(goalMl) } returns Result.success(Unit)

            val result = useCase(goalMl)

            result.isSuccess shouldBe true
            verifySuspend { repository.updateDailyGoal(goalMl) }
        }
}
