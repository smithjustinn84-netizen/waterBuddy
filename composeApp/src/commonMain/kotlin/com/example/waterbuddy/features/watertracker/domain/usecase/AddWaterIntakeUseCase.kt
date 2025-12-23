package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject

@Inject
class AddWaterIntakeUseCase(
    private val waterRepository: WaterRepository,
) {
    suspend operator fun invoke(
        amountMl: Int,
        note: String? = null,
    ): Result<Unit> {
        if (amountMl <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than 0"))
        }
        if (amountMl > 4000) {
            return Result.failure(IllegalArgumentException("Amount cannot exceed 4000ml"))
        }
        return waterRepository.addWaterIntake(amountMl, note)
    }
}
