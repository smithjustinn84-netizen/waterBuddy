# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

waterBuddy is a Kotlin Multiplatform (KMP) water intake habit tracker targeting Android and iOS. It
follows Clean Architecture principles and uses Metro DI framework for dependency injection.

**Package**: `com.example.waterbuddy`

## Common Commands

### Build Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Build for all platforms
./gradlew build
```

### Testing

```bash
# Run all tests
./gradlew test

# Run common tests only
./gradlew :composeApp:commonTest

# Run Android unit tests
./gradlew :composeApp:testDebugUnitTest

# Run JVM tests
./gradlew :composeApp:jvmTest

# Run single test
./gradlew :composeApp:commonTest --tests "WaterTrackerViewModelTest"
```

### Code Coverage

```bash
# Generate Kover coverage report
./gradlew koverHtmlReport

# View report at: composeApp/build/reports/kover/html/index.html
```

### iOS Development

- Open `iosApp/iosApp.xcodeproj` in Xcode to run the iOS app
- Native SQLite driver requires linker option `-lsqlite3` (already configured)

## Architecture

### Layered Clean Architecture + MVVM + UDF

The project strictly follows a **feature-based folder structure** (Package by Feature), not by
architectural layer:

```
commonMain/kotlin/com/example/waterbuddy/
├── core/                      # Shared infrastructure
│   ├── database/             # Room database setup
│   ├── di/                   # Metro DI configuration
│   ├── navigation/           # Type-safe navigation
│   └── theme/                # Compose design system
└── features/                  # Business features
    ├── watertracker/
    │   ├── domain/           # Use cases, repository interfaces, models
    │   ├── data/             # Repository implementations, Room DAOs/entities
    │   └── ui/               # ViewModels, Screens, Compose components
    └── insights/
        ├── domain/
        ├── data/
        └── ui/
```

### Core Principles

1. **MVVM + Unidirectional Data Flow (UDF)**:
    - State: ViewModels expose a single `StateFlow<UiState>`
    - Events: Use `Channel` or `SharedFlow` for one-time side effects (navigation, toasts)
    - Actions: Use sealed interface `UiIntent` or `UiAction` for user inputs

2. **Clean Architecture**:
    - Domain layer: Pure Kotlin, no platform dependencies
    - Data layer: Room/Ktor implementations
    - UI layer: ViewModels and Composables

3. **Dependency Flow**:
    - Features can depend on `core`, but NOT on each other
    - UI → Domain → Data (never sideways between features)

### Metro Dependency Injection

- All ViewModels and Use Cases must be provided via Metro graph
- Use `@Inject` for constructor injection
- Use `@ContributesBinding(AppScope::class)` for interface implementations
- Use `@SingleIn(AppScope::class)` for singletons
- ViewModels use `@ViewModelKey` and `@ContributesIntoMap`

**Graph Setup**:

- `AppComponent` (commonMain) defines the canonical graph (extends `ViewModelGraph`)
- Platform-specific graphs (AndroidAppComponent, IosAppComponent) extend AppComponent with
  `@DependencyGraph`

**ViewModel Injection**:

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = metroViewModel()) {
    // ...
}
```

### Navigation

**Type-Safe Compose Navigation** (androidx.navigation:navigation-compose 2.8+):

- All routes defined as `@Serializable` data classes/objects in `core/navigation/Route.kt`
- Use `composable<T>` not `composable("route")`
- Navigate: `navController.navigate(HydrationInsights)`
- Navigator abstraction in `core/navigation/Navigator` for feature isolation

**Prohibited**: Hardcoded route strings like `"profile/{id}"`

### State Management Pattern

Every feature screen must have:

1. **UiState**: Immutable data class for view state
2. **UiEvent**: Sealed interface for user actions
3. **UiEffect**: (Optional) Sealed interface for side effects

Example:

```kotlin
data class MyUiState(val isLoading: Boolean = false)
sealed interface MyUiEvent {
    data object LoadData : MyUiEvent
}
sealed interface MyUiEffect {
    data object NavigateBack : MyUiEffect
}
```

### Room Database

- Schema directory: `composeApp/schemas/`
- Platform drivers configured per target (Android, iOS, JVM)
- Type converters in `core/database/converter/`
- All database operations are `suspend` functions

## Testing Strategy

### Framework Stack

- **kotlin.test**: Base test framework (preferred over JUnit for KMP)
- **Kotest**: Assertions and test framework
- **Turbine**: Testing StateFlow/SharedFlow
- **Mokkery**: Mocking (not Mockkery typo - it's the library name)
- **Coroutines Test**: Testing coroutines with TestDispatcher

### Test Organization

- **commonTest**: Shared tests for all platforms
- **androidUnitTest**: Android-specific tests (uses JUnit5)
- **jvmTest**: JVM-specific tests

### Test Naming

Use descriptive test names following pattern: `methodName_condition_expectedResult`

## Development Guidelines

### Required Patterns

1. **ViewModels**:
    - Must extend `androidx.lifecycle.ViewModel`
    - Use `viewModelScope` for coroutines (NEVER `GlobalScope`)
    - Expose immutable `StateFlow<UiState>`
    - Single entry point: `fun onEvent(event: UiEvent)`

2. **Use Cases**:
    - One use case per business action
    - Must use `operator fun invoke()` for execution
    - Naming: Verb + Noun + UseCase (e.g., `AddWaterIntakeUseCase`)
    - ViewModels depend on Use Cases, NOT repositories directly

3. **Repository Pattern**:
    - Interface in `domain/` layer
    - Implementation in `data/` layer
    - Use `@ContributesBinding` to bind implementation

### Forbidden Patterns

- ❌ Mutable state (var) in Composables - hoist to ViewModel
- ❌ Exposing `MutableStateFlow` to UI
- ❌ Business logic in Composables
- ❌ `GlobalScope` for coroutines
- ❌ Cross-feature dependencies (Feature A importing Feature B)
- ❌ God-packages named `common` or `util`
- ❌ Hardcoded version numbers in build files (use libs.versions.toml)

### Platform-Specific Code

- Use expect/actual sparingly
- Prefer: Define interface in `commonMain`, implement in `androidMain`/`iosMain`, bind via DI
- Platform implementations must mirror `commonMain` package structure

### Dependency Management

All dependencies managed in `gradle/libs.versions.toml` - never hardcode versions in build files.

## Security (OWASP)

- Validate all data inputs at ViewModel/UseCase boundary
- Never log sensitive user data or tokens
- Use secure storage for authentication tokens
- Implement certificate pinning for network requests
- Encrypt sensitive data at rest
