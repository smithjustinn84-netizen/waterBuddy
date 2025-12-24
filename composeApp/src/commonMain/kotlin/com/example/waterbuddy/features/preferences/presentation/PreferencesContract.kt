package com.example.waterbuddy.features.preferences.presentation

import com.example.waterbuddy.features.preferences.domain.model.ReminderSettings
import com.example.waterbuddy.features.preferences.domain.model.ReminderSound
import kotlinx.datetime.LocalTime

data class PreferencesUiState(
    val dailyGoalMl: Int = 2000,
    val reminderSettings: ReminderSettings = ReminderSettings(),
    val isLoading: Boolean = false,
)

sealed interface PreferencesUiEvent {
    data class UpdateDailyGoal(val goalMl: Int) : PreferencesUiEvent
    data class ToggleReminders(val isEnabled: Boolean) : PreferencesUiEvent
    data class UpdateFrequency(val minutes: Int) : PreferencesUiEvent
    data class UpdateStartTime(val time: LocalTime) : PreferencesUiEvent
    data class UpdateEndTime(val time: LocalTime) : PreferencesUiEvent
    data class UpdateSound(val sound: ReminderSound) : PreferencesUiEvent
}

sealed interface PreferencesUiEffect {
    data class ShowError(val message: String) : PreferencesUiEffect
}
