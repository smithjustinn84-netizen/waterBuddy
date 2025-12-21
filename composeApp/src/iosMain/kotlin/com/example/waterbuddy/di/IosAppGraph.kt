package com.example.waterbuddy.di

import com.example.waterbuddy.core.di.AppGraph
import com.example.waterbuddy.core.di.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
abstract class IosAppGraph : AppGraph {
    @DependencyGraph.Factory
    interface Factory {
        fun create(): IosAppGraph
    }
}
