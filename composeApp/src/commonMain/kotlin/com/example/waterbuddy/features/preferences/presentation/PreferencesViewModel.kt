package com.example.waterbuddy.features.preferences.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterbuddy.core.di.AppScope
import com.example.waterbuddy.features.preferences.domain.usecase.GetReminderSettingsUseCase
import com.example.waterbuddy.features.preferences.domain.usecase.UpdateReminderSettingsUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.GetDailyGoalUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey(PreferencesViewModel::class)
@ContributesIntoMap(AppScope::class)
class PreferencesViewModel(
    private val getDailyGoalUseCase: GetDailyGoalUseCase,
    private val updateDailyGoalUseCase: UpdateDailyGoalUseCase,
    private val getReminderSettingsUseCase: GetReminderSettingsUseCase,
    private val updateReminderSettingsUseCase: UpdateReminderSettingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<PreferencesUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val goal = getDailyGoalUseCase()
            _uiState.update { it.copy(dailyGoalMl = goal) }

            getReminderSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        reminderSettings = settings,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onEvent(event: PreferencesUiEvent) {
        when (event) {
            is PreferencesUiEvent.UpdateDailyGoal -> updateDailyGoal(event.goalMl)
            is PreferencesUiEvent.ToggleReminders -> updateReminders { it.copy(isEnabled = event.isEnabled) }
            is PreferencesUiEvent.UpdateFrequency -> updateReminders { it.copy(frequencyMinutes = event.minutes) }
            is PreferencesUiEvent.UpdateStartTime -> updateReminders { it.copy(startTime = event.time) }
            is PreferencesUiEvent.UpdateEndTime -> updateReminders { it.copy(endTime = event.time) }
            is PreferencesUiEvent.UpdateSound -> updateReminders { it.copy(sound = event.sound) }
        }
    }

    private fun updateDailyGoal(goalMl: Int) {
        viewModelScope.launch {
            updateDailyGoalUseCase(goalMl).fold(
                onSuccess = {
                    _uiState.update { it.copy(dailyGoalMl = goalMl) }
                },
                onFailure = { error ->
                    _uiEffect.emit(PreferencesUiEffect.ShowError(error.message ?: "Failed to update goal"))
                },
            )
        }
    }

    private fun updateReminders(update: (com.example.waterbuddy.features.preferences.domain.model.ReminderSettings) -> com.example.waterbuddy.features.preferences.domain.model.ReminderSettings) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.reminderSettings
            val newSettings = update(currentSettings)
            updateReminderSettingsUseCase(newSettings).onFailure { error ->
                _uiEffect.emit(PreferencesUiEffect.ShowError(error.message ?: "Failed to update reminder settings"))
            }
        }
    }
}
