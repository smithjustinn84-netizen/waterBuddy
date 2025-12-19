# Kotlin Multiplatform Project Coding Standards & Architecture

You are an expert Kotlin Multiplatform (KMP) Software Engineer specializing in Clean Architecture, reactive programming, 
and robust offline-first systems. Your goal is to generate code that adheres to the following 
technical stack and architectural constraints, targeting both Android and iOS.

## 1. Core Architecture & Design
- **Clean Architecture:** Strictly separate layers into UI (Compose), Domain (Use Cases), and Data (Repositories/Data Sources).
- **Code Sharing:** Maximize code sharing in `commonMain`. Platform-specific code should be minimal and isolated in `androidMain` or `iosMain`.
- **MVVM + UDF:** 
    - State: ViewModels must expose a single StateFlow<UiState>.
    - Events: Use a MutableSharedFlow for one-time side effects (Navigation, Snacking, Toasts) referred to as SharedState.
    - Actions: Use a sealed interface UiIntent or UiAction for user inputs sent to the ViewModel.
- **UI Framework:** Compose Multiplatform for all UI components (Android & iOS).
- **Dependency Injection:** Use the **Metro** DI framework (compiler-plugin-based).

## 2. Technical Stack Implementation
### Dependency Injection (Metro)
- Use `@Inject` for constructor injection.
- Define dependency graphs using `@DependencyGraph`.
- Use `@Provides` for manual provider functions within graphs.
- Prefer `@ContributesBinding` and `@ContributesTo` for automatic graph aggregation.
- Ensure DI components are configured in `commonMain` where possible.

### Networking (Ktor)
- Use **Ktor** for all HTTP requests.
- Always use `HttpRequestBuilder` for configuration.
- Prefer Type-safe requests and Kotlinx.Serialization for JSON.

### Storage (Room & DataStore)
- Use **Room** (KMP) for complex relational local data.
- Use **DataStore** (Preferences or Proto) for simple key-value pairs or settings.
- All database operations must be `suspend` functions and reside in the Data layer.

### Compose & Navigation
- Use **Compose ViewModels** (via `viewModel()` or Metro-specific injection).
- Use **Compose Navigation** (Jetpack/JetBrains) for all screen transitions.
- Prefer **Type-safe Navigation** (passing Serializable classes) where possible.
- **Resources:** Use `composeResources` (Compose Multiplatform Resources) for strings, images, and fonts.

## 3. Coding Guidelines
- **Clean Code:** Keep functions small, use descriptive naming, and follow "S.O.L.I.D" principles.
- **Concurrency:** Always use Kotlin Coroutines. Avoid `GlobalScope`; use `viewModelScope` or custom scopes provided via DI.
- **State Management:** Use `produceState` or `collectAsStateWithLifecycle()` in Compose.

## 4. Domain & Data Layer Requirements
### Repository Pattern
- All data operations must go through a **Repository interface** defined in the Domain layer (`commonMain`).
- Implementation classes (`RepositoryImpl`) reside in the Data layer (`commonMain`).
- Repositories are responsible for logic involving switching between **Room** (local) and **Ktor** (remote).
- Always return Kotlin `Result` or a custom `Resource` sealed class to handle errors gracefully.

### Use Cases (Interactors)
- Create a dedicated class for every single business action (e.g., `GetUsersUseCase`, `SubmitOrderUseCase`).
- Use Cases must only depend on Repositories, not other Use Cases (unless strictly necessary).
- ViewModels must inject Use Cases via **Metro**, not Repositories directly.
- Each Use Case should perform exactly one task.
- Follow the naming convention: Verb + Noun + UseCase (e.g., GetAuthenticatedUserUseCase).
- Always use the `operator fun invoke` for execution.

### ViewModels (Presentation Layer)
- Use `androidx.lifecycle.viewmodel.compose.viewModel`.
- State must be an immutable data class.
- Side effects must be handled via a SharedFlow to prevent event loss or re-emission on configuration changes.

## 5. Offline-First Repository Pattern
- **Single Source of Truth:** The UI must only observe data from the **Room** database.
- **Sync Logic:** The Repository is responsible for fetching from **Ktor**, saving to **Room**, and handling errors without crashing the UI.
- **Metro DI:** Use `@ContributesBinding` to bind implementations to interfaces and `@SingleIn(AppScope::class)` for repositories.

## 6. Platform Specifics
- **Expect/Actual:** Use sparingly. Prefer defining an interface in `commonMain` and implementing it in `androidMain`/`iosMain`, then binding it via DI.
- **iOS:** Ensure `iosMain` exposes necessary entry points (e.g., `MainViewController`) for the Swift app.

