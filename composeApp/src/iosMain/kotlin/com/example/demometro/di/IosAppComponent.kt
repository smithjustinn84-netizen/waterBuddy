package com.example.demometro.di

import com.example.demometro.core.di.AppComponent
import com.example.demometro.core.di.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
abstract class IosAppComponent : AppComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun create(): IosAppComponent
    }
}

