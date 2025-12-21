# Metro DI Context Example

This document provides a reference for AI agents to correctly implement Dependency Injection using Metro in this project.

## 1. Defining a Scope

Scopes are simple objects used to group related dependencies.

```kotlin
package com.example.waterbuddy.core.di
object AppScope
```

## 2. Dependency Graph

The graph interface defines the entry points for the dependency graph. It should extend `ViewModelGraph` if using ViewModels.

```kotlin
@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
    val navigator: Navigator
    val repository: WaterRepository
}
```

## 3. Injecting Dependencies

Use `@Inject` on the constructor of classes you want Metro to instantiate.

```kotlin
@Inject
class WaterRepositoryImpl(
    private val database: AppDatabase
) : WaterRepository
```

## 4. Binding Interfaces

To bind an implementation to an interface, use `@ContributesBinding`.

```kotlin
@ContributesBinding(AppScope::class)
@Inject
class WaterRepositoryImpl(...) : WaterRepository
```

## 5. Contributing ViewModels

ViewModels must be contributed to the map multibinding used by the `MetroViewModelFactory`.

```kotlin
@Inject
@ViewModelKey(HomeViewModel::class)
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class HomeViewModel(
    private val navigator: Navigator
) : ViewModel()
```

## 6. Providing Third-Party Classes

Use a `@ContributesTo` interface with `@Provides` methods for classes you don't control.

```kotlin
@ContributesTo(AppScope::class)
interface DatabaseModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
        return builder.setDriver(BundledSQLiteDriver()).build()
    }
}
```

## 7. Assisted Injection (ViewModel)

For ViewModels that need runtime parameters.

```kotlin
@AssistedInject
class DetailViewModel(
    @Assisted val id: String,
    private val repository: WaterRepository
) : ViewModel() {

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(id: String): DetailViewModel
    }
}
```

**Usage in Compose:**

```kotlin
val viewModel = assistedMetroViewModel<DetailViewModel, DetailViewModel.Factory> {
    create(id)
}
```
