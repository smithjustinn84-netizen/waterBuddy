package com.example.waterbuddy.core.di

import com.example.waterbuddy.core.navigation.Navigator
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface AppGraph : ViewModelGraph {
    val navigator: Navigator
}
