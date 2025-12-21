package com.example.waterbuddy.features.insights.ui

import com.example.waterbuddy.features.insights.domain.model.HydrationInsights

data class HydrationInsightsUiState(
    val isLoading: Boolean = false,
    val insights: HydrationInsights? = null,
    val selectedTimeRange: TimeRange = TimeRange.WEEK,
    val errorMessage: String? = null,
)

enum class TimeRange {
    WEEK,
    MONTH,
}

sealed interface HydrationInsightsUiIntent {
    data class SelectTimeRange(
        val range: TimeRange,
    ) : HydrationInsightsUiIntent

    data object Refresh : HydrationInsightsUiIntent
}

sealed interface HydrationInsightsUiEvent {
    data class ShowError(
        val message: String,
    ) : HydrationInsightsUiEvent
}
