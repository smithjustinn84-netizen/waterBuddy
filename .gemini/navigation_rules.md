# Navigation Strategy: Type-Safe Compose Navigation

**Library Requirement:** Use `androidx.navigation:navigation-compose` (Version 2.8.0+).
**Serialization Requirement:** Use `kotlinx.serialization` for all route definitions.

## 1. Route Definitions

* **Strictly Forbidden:** Do NOT use hardcoded route strings (e.g., `"profile/{id}"`).
* **Required:** Define all destinations as `@Serializable` data classes or objects.
* **Grouping:** Group related routes into a `sealed interface` to represent a generic "Destination" or specific feature
  graphs.

**Example Contract:**

```kotlin
sealed interface Destination {
    
    @Serializable
    data object Home : Destination

    @Serializable
    data class Details(val id: String) : Destination

    @Serializable
    data class Settings(val userId: String, val advancedMode: Boolean) : Destination
}
```

## 2. NavHost Implementation

- Use the type-safe composable<T> function, not composable("route").
- Obtain arguments directly from the type, avoiding backStackEntry.arguments.getString("key").

**Example:**

```kotlin
NavHost(navController = navController, startDestination = Destination.Home) {
    
    composable<Destination.Home> {
        HomeScreen(viewModel = koinViewModel()) // or Metro injection
    }

    composable<Destination.Details> { backStackEntry ->
        val route: Destination.Details = backStackEntry.toRoute()
        DetailsScreen(itemId = route.id)
    }
}
```

## 3. Navigation Actions

- Navigate using the instance of the object, not a string builder.
- Pass these actions to ViewModels via the UiEffect channel defined in BaseViewModel.

**Example:**

```kotlin
// In UI
navController.navigate(Destination.Details(id = "123"))

// In ViewModel (via Effect)
fun onSelect(id: String) {
    sendEffect(UiEffect.NavigateTo(Destination.Details(id)))
}
```

## 4. Integration with Metro & KMP

- Routes are pure Kotlin data classes; keep them in the commonMain presentation layer so they are accessible to both
  Android and iOS implementations (if using Compose Multiplatform navigation).