## 7. Code Style & Best Practices

- Clean Code: Adhere to SOLID principles and DRY.
- Naming: Be verbose and descriptive. Avoid abbreviations.
- Error Handling: Wrap repository operations in `Result<T>` and map them to UI-friendly error states in the ViewModel.
- Concurrency: Use Kotlin Coroutines with appropriate Dispatchers (Dispatchers.IO for Data, Dispatchers.Default for heavy logic).

## 8. use a feature-based folder structure (also known as "Package by Feature"):
  - organizes code by what it is to the user (e.g., "Login," "Profile," "Cart") rather than what it does technically (e.g., "ViewModels," "Models," "Repositories").
  - The "Core" Exception: Not everything can be a feature. Shared items like your AppTheme, NetworkClient, or a StandardButton belong in a core or common package.
  - Encapsulation: Try to keep feature-specific logic internal. If the AuthRepository is only used by the auth feature, it should stay in that folder.
  - UI Sub-folders: As discussed earlier, inside each feature folder, create a ui sub-folder to separate the Compose code from the business logic (ViewModels/Models).
  - A feature-based architecture typically follows a "Vertical Slice" pattern where dependencies flow from the UI down to the Core, but never horizontally between features. 
  - Dependency Flow: Features can depend on core, but features should rarely depend directly on each other. If Feature A needs data from Feature B, they should usually communicate through a shared core repository or via the Navigation layer.

## Code Examples

```kotlin
data class UserProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)

data class User(val name: String, val email: String)

data class UserProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)

class ProfileViewModel : ViewModel() {
    // 1. Single State Flow (The MVI principle)
    private val _state = MutableStateFlow(UserProfileUiState())
    val state: StateFlow<UserProfileUiState> = _state.asStateFlow()

    // 2. User action handled by a public function (The MVVM/UDF input)
    fun loadProfile() {
        // State update performed imperatively inside the function body
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Simulate network call
                delay(1000)
                val user = User("Jane Doe", "jane@example.com")

                // Final state update
                _state.update { it.copy(isLoading = false, user = user) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Load failed") }
            }
        }
    }
}

@Composable
fun ProfileScreenUDF(viewModel: ProfileViewModel = viewModel()) {
    // View collects only the single state stream
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        // View calls a public function for the action
        viewModel.loadProfile()
    }

    when {
        state.isLoading -> Text("Loading profile...")
        state.user != null -> Column {
            Text("Name: ${state.user.name}")
            Text("Email: ${state.user.email}")
        }
        state.errorMessage != null -> Text("Error: ${state.errorMessage}")
    }
}

// 1. Define the possible one-off effects
sealed class ProfileUiEvent {
    object NavigateToLogin : ProfileUiEvent()
    data class ShowToast(val message: String) : ProfileUiEvent()
}

class ProfileViewModel : ViewModel() {
    // ... (StateFlow code from the previous example remains the same) ...

    // 2. The SharedFlow for Side Effects
    private val _events = MutableSharedFlow<ProfileUiEvent>()
    val events: SharedFlow<ProfileUiEvent> = _events.asSharedFlow()

    // 3. New action that triggers an Effect
    fun onDeleteProfileClicked() {
        // State update for an immediate UI change (e.g., button disable)
        // _state.update { it.copy(isDeleting = true) } 

        viewModelScope.launch {
            // Simulate API call
            delay(500)

            // 4. Emit the one-off navigation event
            _events.emit(ProfileUiEvent.NavigateToLogin)
            _events.emit(ProfileUiEvent.ShowToast("Profile Deleted!"))
        }
    }
}

@Composable
fun ProfileScreenUDF(
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController // Assuming you use Jetpack Compose Navigation
) {
    val state by viewModel.state.collectAsState()

    // 5. LaunchedEffect to collect and handle one-off events
    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileUiEvent.NavigateToLogin -> {
                    // Handle Navigation effect
                    navController.navigate("login_route") {
                        popUpTo("profile_route") { inclusive = true }
                    }
                }
                is ProfileUiEvent.ShowToast -> {
                    // Handle Toast/Snackbar effect
                    // Example: ScaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    // --- UI Rendering based on 'state' ---
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // ... Render UI based on 'state' ...
        Text(text = "Welcome, ${state.user?.name ?: "User"}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            // 6. UI sends an action back to the ViewModel
            onClick = viewModel::onDeleteProfileClicked,
            enabled = !state.isLoading // Example of using state for UI
        ) {
            Text("Delete Profile")
        }
    }
}
```

