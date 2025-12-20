## Navigation Architecture: The Navigator Pattern

**Core Principle:** ViewModels must NEVER depend on `androidx.navigation.NavController` or any Android-specific
navigation framework classes. Navigation is a side-effect that must be abstracted.

### 1. The Navigator Interface

* **Requirement:** All navigation logic must be routed through a `Navigator` interface defined in the `commonMain`
  `core` module.
* **Mechanism:** The Navigator must expose a `Flow<NavigationCommand>` (or similar) that the UI layer observes.
* **Injection:** The `Navigator` must be a `@SingleIn(AppScope::class)` singleton injected into ViewModels via Metro.

### 2. Forbidden Patterns

* ❌ **Prohibited:** Injecting `NavController` into a ViewModel.
* ❌ **Prohibited:** Passing `NavController` lambdas (e.g., `onNext: () -> Unit`) into ViewModels.
* ❌ **Prohibited:** Handling navigation logic directly in Composable `onClick` handlers without passing through the
  ViewModel/Navigator.

### 3. Required Implementation Structure

**The Contract (Common):**

```kotlin
interface Navigator {
    fun navigate(destination: Any, clearBackStack: Boolean = false)
    fun goBack()
}
```

**The Implementation (core/navigation)**

We implement this as a Singleton using Metro. It holds a Channel that the UI will listen to.

```kotlin
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
```

The Usage (ViewModel):

```kotlin
@Inject
class FeatureViewModel(
    private val navigator: Navigator // ✅ Correct: Abstracted dependency
) : BaseViewModel<T>() {

    fun onConfirm() {
        // ✅ Correct: Type-safe, testable navigation call
        navigator.navigate(Destination.Dashboard) 
    }
}
```

### 4. UI Layer Responsibility

- The root Composable (or Activity) is the only place allowed to hold a reference to NavController.
- It must observe the Navigator's commands and forward them to the NavController.