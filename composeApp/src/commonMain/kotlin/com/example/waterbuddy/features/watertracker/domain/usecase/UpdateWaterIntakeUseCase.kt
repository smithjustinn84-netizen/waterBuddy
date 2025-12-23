package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.core.di.AppScope
import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class UpdateWaterIntakeUseCase(
    private val repository: WaterRepository,
) {
    suspend operator fun invoke(
        id: String,
        amountMl: Int,
    ): Result<Unit> {
        if (amountMl <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than 0"))
        }
        return repository.updateWaterIntake(id, amountMl)
    }
}
