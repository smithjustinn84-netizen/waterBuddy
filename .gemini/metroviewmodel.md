# Context Example: Metro DI & ViewModels

Use this as a reference for implementing Dependency Injection and ViewModels using Metro.

## 1. Core Graph Setup

The `AppGraph` serves as the central hub for all dependencies.

```kotlin
@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
    val navigator: Navigator
    // Add other top-level dependencies here
}
```

## 2. ViewModel Implementation

Every ViewModel must be injected and contributed to the `AppScope` map.

```kotlin
@Inject
@ViewModelKey(MyViewModel::class)
// Note: specify binding<ViewModel>() if inheriting from a base class
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class MyViewModel(
    private val navigator: Navigator,
    private val repository: MyRepository
) : ViewModel() {
    // ... logic
}
```

## 3. Repository & Service Binding

Bind implementations to interfaces using `@ContributesBinding`.

```kotlin
@ContributesBinding(AppScope::class)
@Inject
class MyRepositoryImpl(private val database: AppDatabase) : MyRepository
```

## 4. Manual Assisted Injection

Use this pattern when a ViewModel requires runtime parameters (e.g., an ID from a route).

```kotlin
@AssistedInject
class DetailViewModel(
    @Assisted val itemId: String,
    private val repository: MyRepository
) : ViewModel() {

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(itemId: String): DetailViewModel
    }
}
```

## 5. Compose Usage

Retrieve ViewModels in your Composables using the following helpers:

```kotlin
// Standard ViewModel
@Composable
fun MyScreen(viewModel: MyViewModel = metroViewModel()) {
    ...
}

// Assisted ViewModel
@Composable
fun DetailScreen(id: String) {
    val viewModel = assistedMetroViewModel<DetailViewModel, DetailViewModel.Factory> {
        create(id)
    }
}
```

## 6. Providing Platform Dependencies

For dependencies like `AppDatabase` that require platform-specific builders:

```kotlin
@ContributesTo(AppScope::class)
interface DataModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase =
        builder.setDriver(BundledSQLiteDriver()).build()
}
```
