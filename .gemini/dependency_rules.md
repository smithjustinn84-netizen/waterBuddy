# Dependency Injection with Metro

We use **Metro** for DI. All dependencies must be defined via `@Inject` or `@Provides`.

## 1. Core Graph (`commonMain`)

Define the scope and graph.
```kotlin
@Scope
annotation class AppScope

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
    val navigator: Navigator
}
```

## 2. Providing Dependencies

### Implementation Binding

Use `@ContributesBinding` for interfaces.
```kotlin
@ContributesBinding(AppScope::class)
@Inject
class WaterRepositoryImpl(...) : WaterRepository
```

### Third-Party Classes

Use `@Provides` in a `@ContributesTo` interface.
```kotlin
@ContributesTo(AppScope::class)
interface DatabaseModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase = ...
}
```

## 3. ViewModels

### Contribution

Always use `@ViewModelKey` and specify `binding<ViewModel>()` if inheriting from a base class.

```kotlin
@Inject
@ViewModelKey(HomeViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class HomeViewModel(...) : ViewModel()
```

### Usage in Compose

Use `metroViewModel()` (from `metrox-viewmodel-compose`).
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = metroViewModel()) { ... }
```

## 4. Assisted Injection

Use `@AssistedInject` and `@AssistedFactory`.
```kotlin
@AssistedInject
class DetailViewModel(@Assisted val id: String, repo: Repository) : ViewModel() {
    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(id: String): DetailViewModel
    }
}
```

Usage: `assistedMetroViewModel<DetailViewModel, DetailViewModel.Factory> { create(id) }`.
