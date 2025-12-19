package com.example.demometro

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demometro.core.di.AppComponent
import com.example.demometro.core.navigation.WaterTracker
import com.example.demometro.features.water.ui.WaterTrackerScreen

@Composable
fun App(component: AppComponent) {
    MaterialTheme {
        val navController = rememberNavController()

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

