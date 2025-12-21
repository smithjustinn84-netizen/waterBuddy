package com.example.waterbuddy.core.di

import com.example.waterbuddy.core.navigation.Navigator
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface AppComponent : ViewModelGraph {
    val navigator: Navigator
}
