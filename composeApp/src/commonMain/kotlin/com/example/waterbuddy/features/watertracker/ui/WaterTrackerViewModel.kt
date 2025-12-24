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
import org.jetbrains.compose.resources.getString
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.error_failed_to_add
import waterbuddy.composeapp.generated.resources.error_failed_to_delete
import waterbuddy.composeapp.generated.resources.error_failed_to_update
import waterbuddy.composeapp.generated.resources.error_failed_to_update_goal
import waterbuddy.composeapp.generated.resources.ml_suffix
import waterbuddy.composeapp.generated.resources.quote_1
import waterbuddy.composeapp.generated.resources.quote_2
import waterbuddy.composeapp.generated.resources.quote_3
import waterbuddy.composeapp.generated.resources.quote_4
import waterbuddy.composeapp.generated.resources.quote_5
import waterbuddy.composeapp.generated.resources.success_entry_deleted
import waterbuddy.composeapp.generated.resources.success_entry_updated
import waterbuddy.composeapp.generated.resources.success_goal_updated
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

    private val quotes =
        listOf(
            Res.string.quote_1,
            Res.string.quote_2,
            Res.string.quote_3,
            Res.string.quote_4,
            Res.string.quote_5,
        )

    init {
        refreshQuote()
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
            WaterTrackerUiIntent.ShowCustomAddDialog -> _state.update { it.copy(showCustomAddDialog = true) }
            WaterTrackerUiIntent.DismissCustomAddDialog -> _state.update { it.copy(showCustomAddDialog = false) }
            WaterTrackerUiIntent.RefreshQuote -> refreshQuote()
        }
    }

    private fun refreshQuote() {
        viewModelScope.launch {
            val currentQuote = _state.value.quote
            var nextQuoteRes = quotes.random()
            var nextQuote = getString(nextQuoteRes)
            while (nextQuote == currentQuote && quotes.size > 1) {
                nextQuoteRes = quotes.random()
                nextQuote = getString(nextQuoteRes)
            }
            _state.update { it.copy(quote = nextQuote) }
        }
    }

    private fun addWater(amountMl: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showCustomAddDialog = false) }

            addWaterIntakeUseCase(amountMl).fold(
                onSuccess = {
                    _events.emit(WaterTrackerUiEvent.MartianBlessing)
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: getString(Res.string.error_failed_to_add),
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
                    _events.emit(WaterTrackerUiEvent.ShowSuccess(getString(Res.string.success_entry_deleted)))
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: getString(Res.string.error_failed_to_delete),
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
                    _events.emit(WaterTrackerUiEvent.ShowSuccess(getString(Res.string.success_entry_updated)))
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: getString(Res.string.error_failed_to_update),
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
                    val mlSuffix = getString(Res.string.ml_suffix)
                    _events.emit(
                        WaterTrackerUiEvent.ShowSuccess(
                            getString(Res.string.success_goal_updated, goalMl, mlSuffix),
                        ),
                    )
                },
                onFailure = { error ->
                    _events.emit(
                        WaterTrackerUiEvent.ShowError(
                            error.message ?: getString(Res.string.error_failed_to_update_goal),
                        ),
                    )
                },
            )
        }
    }
}
