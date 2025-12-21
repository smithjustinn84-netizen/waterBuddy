package com.example.waterbuddy.di

import com.example.waterbuddy.core.di.AppComponent
import com.example.waterbuddy.core.di.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
abstract class IosAppComponent : AppComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun create(): IosAppComponent
    }
}

