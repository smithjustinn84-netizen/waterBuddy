package com.example.waterbuddy.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
object WaterTracker : Route

@Serializable
object HydrationInsights : Route
