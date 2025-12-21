# Navigation: The Navigator Pattern

ViewModels must NEVER depend on `NavController`. Use the `Navigator` abstraction.

## 1. The Interface (`commonMain/core/navigation`)
```kotlin
interface Navigator {
    val commands: Flow<NavigationCommand>
    fun navigate(route: Route)
    fun pop()
}

sealed interface NavigationCommand {
    data class To(val route: Route) : NavigationCommand
    data object Back : NavigationCommand
}
```

## 2. The Implementation

- Implement as a `@SingleIn(AppScope::class)` singleton.
- Use a `Channel(Channel.BUFFERED)` to hold commands.

## 3. ViewModel Usage
```kotlin
@Inject
class SettingsViewModel(private val navigator: Navigator) : ViewModel() {
    fun onBackClicked() = navigator.pop()
    fun onAboutClicked() = navigator.navigate(Route.About)
}
```

## 4. Compose Integration

Observe the `navigator.commands` in the root Composable and execute them using the `NavController`.
```kotlin
val commands by navigator.commands.collectAsStateWithLifecycle(null)
LaunchedEffect(commands) {
    when(val cmd = commands) {
        is To -> navController.navigate(cmd.route)
        is Back -> navController.popBackStack()
    }
}
```

All routes must be defined in `core/navigation/Route.kt` using `@Serializable` objects.