# Quick Start

This guide covers installation and the most common patterns in Metro to get you up and running quickly.

## Installation

Metro is primarily applied via its companion Gradle plugin.

```kotlin
plugins {
  kotlin("multiplatform") // or jvm, android, etc
  id("dev.zacsweers.metro") version "0.9.2"
}
```

## Basic Setup

### 1. Define your dependency graph

A dependency graph is the entry point to your object graph. Define it as an interface annotated with `@DependencyGraph`:

```kotlin
@DependencyGraph(AppScope::class)
interface AppGraph {
  val repository: Repository
}
```

### 2. Inject your classes

Use `@Inject` on classes to make them available for injection:

```kotlin
@Inject
class ApiClient(private val httpClient: HttpClient)

@Inject
class Repository(private val apiClient: ApiClient)
```

### 3. Create your graph

Use the `createGraph()` intrinsic to instantiate your graph:

```kotlin
val appGraph = createGraph<AppGraph>()
val repository = appGraph.repository
```

## The Api/Impl Pattern

One of the most common patterns in dependency injection is binding an implementation to its interface. Metro offers two approaches depending on your needs.

### Without aggregation (explicit binding)

Use `@Binds` to explicitly bind an implementation to its interface within a graph:

```kotlin
interface Repository {
  fun getData(): Data
}

@Inject
class RepositoryImpl(private val apiClient: ApiClient) : Repository {
  override fun getData(): Data = apiClient.fetch()
}

@DependencyGraph
interface AppGraph {
  val repository: Repository

  // Explicitly bind the implementation to the interface
  @Binds val RepositoryImpl.bind: Repository
}
```

### With aggregation (recommended for multi-module projects)

Using `@ContributesBinding` automatically binds your implementation to its interface and contributes it to any graph with the matching scope:

```kotlin
interface Repository {
  fun getData(): Data
}

// This class is automatically bound as a Repository in any graph with AppScope
@ContributesBinding(AppScope::class)
@Inject
class RepositoryImpl(private val apiClient: ApiClient) : Repository {
  override fun getData(): Data = apiClient.fetch()
}

// The graph automatically receives the Repository binding
@DependencyGraph(AppScope::class)
interface AppGraph {
  val repository: Repository  // Resolved to RepositoryImpl
}
```

@ContributesBinding` infers the bound type from the single supertype. For classes with multiple supertypes, specify it explicitly: `binding = binding<YourInterface>()`.

## Common Patterns

### Providing third-party classes

For classes, you don't control (e.g., OkHttp), use `@Provides` in a contributed interface:

```kotlin
@ContributesTo(AppScope::class)
interface NetworkProviders {
  @Provides
  fun provideCache(application: Application): Cache =
    Cache(application.cacheDir.resolve("http_cache"), 50L * 1024 * 1024)

  @Provides
  fun provideOkHttpClient(cache: Cache): OkHttpClient =
    OkHttpClient.Builder()
      .cache(cache)
      .build()
}
```

### Scoping (singletons)

Use `@SingleIn` to ensure only one instance exists per graph:

```kotlin
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DatabaseImpl : Database
```

### Testing with fakes

Use `createDynamicGraph()` to replace bindings in tests:

```kotlin
// Production code
@ContributesBinding(AppScope::class)
@Inject
class RealRepository : Repository

// Test code
class RepositoryTest {
  @Test
  fun testWithFake() {
    val testGraph = createDynamicGraph<AppGraph>(FakeBindings)
    // testGraph.repository now returns FakeRepository
  }

  @BindingContainer
  object FakeBindings {
    @Provides fun provideRepository(): Repository = FakeRepository()
  }
}
```

When mixing contributions between common and platform-specific source sets, you must define your final `@DependencyGraph` in the platform-specific code. This is because a graph defined in commonMain wouldn’t have full visibility of contributions from platform-specific types. A good pattern for this is to define your canonical graph in commonMain *without* a `@DependencyGraph` annotation and then a `{Platform}{Graph}` type in the platform source set that extends it and does have the `@DependencyGraph`. Metro automatically exposes bindings of the base graph type on the graph for any injections that need it.

```kotlin
// In commonMain
interface AppGraph {
  val httpClient: HttpClient
}

// In jvmMain
@DependencyGraph
interface JvmAppGraph : AppGraph {
  @Provides fun provideHttpClient(): HttpClient = HttpClient(Netty)
}

// In androidMain
@DependencyGraph
interface AndroidAppGraph : AppGraph {
  @Provides fun provideHttpClient(): HttpClient = HttpClient(OkHttp)
}
```