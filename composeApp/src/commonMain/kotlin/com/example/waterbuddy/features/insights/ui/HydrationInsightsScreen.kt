package com.example.waterbuddy.features.insights.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import com.example.waterbuddy.features.insights.domain.model.HydrationInsights
import com.example.waterbuddy.features.insights.ui.components.KpiGrid
import com.example.waterbuddy.features.insights.ui.components.MonthlyHeatmap
import com.example.waterbuddy.features.insights.ui.components.TimeRangeSelector
import com.example.waterbuddy.features.insights.ui.components.WeeklyBarChart
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.insights_title_main
import waterbuddy.composeapp.generated.resources.insights_title_sub

@Composable
fun HydrationInsightsScreen(viewModel: HydrationInsightsViewModel = metroViewModel()) {
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
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationInsightsContent(
    state: HydrationInsightsUiState,
    onIntent: (HydrationInsightsUiIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = stringResource(Res.string.insights_title_main),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = stringResource(Res.string.insights_title_sub),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Start,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // 1. Top Segmented Button
            TimeRangeSelector(
                selectedRange = state.selectedTimeRange,
                onRangeSelected = {
                    onIntent(HydrationInsightsUiIntent.SelectTimeRange(it))
                },
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.insights != null) {
                // 2. Chart Section
                if (state.selectedTimeRange == TimeRange.WEEK) {
                    WeeklyBarChart(
                        data = state.insights.weeklyTrend,
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    )
                } else {
                    MonthlyHeatmap(
                        data = state.insights.monthlyTrend,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // 3. KPI Grid
                KpiGrid(insights = state.insights)
            }
        }
    }
}

private val previewWeeklyData =
    listOf(
        DailyWaterStats(LocalDate(2024, 1, 1), 1500, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 2), 2200, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 3), 1800, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 4), 2500, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 5), 2000, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 6), 1200, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 7), 2100, 2000, emptyList()),
    )

private val previewMonthlyData =
    (1..28).map { i ->
        DailyWaterStats(
            date = LocalDate(2024, 1, i),
            totalMl = (1000..2500).random(),
            goalMl = 2000,
            entries = emptyList(),
        )
    }

private val previewInsights =
    HydrationInsights(
        averageIntake = 1900,
        totalIntake = 53200,
        completionRate = 0.75f,
        longestStreak = 5,
        solsActive = 28,
        totalRituals = 112,
        averageRitualsPerSol = 4.0f,
        maxRitualAmount = 500,
        peakDay = LocalDate(2024, 1, 4),
        peakDayIntake = 2500,
        weeklyTrend = previewWeeklyData,
        monthlyTrend = previewMonthlyData,
    )

@Preview
@Composable
private fun HydrationInsightsScreenWeeklyPreview() {
    WaterBuddyTheme(darkTheme = false) {
        Surface {
            HydrationInsightsContent(
                state =
                    HydrationInsightsUiState(
                        insights = previewInsights,
                        selectedTimeRange = TimeRange.WEEK,
                    ),
                onIntent = {},
            )
        }
    }
}

@Preview
@Composable
private fun HydrationInsightsScreenMonthlyPreview() {
    WaterBuddyTheme(darkTheme = false) {
        Surface {
            HydrationInsightsContent(
                state =
                    HydrationInsightsUiState(
                        insights = previewInsights,
                        selectedTimeRange = TimeRange.MONTH,
                    ),
                onIntent = {},
            )
        }
    }
}

@Preview
@Composable
private fun HydrationInsightsScreenDarkModePreview() {
    WaterBuddyTheme(darkTheme = true) {
        Surface {
            HydrationInsightsContent(
                state =
                    HydrationInsightsUiState(
                        insights = previewInsights,
                        selectedTimeRange = TimeRange.WEEK,
                    ),
                onIntent = {},
            )
        }
    }
}

@Preview
@Composable
private fun HydrationInsightsScreenMonthlyDarkModePreview() {
    WaterBuddyTheme(darkTheme = true) {
        Surface {
            HydrationInsightsContent(
                state =
                    HydrationInsightsUiState(
                        insights = previewInsights,
                        selectedTimeRange = TimeRange.MONTH,
                    ),
                onIntent = {},
            )
        }
    }
}

@Preview
@Composable
private fun HydrationInsightsScreenLoadingPreview() {
    WaterBuddyTheme {
        Surface {
            HydrationInsightsContent(
                state =
                    HydrationInsightsUiState(
                        isLoading = true,
                    ),
                onIntent = {},
            )
        }
    }
}
