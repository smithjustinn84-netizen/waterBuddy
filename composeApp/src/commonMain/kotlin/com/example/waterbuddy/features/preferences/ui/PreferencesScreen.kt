package com.example.waterbuddy.features.preferences.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import com.example.waterbuddy.features.preferences.domain.model.ReminderSound
import com.example.waterbuddy.features.preferences.presentation.PreferencesUiEffect
import com.example.waterbuddy.features.preferences.presentation.PreferencesUiEvent
import com.example.waterbuddy.features.preferences.presentation.PreferencesUiState
import com.example.waterbuddy.features.preferences.presentation.PreferencesViewModel
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.frequency_label
import waterbuddy.composeapp.generated.resources.frequency_minutes_format
import waterbuddy.composeapp.generated.resources.ml_suffix
import waterbuddy.composeapp.generated.resources.preferences_footer
import waterbuddy.composeapp.generated.resources.preferences_title_main
import waterbuddy.composeapp.generated.resources.preferences_title_sub
import waterbuddy.composeapp.generated.resources.reminders_enabled_label
import waterbuddy.composeapp.generated.resources.reminders_section_title
import waterbuddy.composeapp.generated.resources.set_goal_title
import waterbuddy.composeapp.generated.resources.sound_default
import waterbuddy.composeapp.generated.resources.sound_label
import waterbuddy.composeapp.generated.resources.sound_life_stream
import waterbuddy.composeapp.generated.resources.sound_martian_drip
import waterbuddy.composeapp.generated.resources.sound_zenith_bell

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = metroViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PreferencesUiEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    PreferencesContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesContent(
    state: PreferencesUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (PreferencesUiEvent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(Res.string.preferences_title_main),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(Res.string.preferences_title_sub),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // Goal Section
                Text(
                    text = stringResource(Res.string.set_goal_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${state.dailyGoalMl} ${stringResource(Res.string.ml_suffix)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = state.dailyGoalMl.coerceIn(
                        UpdateDailyGoalUseCase.MIN_GOAL,
                        UpdateDailyGoalUseCase.MAX_GOAL,
                    ).toFloat(),
                    onValueChange = {
                        onEvent(PreferencesUiEvent.UpdateDailyGoal(it.toInt()))
                    },
                    valueRange = UpdateDailyGoalUseCase.MIN_GOAL.toFloat()..UpdateDailyGoalUseCase.MAX_GOAL.toFloat(),
                    steps = (UpdateDailyGoalUseCase.MAX_GOAL - UpdateDailyGoalUseCase.MIN_GOAL) / 50 - 1,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    thumb = {
                        Text(
                            text = "🛸",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                )

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(32.dp))

                // Reminders Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "🔔",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.reminders_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(Res.string.reminders_enabled_label))
                    Switch(
                        checked = state.reminderSettings.isEnabled,
                        onCheckedChange = { onEvent(PreferencesUiEvent.ToggleReminders(it)) },
                    )
                }

                if (state.reminderSettings.isEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Frequency Picker
                    FrequencyPicker(
                        currentMinutes = state.reminderSettings.frequencyMinutes,
                        onFrequencySelected = { onEvent(PreferencesUiEvent.UpdateFrequency(it)) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sound Picker
                    SoundPicker(
                        currentSound = state.reminderSettings.sound,
                        onSoundSelected = { onEvent(PreferencesUiEvent.UpdateSound(it)) },
                    )
                }

                Spacer(modifier = Modifier.height(100.dp)) // Padding for footer
            }

            Text(
                text = stringResource(Res.string.preferences_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
fun FrequencyPicker(
    currentMinutes: Int,
    onFrequencySelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val frequencies = listOf(30, 60, 90, 120, 180)

    Column {
        Text(
            text = stringResource(Res.string.frequency_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Box {
            Text(
                text = stringResource(Res.string.frequency_minutes_format, currentMinutes),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                frequencies.forEach { freq ->
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.frequency_minutes_format, freq)) },
                        onClick = {
                            onFrequencySelected(freq)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun SoundPicker(
    currentSound: ReminderSound,
    onSoundSelected: (ReminderSound) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val soundLabel = when (currentSound) {
        ReminderSound.DEFAULT -> stringResource(Res.string.sound_default)
        ReminderSound.MARTIAN_DRIP -> stringResource(Res.string.sound_martian_drip)
        ReminderSound.LIFE_STREAM -> stringResource(Res.string.sound_life_stream)
        ReminderSound.ZENITH_BELL -> stringResource(Res.string.sound_zenith_bell)
    }

    Column {
        Text(
            text = stringResource(Res.string.sound_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Box {
            Text(
                text = soundLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ReminderSound.entries.forEach { sound ->
                    val label = when (sound) {
                        ReminderSound.DEFAULT -> stringResource(Res.string.sound_default)
                        ReminderSound.MARTIAN_DRIP -> stringResource(Res.string.sound_martian_drip)
                        ReminderSound.LIFE_STREAM -> stringResource(Res.string.sound_life_stream)
                        ReminderSound.ZENITH_BELL -> stringResource(Res.string.sound_zenith_bell)
                    }
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSoundSelected(sound)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreferencesPreview() {
    WaterBuddyTheme {
        Surface {
            PreferencesContent(
                state = PreferencesUiState(dailyGoalMl = 2500),
                snackbarHostState = remember { SnackbarHostState() },
                onEvent = {},
            )
        }
    }
}
