package com.example.demometro.core.navigation

import com.example.demometro.core.di.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class NavigatorImpl : Navigator {

    private val _commands = Channel<NavigationCommand>(Channel.BUFFERED)
    override val commands: Flow<NavigationCommand> = _commands.receiveAsFlow()

    override fun navigate(destination: Any, clearBackStack: Boolean) {
        _commands.trySend(NavigationCommand.NavigateTo(destination, clearBackStack))
    }

    override fun goBack() {
        _commands.trySend(NavigationCommand.NavigateUp)
    }
}
