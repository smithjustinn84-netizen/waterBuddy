package com.example.waterbuddy.features.watertracker.domain.usecase

import com.example.waterbuddy.features.watertracker.domain.repository.WaterRepository
import dev.zacsweers.metro.Inject

@Inject
class UpdateDailyGoalUseCase(
    private val waterRepository: WaterRepository,
) {
    suspend operator fun invoke(goalMl: Int): Result<Unit> {
        if (goalMl < MIN_GOAL || goalMl > MAX_GOAL) {
            return Result.failure(IllegalArgumentException("Goal must be between $MIN_GOAL and $MAX_GOAL ml"))
        }
        return waterRepository.updateDailyGoal(goalMl)
    }

    companion object {
        const val MIN_GOAL = 500
        const val MAX_GOAL = 5000
    }
}
