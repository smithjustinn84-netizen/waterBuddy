package com.example.demometro.di

import com.example.demometro.presentation.water.WaterTrackerViewModel
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
abstract class AppComponent {
    abstract val waterTrackerViewModel: WaterTrackerViewModel
}

