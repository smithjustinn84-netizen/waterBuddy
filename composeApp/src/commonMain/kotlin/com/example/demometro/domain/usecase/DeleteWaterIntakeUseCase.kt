package com.example.demometro.domain.usecase

import com.example.demometro.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject

@Inject
class DeleteWaterIntakeUseCase(
    private val waterRepository: WaterRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return waterRepository.deleteWaterIntake(id)
    }
}

