package com.example.demometro.di

import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
abstract class IosAppComponent : AppComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun create(): IosAppComponent
    }
}

