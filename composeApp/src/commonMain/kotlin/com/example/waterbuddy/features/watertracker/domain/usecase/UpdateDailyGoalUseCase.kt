package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject

@Inject
class UpdateDailyGoalUseCase(
    private val waterRepository: WaterRepository
) {
    suspend operator fun invoke(goalMl: Int): Result<Unit> {
        return waterRepository.updateDailyGoal(goalMl)
    }
}