package com.example.waterbuddy

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.waterbuddy.core.di.AppGraph
import com.example.waterbuddy.core.navigation.HydrationInsights
import com.example.waterbuddy.core.navigation.NavigationCommand
import com.example.waterbuddy.core.navigation.Preferences
import com.example.waterbuddy.core.navigation.Route
import com.example.waterbuddy.core.navigation.WaterTracker
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import com.example.waterbuddy.features.insights.ui.HydrationInsightsScreen
import com.example.waterbuddy.features.preferences.ui.PreferencesScreen
import com.example.waterbuddy.features.watertracker.ui.WaterTrackerScreen
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.nav_insights
import waterbuddy.composeapp.generated.resources.nav_preferences
import waterbuddy.composeapp.generated.resources.nav_tracker

@Composable
fun App(component: AppGraph) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides component.metroViewModelFactory) {
        WaterBuddyTheme {
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

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TOP_LEVEL_DESTINATIONS.forEach { destination ->
                            val selected =
                                currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
                            NavigationBarItem(
                                icon = {
                                    Text(
                                        text = destination.iconEmoji,
                                        modifier = Modifier,
                                    )
                                },
                                label = { Text(stringResource(destination.iconTextId)) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            )
                        }
                    }
                },
                contentWindowInsets = WindowInsets(0.dp),
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = WaterTracker,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                ) {
                    composable<WaterTracker> {
                        WaterTrackerScreen()
                    }
                    composable<HydrationInsights> {
                        HydrationInsightsScreen()
                    }
                    composable<Preferences> {
                        PreferencesScreen()
                    }
                }
            }
        }
    }
}

private data class TopLevelDestination(
    val route: Route,
    val iconEmoji: String,
    val iconTextId: org.jetbrains.compose.resources.StringResource,
)

private val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = WaterTracker,
            iconEmoji = "💧",
            iconTextId = Res.string.nav_tracker,
        ),
        TopLevelDestination(
            route = HydrationInsights,
            iconEmoji = "📊",
            iconTextId = Res.string.nav_insights,
        ),
        TopLevelDestination(
            route = Preferences,
            iconEmoji = "⚙️",
            iconTextId = Res.string.nav_preferences,
        ),
    )
