package com.example.waterbuddy.features.insights.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.waterbuddy.features.insights.ui.components.KpiGrid
import com.example.waterbuddy.features.insights.ui.components.MonthlyHeatmap
import com.example.waterbuddy.features.insights.ui.components.TimeRangeSelector
import com.example.waterbuddy.features.insights.ui.components.WeeklyBarChart
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun HydrationInsightsScreen(
    viewModel: HydrationInsightsViewModel = metroViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HydrationInsightsUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    HydrationInsightsContent(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationInsightsContent(
    state: HydrationInsightsUiState,
    onIntent: (HydrationInsightsUiIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hydration Insights",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Top Segmented Button
            TimeRangeSelector(
                selectedRange = state.selectedTimeRange,
                onRangeSelected = {
                    onIntent(HydrationInsightsUiIntent.SelectTimeRange(it))
                }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.insights != null) {
                // 2. Chart Section
                if (state.selectedTimeRange == TimeRange.WEEK) {
                    WeeklyBarChart(
                        data = state.insights.weeklyTrend,
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                } else {
                    MonthlyHeatmap(
                        data = state.insights.monthlyTrend,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 3. KPI Grid
                KpiGrid(insights = state.insights)
            }
        }
    }
}
