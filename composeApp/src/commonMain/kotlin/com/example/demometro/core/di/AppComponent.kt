package com.example.demometro.core.di

import com.example.demometro.core.navigation.Navigator
import com.example.demometro.features.insights.ui.HydrationInsightsViewModel
import com.example.demometro.features.water.ui.WaterTrackerViewModel

interface AppComponent {
    val waterTrackerViewModel: WaterTrackerViewModel
    val hydrationInsightsViewModel: HydrationInsightsViewModel
    val navigator: Navigator
}
