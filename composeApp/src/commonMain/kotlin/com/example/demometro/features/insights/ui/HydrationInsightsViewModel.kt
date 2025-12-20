package com.example.demometro.features.insights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demometro.features.insights.domain.usecase.GetHydrationInsightsUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Inject
class HydrationInsightsViewModel(
    private val getHydrationInsightsUseCase: GetHydrationInsightsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HydrationInsightsUiState(isLoading = true))
    val state: StateFlow<HydrationInsightsUiState> = _state.asStateFlow()

    private val _events = Channel<HydrationInsightsUiEvent>()
    val events: Flow<HydrationInsightsUiEvent> = _events.receiveAsFlow()

    init {
        loadInsights()
    }

    fun handleIntent(intent: HydrationInsightsUiIntent) {
        when (intent) {
            is HydrationInsightsUiIntent.SelectTimeRange -> {
                _state.update { it.copy(selectedTimeRange = intent.range) }
            }

            HydrationInsightsUiIntent.Refresh -> {
                loadInsights()
            }
        }
    }

    private fun loadInsights() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                getHydrationInsightsUseCase()
                    .catch { e ->
                        _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                        _events.send(HydrationInsightsUiEvent.ShowError(e.message ?: "Unknown error"))
                    }
                    .collect { insights ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                insights = insights
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _events.send(HydrationInsightsUiEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
