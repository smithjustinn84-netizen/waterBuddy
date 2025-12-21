package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteWaterIntakeUseCaseTest {
    private val repository = mock<WaterRepository>()
    private val useCase = DeleteWaterIntakeUseCase(repository)

    @Test
    fun `invoke calls repository and returns result`() =
        runTest {
            val id = "1"
            val expectedResult = Result.success(Unit)

            everySuspend { repository.deleteWaterIntake(id) } returns expectedResult

            val result = useCase(id)

            result shouldBe expectedResult
            verifySuspend { repository.deleteWaterIntake(id) }
        }
}
