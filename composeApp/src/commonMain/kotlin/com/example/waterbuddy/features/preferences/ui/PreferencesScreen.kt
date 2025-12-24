package com.example.waterbuddy.features.preferences.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.waterbuddy.features.preferences.presentation.PreferencesUiEffect
import com.example.waterbuddy.features.preferences.presentation.PreferencesUiEvent
import com.example.waterbuddy.features.preferences.presentation.PreferencesUiState
import com.example.waterbuddy.features.preferences.presentation.PreferencesViewModel
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.ml_suffix
import waterbuddy.composeapp.generated.resources.preferences_footer
import waterbuddy.composeapp.generated.resources.preferences_title_main
import waterbuddy.composeapp.generated.resources.preferences_title_sub
import waterbuddy.composeapp.generated.resources.set_goal_title

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
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
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
                )
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

@Preview
@Composable
fun PreferencesDarkModePreview() {
    WaterBuddyTheme(darkTheme = true) {
        Surface {
            PreferencesContent(
                state = PreferencesUiState(dailyGoalMl = 2500),
                snackbarHostState = remember { SnackbarHostState() },
                onEvent = {},
            )
        }
    }
}
