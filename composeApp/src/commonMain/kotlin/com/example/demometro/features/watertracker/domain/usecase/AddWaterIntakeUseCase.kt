package com.example.demometro.features.watertracker.domain.usecase

import com.example.demometro.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject

@Inject
class AddWaterIntakeUseCase(
    private val waterRepository: WaterRepository
) {
    suspend operator fun invoke(amountMl: Int, note: String? = null): Result<Unit> {
        return waterRepository.addWaterIntake(amountMl, note)
    }
}