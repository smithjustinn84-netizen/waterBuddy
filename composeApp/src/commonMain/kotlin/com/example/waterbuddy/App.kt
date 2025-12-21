package com.example.waterbuddy

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.waterbuddy.core.di.AppComponent
import com.example.waterbuddy.core.navigation.HydrationInsights
import com.example.waterbuddy.core.navigation.NavigationCommand
import com.example.waterbuddy.core.navigation.WaterTracker
import com.example.waterbuddy.features.insights.ui.HydrationInsightsScreen
import com.example.waterbuddy.features.watertracker.ui.WaterTrackerScreen
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun App(component: AppComponent) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides component.metroViewModelFactory) {
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
                    WaterTrackerScreen()
                }
                composable<HydrationInsights> {
                    HydrationInsightsScreen()
                }
            }
        }
    }
}
