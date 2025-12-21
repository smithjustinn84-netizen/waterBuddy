package com.example.waterbuddy.features.watertracker.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.waterbuddy.core.navigation.HydrationInsights
import com.example.waterbuddy.features.watertracker.domain.model.WaterIntake
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.bottle_label
import waterbuddy.composeapp.generated.resources.cancel_button
import waterbuddy.composeapp.generated.resources.consumed_label
import waterbuddy.composeapp.generated.resources.empty_state_subtitle
import waterbuddy.composeapp.generated.resources.empty_state_title
import waterbuddy.composeapp.generated.resources.exceeded_label
import waterbuddy.composeapp.generated.resources.glass_label
import waterbuddy.composeapp.generated.resources.goal_button
import waterbuddy.composeapp.generated.resources.goal_input_label
import waterbuddy.composeapp.generated.resources.goal_label
import waterbuddy.composeapp.generated.resources.goal_reached_message
import waterbuddy.composeapp.generated.resources.large_label
import waterbuddy.composeapp.generated.resources.ml_suffix
import waterbuddy.composeapp.generated.resources.quick_add_title
import waterbuddy.composeapp.generated.resources.remaining_label
import waterbuddy.composeapp.generated.resources.save_button
import waterbuddy.composeapp.generated.resources.set_goal_description
import waterbuddy.composeapp.generated.resources.set_goal_title
import waterbuddy.composeapp.generated.resources.todays_entries
import waterbuddy.composeapp.generated.resources.water_tracker_title
import kotlin.math.PI
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

            viewModel.handleIntent(WaterTrackerUiIntent.AddWater(targetDrinkAmount))

            // Reset
            targetDrinkAmount = 0
            currentDrinkAmount = 0
        }
    }

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
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                WaterTrackerUiEvent.GoalReached -> {
                    snackbarHostState.showSnackbar(
                        message = successMessage,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.water_tracker_title)) },
                actions = {
                    IconButton(onClick = { viewModel.navigator.navigate(HydrationInsights) }) {
                        Text("📊")
                    }
                    TextButton(onClick = { viewModel.handleIntent(WaterTrackerUiIntent.ShowGoalDialog) }) {
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
                        onDelete = { viewModel.handleIntent(WaterTrackerUiIntent.DeleteEntry(entry.id)) }
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
            onDismiss = { viewModel.handleIntent(WaterTrackerUiIntent.DismissGoalDialog) },
            onConfirm = { newGoal ->
                viewModel.handleIntent(WaterTrackerUiIntent.UpdateGoal(newGoal))
            }
        )
    }
}

@Composable
private fun DrinkOverlay(
    amountMl: Int,
    modifier: Modifier = Modifier,
    maxAmountMl: Int = 1000
) {
    val fillPercentage = (amountMl.toFloat() / maxAmountMl.toFloat()).coerceIn(0f, 1f)
    val color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

    val transition = rememberInfiniteTransition()
    val waveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val waterHeight = height * fillPercentage
            val waveAmplitude = 20.dp.toPx()

            val path = Path().apply {
                moveTo(0f, height)
                lineTo(0f, height - waterHeight)

                // Draw wave
                val steps = 50
                for (i in 0..steps) {
                    val x = width * (i.toFloat() / steps)
                    val progress = i.toFloat() / steps
                    val waveY =
                        kotlin.math.sin((progress * 2 * PI) + (waveOffset * 2 * PI)) * waveAmplitude
                    lineTo(x, height - waterHeight + waveY.toFloat())
                }

                lineTo(width, height)
                close()
            }

            drawPath(path, color)
        }

        // Center Text
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$amountMl ml",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun WaterProgressCard(
    totalMl: Int,
    goalMl: Int,
    progressPercentage: Float,
    remainingMl: Int,
    isGoalReached: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = tween(durationMillis = 1000)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGoalReached)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Water Drop Icon with progress
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "💧",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = stringResource(Res.string.consumed_label), value = "${totalMl}ml")
                StatItem(label = stringResource(Res.string.goal_label), value = "${goalMl}ml")
                StatItem(
                    label = if (isGoalReached) stringResource(Res.string.exceeded_label) else stringResource(Res.string.remaining_label),
                    value = if (isGoalReached) "+${totalMl - goalMl}ml" else "${remainingMl}ml"
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickAddSection(onAddWater: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.quick_add_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAddButton(
                    amount = 250,
                    label = stringResource(Res.string.glass_label),
                    emoji = "🥤",
                    onClick = { onAddWater(250) },
                    modifier = Modifier.weight(1f)
                )
                QuickAddButton(
                    amount = 500,
                    label = stringResource(Res.string.bottle_label),
                    emoji = "💧",
                    onClick = { onAddWater(500) },
                    modifier = Modifier.weight(1f)
                )
                QuickAddButton(
                    amount = 750,
                    label = stringResource(Res.string.large_label),
                    emoji = "☕",
                    onClick = { onAddWater(750) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickAddButton(
    amount: Int,
    label: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${amount}ml", style = MaterialTheme.typography.bodySmall)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun WaterEntryItem(
    entry: WaterIntake,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💧",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Column {
                    Text(
                        text = "${entry.amountMl}ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatTime(entry.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(onClick = onDelete) {
                Text(
                    text = "🗑️",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💧",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.empty_state_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(Res.string.empty_state_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun GoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goalText by remember { mutableStateOf((currentGoal).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.set_goal_title)) },
        text = {
            Column {
                Text(stringResource(Res.string.set_goal_description))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(Res.string.goal_input_label)) },
                    singleLine = true,
                    suffix = { Text(stringResource(Res.string.ml_suffix)) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = goalText.toIntOrNull() ?: currentGoal
                    onConfirm(goal)
                }
            ) {
                Text(stringResource(Res.string.save_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        }
    )
}

private fun formatTime(dateTime: LocalDateTime): String {
    val hour = dateTime.hour
    val minute = dateTime.minute.toString().padStart(2, '0')
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$displayHour:$minute $amPm"
}
