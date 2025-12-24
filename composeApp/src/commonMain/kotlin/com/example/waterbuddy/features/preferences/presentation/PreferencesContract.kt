package com.example.waterbuddy.features.preferences.presentation

data class PreferencesUiState(
    val dailyGoalMl: Int = 2000,
    val isLoading: Boolean = false,
)

sealed interface PreferencesUiEvent {
    data class UpdateDailyGoal(val goalMl: Int) : PreferencesUiEvent
}

sealed interface PreferencesUiEffect {
    data class ShowError(val message: String) : PreferencesUiEffect
}
