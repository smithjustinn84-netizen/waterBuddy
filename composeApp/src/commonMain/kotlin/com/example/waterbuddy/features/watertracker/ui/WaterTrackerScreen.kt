package com.example.waterbuddy.features.watertracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.waterbuddy.core.navigation.HydrationInsights
import com.example.waterbuddy.features.watertracker.domain.model.WaterIntake
import com.example.waterbuddy.features.watertracker.ui.components.DrinkOverlay
import com.example.waterbuddy.features.watertracker.ui.components.EmptyStateMessage
import com.example.waterbuddy.features.watertracker.ui.components.GoalDialog
import com.example.waterbuddy.features.watertracker.ui.components.QuickAddSection
import com.example.waterbuddy.features.watertracker.ui.components.WaterEntryItem
import com.example.waterbuddy.features.watertracker.ui.components.WaterProgressCard
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.goal_button
import waterbuddy.composeapp.generated.resources.goal_reached_message
import waterbuddy.composeapp.generated.resources.todays_entries
import waterbuddy.composeapp.generated.resources.water_tracker_title
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerScreen(
    viewModel: WaterTrackerViewModel = metroViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showGoalDialog by viewModel.showGoalDialog.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val successMessage = stringResource(Res.string.goal_reached_message)

    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is WaterTrackerUiEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is WaterTrackerUiEvent.ShowError -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }

                WaterTrackerUiEvent.GoalReached -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = successMessage,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    WaterTrackerContent(
        state = state,
        showGoalDialog = showGoalDialog,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        onNavigateToInsights = { viewModel.navigator.navigate(HydrationInsights) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerContent(
    state: WaterTrackerUiState,
    showGoalDialog: Boolean,
    snackbarHostState: SnackbarHostState,
    onIntent: (WaterTrackerUiIntent) -> Unit,
    onNavigateToInsights: () -> Unit
) {
    // Drinking Interaction State
    var targetDrinkAmount by remember { mutableStateOf(0) }
    var currentDrinkAmount by remember { mutableStateOf(0) }

    LaunchedEffect(targetDrinkAmount) {
        if (targetDrinkAmount > 0) {
            val timeSource = TimeSource.Monotonic
            var lastTime = timeSource.markNow()

            while (currentDrinkAmount < targetDrinkAmount) {
                delay(16) // Target ~60 FPS
                val now = timeSource.markNow()
                val elapsed = (now - lastTime).inWholeMilliseconds

                if (elapsed > 0) {
                    // Rate: ~900ml per second (faster fill for quick add)
                    val amountToAdd = (elapsed * 0.9).toInt()
                    if (amountToAdd > 0) {
                        currentDrinkAmount += amountToAdd
                        lastTime = now
                    }
                }
            }
            // Cap at target
            currentDrinkAmount = targetDrinkAmount

            // Show full state briefly
            delay(500)

            onIntent(WaterTrackerUiIntent.AddWater(targetDrinkAmount))

            // Reset
            targetDrinkAmount = 0
            currentDrinkAmount = 0
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.water_tracker_title)) },
                actions = {
                    IconButton(onClick = onNavigateToInsights) {
                        Text("📊")
                    }
                    TextButton(onClick = { onIntent(WaterTrackerUiIntent.ShowGoalDialog) }) {
                        Text("⚙️ ${stringResource(Res.string.goal_button)}")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress Card
                item {
                    WaterProgressCard(
                        totalMl = state.totalMl,
                        goalMl = state.goalMl,
                        progressPercentage = state.progressPercentage,
                        remainingMl = state.remainingMl,
                        isGoalReached = state.isGoalReached
                    )
                }

                // Quick Add Buttons
                item {
                    QuickAddSection(
                        onAddWater = { amount ->
                            if (targetDrinkAmount == 0) { // Prevent double clicks during animation
                                targetDrinkAmount = amount
                            }
                        }
                    )
                }

                // Today's Entries Header
                item {
                    Text(
                        text = stringResource(Res.string.todays_entries),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Water Intake List
                items(state.entries, key = { it.id }) { entry ->
                    WaterEntryItem(
                        entry = entry,
                        onDelete = { onIntent(WaterTrackerUiIntent.DeleteEntry(entry.id)) }
                    )
                }

                if (state.entries.isEmpty()) {
                    item {
                        EmptyStateMessage()
                    }
                }
            }

            // Fluid Animation Overlay
            if (currentDrinkAmount > 0) {
                DrinkOverlay(
                    amountMl = currentDrinkAmount,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Goal Dialog
    if (showGoalDialog) {
        GoalDialog(
            currentGoal = state.goalMl,
            onDismiss = { onIntent(WaterTrackerUiIntent.DismissGoalDialog) },
            onConfirm = { newGoal ->
                onIntent(WaterTrackerUiIntent.UpdateGoal(newGoal))
            }
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun WaterTrackerPreview() {
    MaterialTheme {
        WaterTrackerContent(
            state = WaterTrackerUiState(
                totalMl = 750,
                goalMl = 2000,
                progressPercentage = 0.375f,
                remainingMl = 1250,
                entries = listOf(
                    WaterIntake(
                        id = "1",
                        amountMl = 250,
                        timestamp = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                    ),
                    WaterIntake(
                        id = "2",
                        amountMl = 500,
                        timestamp = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                )
            ),
            showGoalDialog = false,
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateToInsights = {}
        )
    }
}

@Preview
@Composable
fun WaterTrackerEmptyPreview() {
    MaterialTheme {
        WaterTrackerContent(
            state = WaterTrackerUiState(),
            showGoalDialog = false,
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateToInsights = {}
        )
    }
}
