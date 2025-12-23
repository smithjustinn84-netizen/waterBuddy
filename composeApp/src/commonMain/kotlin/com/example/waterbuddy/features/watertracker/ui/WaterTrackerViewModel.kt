package com.example.waterbuddy.features.watertracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterbuddy.core.di.AppScope
import com.example.waterbuddy.core.navigation.Navigator
import com.example.waterbuddy.features.watertracker.domain.usecase.AddWaterIntakeUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.DeleteWaterIntakeUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.ObserveDailyWaterStatsUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateWaterIntakeUseCase
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Suppress("DEPRECATION")
@Inject
@ViewModelKey(WaterTrackerViewModel::class)
@ContributesIntoMap(AppScope::class)
class WaterTrackerViewModel(
    private val observeDailyWaterStatsUseCase: ObserveDailyWaterStatsUseCase,
    private val addWaterIntakeUseCase: AddWaterIntakeUseCase,
    private val deleteWaterIntakeUseCase: DeleteWaterIntakeUseCase,
    private val updateWaterIntakeUseCase: UpdateWaterIntakeUseCase,
    private val updateDailyGoalUseCase: UpdateDailyGoalUseCase,
    val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(WaterTrackerUiState(isLoading = true))
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<WaterTrackerUiEvent>()

    @OptIn(FlowPreview::class)
    val events =
        _events
            .asSharedFlow()
            .debounce(500)

    var showGoalDialog = MutableStateFlow(false)
        private set

    init {
        observeWaterStats()
    }

    private fun observeWaterStats() {
        viewModelScope.launch {
            val now = Clock.System.now()
            val today =
                Instant
                    .fromEpochMilliseconds(now.toEpochMilliseconds())
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            observeDailyWaterStatsUseCase(today).collect { stats ->
                val wasGoalReached = _state.value.isGoalReached

                _state.update {
                    it.copy(
                        totalMl = stats.totalMl,
                        goalMl = stats.goalMl,
                        progressPercentage = stats.progressPercentage,
                        remainingMl = stats.remainingMl,
                        isGoalReached = stats.isGoalReached,
                        entries = stats.entries,
                        isLoading = false,
                    )
                }

                // Emit goal reached event only when crossing the threshold
                if (!wasGoalReached && stats.isGoalReached) {
                    _events.emit(WaterTrackerUiEvent.GoalReached)
                }
            }
        }
    }

    fun handleIntent(intent: WaterTrackerUiIntent) {
        when (intent) {
            is WaterTrackerUiIntent.AddWater -> addWater(intent.amountMl)
            is WaterTrackerUiIntent.DeleteEntry -> deleteEntry(intent.id)
            is WaterTrackerUiIntent.UpdateEntry -> updateEntry(intent.id, intent.amountMl)
            is WaterTrackerUiIntent.ShowEditDialog -> _state.update { it.copy(editingEntry = intent.entry) }
            WaterTrackerUiIntent.DismissEditDialog -> _state.update { it.copy(editingEntry = null) }
            is WaterTrackerUiIntent.UpdateGoal -> updateGoal(intent.goalMl)
            WaterTrackerUiIntent.ShowGoalDialog -> showGoalDialog.value = true
            WaterTrackerUiIntent.DismissGoalDialog -> showGoalDialog.value = false
        }
    }

    private fun addWater(amountMl: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            addWaterIntakeUseCase(amountMl).fold(
                onSuccess = {
                    _events.emit(WaterTrackerUiEvent.ShowSuccess("Added ${amountMl}ml"))
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: "Failed to add water",
                        ),
                    )
                },
            )

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun deleteEntry(id: String) {
        viewModelScope.launch {
            deleteWaterIntakeUseCase(id).fold(
                onSuccess = {
                    _events.emit(WaterTrackerUiEvent.ShowSuccess("Entry deleted"))
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: "Failed to delete entry",
                        ),
                    )
                },
            )
        }
    }

    private fun updateEntry(
        id: String,
        amountMl: Int,
    ) {
        viewModelScope.launch {
            updateWaterIntakeUseCase(id, amountMl).fold(
                onSuccess = {
                    _state.update { it.copy(editingEntry = null) }
                    _events.emit(WaterTrackerUiEvent.ShowSuccess("Entry updated"))
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: "Failed to update entry",
                        ),
                    )
                },
            )
        }
    }

    private fun updateGoal(goalMl: Int) {
        viewModelScope.launch {
            updateDailyGoalUseCase(goalMl).fold(
                onSuccess = {
                    showGoalDialog.value = false
                    _events.emit(WaterTrackerUiEvent.ShowSuccess("Goal updated to ${goalMl}ml"))
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: "Failed to update goal",
                        ),
                    )
                },
            )
        }
    }
}
