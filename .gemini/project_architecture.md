# Project Architecture & Coding Standards

## 1. Core Architecture: MVVM + UFD

All feature development must strictly follow **Model-View-ViewModel (MVVM)** with **Unidirectional Data Flow (UFD)**.

### The Contract

Every screen or major component must be composed of three distinct artifacts:

1. **UiState**: An immutable `data class` representing the entire view state.
2. **UiEvent**: A `sealed interface` representing all possible user actions (clicks, inputs).
3. **UiEffect** (Optional): A `sealed interface` for "fire-and-forget" events (Navigation, Snackbars).

### The ViewModel Pattern

* **State Management**: Expose state as a `StateFlow<UiState>`.
* **Event Handling**: Expose a single public entry point: `fun onEvent(event: UiEvent)`.
* **Side Effects**: Use `Channel` or `SharedFlow` for `UiEffect`.
* **Concurrency**: All coroutines must be launched via `viewModelScope`.

**Required Structure:**

```kotlin
class ExampleViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExampleState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ExampleEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ExampleEvent) {
        when(event) {
            is ExampleEvent.LoadData -> loadData()
            is ExampleEvent.OnItemClicked -> navigateToDetails(event.id)
        }
    }
}
```

## Project Overview

This is a Kotlin Multiplatform (KMP) project targeting Android and iOS.

- **Shared Code**: Located in `composeApp/src/commonMain`. This is where most of the application logic and UI should
  reside.
- **Android Code**: Located in `composeApp/src/androidMain`. Use this for Android-specific implementations.
- **iOS Code**: Located in `composeApp/src/iosMain`. Use this for iOS-specific implementations.
- **UI Framework**: Compose Multiplatform.

## Coding Guidelines

1. **Language**: Use Kotlin for all code.
2. **UI Development**:
    - Use Compose Multiplatform for UI components to ensure they work on both Android and iOS.
    - Place UI code in `commonMain` unless it requires platform-specific APIs.
3. **State Management**:
    - Use `ViewModel` and `StateFlow` for managing UI state in a reactive way.
    - Ensure ViewModels are shared in `commonMain` if possible.
4. **Dependencies**:
    - Use Gradle Version Catalogs (`gradle/libs.versions.toml`) for adding or updating dependencies.
    - Do not hardcode version numbers in `build.gradle.kts` files.

## File Structure

- `composeApp/src/commonMain/kotlin`: Shared business logic and UI.
- `composeApp/src/androidMain/kotlin`: Android-specific implementations.
- `composeApp/src/iosMain/kotlin`: iOS-specific implementations.
- `iosApp/`: Native iOS application entry point (Swift/SwiftUI).

## Best Practices

- Prioritize code sharing. Only write platform-specific code when absolutely necessary.
- Follow modern Android and Kotlin best practices.
- Keep components small and reusable.

## 2. Technology Stack Requirements

- Dependency Injection: Use Metro exclusively. All ViewModels and UseCases must be provided via the Metro graph.
- Persistence: Use Room for all local data storage.
- Networking: Use Ktor for API communication.
- UI: Use Jetpack Compose (Multiplatform).
- Jetpack Compose Navigation with Type Safe operators

Testing:

- Use `Turbine` for testing Flows (State/Effect).
- Use `Mockkery` for mocking dependencies.
- Use `Robolectric` for Android integration tests.

## 3. Design Principles

- SOLID: Strictly adhere to SOLID principles. Dependency Inversion is critical—depend on abstractions (interfaces), not
  concretions.
- Clean Architecture:
    - Domain Layer: Pure Kotlin, no platform dependencies.
    - Data Layer: Handles Room/Ktor implementations.
    - UI Layer: ViewModels and Composables.

- Security (OWASP):
    - Validate all data inputs at the ViewModel/UseCase boundary.
    - Never log sensitive user data or tokens.
    - Use secure storage for authentication tokens.
    - Implement certificate pinning for all network requests.
    - All sensitive data must be encrypted at rest.

## 4. Forbidden Patterns

- Do not use mutable state variables (var) inside UI Composables; hoist them to the ViewModel.
- Do not expose MutableStateFlow to the view.
- Do not put business logic inside Composables.
- Do not use `GlobalScope` for coroutines. Always use a structured concurrency scope.
