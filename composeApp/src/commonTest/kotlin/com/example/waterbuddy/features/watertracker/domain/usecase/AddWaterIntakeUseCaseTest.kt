package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AddWaterIntakeUseCaseTest {

    private val repository = mock<WaterRepository>()
    private val useCase = AddWaterIntakeUseCase(repository)

    @Test
    fun `invoke calls repository and returns result`() = runTest {
        val amountMl = 250
        val note = "Test note"
        val expectedResult = Result.success(Unit)

        everySuspend { repository.addWaterIntake(amountMl, note) } returns expectedResult

        val result = useCase(amountMl, note)

        result shouldBe expectedResult
        verifySuspend { repository.addWaterIntake(amountMl, note) }
    }
}
