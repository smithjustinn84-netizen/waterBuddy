package com.example.waterbuddy.core.navigation

import kotlinx.coroutines.flow.Flow

interface Navigator {
    val commands: Flow<NavigationCommand>
    fun navigate(destination: Any, clearBackStack: Boolean = false)
    fun goBack()
}

sealed interface NavigationCommand {
    data class NavigateTo(val destination: Any, val clearBackStack: Boolean) : NavigationCommand
    data object NavigateUp : NavigationCommand
}
