package com.example.demometro.core.di

import com.example.demometro.core.navigation.Navigator
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface AppComponent : ViewModelGraph {
    val navigator: Navigator
}
