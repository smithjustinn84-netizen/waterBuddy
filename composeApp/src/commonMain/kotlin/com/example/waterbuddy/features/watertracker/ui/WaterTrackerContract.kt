package com.example.waterbuddy.features.watertracker.ui

import com.example.waterbuddy.features.watertracker.domain.model.WaterIntake

data class WaterTrackerUiState(
    val totalMl: Int = 0,
    val goalMl: Int = 2000,
    val progressPercentage: Float = 0f,
    val remainingMl: Int = 2000,
    val isGoalReached: Boolean = false,
    val entries: List<WaterIntake> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val editingEntry: WaterIntake? = null,
    val showCustomAddDialog: Boolean = false,
    val quote: String? = null,
)

sealed interface WaterTrackerUiIntent {
    data class AddWater(
        val amountMl: Int,
    ) : WaterTrackerUiIntent

    data class DeleteEntry(
        val id: String,
    ) : WaterTrackerUiIntent

    data class UpdateEntry(
        val id: String,
        val amountMl: Int,
    ) : WaterTrackerUiIntent

    data class ShowEditDialog(
        val entry: WaterIntake,
    ) : WaterTrackerUiIntent

    data object DismissEditDialog : WaterTrackerUiIntent

    data class UpdateGoal(
        val goalMl: Int,
    ) : WaterTrackerUiIntent

    data object ShowGoalDialog : WaterTrackerUiIntent

    data object DismissGoalDialog : WaterTrackerUiIntent

    data object ShowCustomAddDialog : WaterTrackerUiIntent

    data object DismissCustomAddDialog : WaterTrackerUiIntent

    data object RefreshQuote : WaterTrackerUiIntent
}

sealed interface WaterTrackerUiEvent {
    data class ShowSuccess(
        val message: String,
    ) : WaterTrackerUiEvent

    data class ShowError(
        val message: String,
    ) : WaterTrackerUiEvent

    data object GoalReached : WaterTrackerUiEvent

    data object MartianBlessing : WaterTrackerUiEvent
}
