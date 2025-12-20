package com.example.demometro

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demometro.core.di.AppComponent
import com.example.demometro.core.navigation.HydrationInsights
import com.example.demometro.core.navigation.NavigationCommand
import com.example.demometro.core.navigation.WaterTracker
import com.example.demometro.features.insights.ui.HydrationInsightsScreen
import com.example.demometro.features.watertracker.ui.WaterTrackerScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun App(component: AppComponent) {
    MaterialTheme {
        val navController = rememberNavController()
        val navigator = component.navigator

        LaunchedEffect(navigator) {
            navigator.commands.collectLatest { command ->
                when (command) {
                    is NavigationCommand.NavigateTo -> {
                        navController.navigate(command.destination) {
                            if (command.clearBackStack) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    NavigationCommand.NavigateUp -> navController.navigateUp()
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = WaterTracker
        ) {
            composable<WaterTracker> {
                WaterTrackerScreen(
                    viewModel = component.waterTrackerViewModel
                )
            }
            composable<HydrationInsights> {
                HydrationInsightsScreen(
                    viewModel = component.hydrationInsightsViewModel
                )
            }
        }
    }
}
