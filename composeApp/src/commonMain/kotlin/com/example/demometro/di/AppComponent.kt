package com.example.demometro.di

import com.example.demometro.HomeViewModel
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
abstract class AppComponent {
    abstract val homeViewModel: HomeViewModel
}

