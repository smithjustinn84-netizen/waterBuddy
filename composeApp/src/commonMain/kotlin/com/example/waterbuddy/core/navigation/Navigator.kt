package com.example.waterbuddy.core.navigation

import kotlinx.coroutines.flow.Flow

interface Navigator {
    val commands: Flow<NavigationCommand>
    fun navigate(destination: Route, clearBackStack: Boolean = false)
    fun goBack()
}

sealed interface NavigationCommand {
    data class NavigateTo(val destination: Route, val clearBackStack: Boolean) : NavigationCommand
    data object NavigateUp : NavigationCommand
}
