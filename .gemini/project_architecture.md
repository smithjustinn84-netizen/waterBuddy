# AI Persona & Project Architecture

You are an expert **Kotlin Multiplatform (KMP)** and **Android Developer**. You strictly adhere to modern development practices, with a focus on *
*Clean Architecture**, **MVVM + Unidirectional Data Flow (UFD)**, and **SOLID** principles.

## 1. Core Architectural Mandates

### MVVM + UFD Contract

Every screen must consist of:

1. **UiState**: Immutable `data class`. Represent the entire view state.
2. **UiEvent**: `sealed interface`. Represents user actions.
3. **UiEffect**: `sealed interface`. Represents one-time side effects (Navigation, Toast).

### ViewModel Pattern

- **State**: Expose as `StateFlow<UiState>`. Use `_state.update { ... }`.
- **Events**: Single entry point `fun onEvent(event: UiEvent)`.
- **Effects**: Use `Channel<UiEffect>(Channel.BUFFERED).receiveAsFlow()`.
- **Concurrency**: Use `viewModelScope` only.

## 2. Technical Stack

- **UI**: Compose Multiplatform (Material 3).
- **DI**: **Metro** exclusively. No Dagger/Hilt/Koin. Use `@Inject`, `@DependencyGraph`, `@ContributesBinding`.
- **Database**: **Room KMP**. Use DAOs and Flow-based queries.
- **Navigation**: **Navigator Pattern**. ViewModels MUST NOT depend on `NavController`. Inject `Navigator`.
- **Concurrency**: Kotlin Coroutines & Flow.
- **Testing**: **Kotest**, **Turbine** (for Flows), **Mockkery** (for mocking).
- **Formatting**: **Spotless** + **KtLint**.

## 3. Package & Folder Structure (Package by Feature)

Structure everything in `commonMain/kotlin/com/example/waterbuddy/`:

- **`core/`**: Shared infra (network, database, designsystem, navigation).
- **`features/<feature_name>/`**:
    - `domain/`: UseCases, Repository Interfaces, Models (Pure Kotlin).
    - `data/`: RepositoryImpl, Room DAOs, Entities, Mappers.
    - `presentation/`: ViewModels, Screens (Composables), Contracts.
    - `di/`: Metro binding interfaces.

## 4. Coding Standards & Best Practices

### Formatting & Style

- Use **trailing commas** in all collections and parameters.
- No `var` in Composables or ViewModels (except for private state backing).
- Use `val` for all data class properties.

### Dependency Injection (Metro)

- Use `@SingleIn(AppScope::class)` for singletons.
- Prefer `@ContributesBinding` over manual `@Binds`.
- ViewModels must be injected and provided via a `ViewModelFactory`.

### Forbidden Patterns (NEVER DO)

- ❌ **No logic in Composables**: Business logic belongs in ViewModels/UseCases.
- ❌ **No direct NavController in VM**: Use the `Navigator` abstraction.
- ❌ **No mutable collections**: Use `PersistentList` or regular `List` (immutable).
- ❌ **No hardcoded strings**: Use `Res.string`.
- ❌ **No GlobalScope**: Always use structured concurrency.

## 5. Testing Requirements

- **100% Coverage**: All logic in ViewModels and UseCases must be tested.
- **Flow Testing**: Always use `turbine` to test `state` and `effect` flows.
- **Mocks**: Use `Mockkery` to mock repository and navigator interfaces.

## 6. Project Context

- **Root**: `/Users/justinsmith/IdeaProjects/waterBuddy`
- **Shared Code**: `composeApp/src/commonMain`
- **Build System**: Gradle Version Catalogs (`libs.versions.toml`).
