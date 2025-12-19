package com.example.demometro

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demometro.di.AppComponent
import com.example.demometro.presentation.water.WaterTrackerScreen
import dev.zacsweers.metro.createGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val component = remember { createGraph<AppComponent>() }

        NavHost(
            navController = navController,
            startDestination = WaterTracker
        ) {
            composable<WaterTracker> {
                WaterTrackerScreen(
                    viewModel = component.waterTrackerViewModel
                )
            }
        }
    }
}

