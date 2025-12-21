package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject

@Inject
class DeleteWaterIntakeUseCase(
    private val waterRepository: WaterRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> = waterRepository.deleteWaterIntake(id)
}
