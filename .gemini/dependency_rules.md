# Dependency Injection Rules (Metro)

- Dependency Injection: Use Metro exclusively. All ViewModels and UseCases must be provided via the Metro graph.
- All dependencies should be scoped appropriately.

## This guide covers installation and the most common patterns in Metro to get you up and running quickly.

## Installation

Metro is primarily applied via its companion Gradle plugin.

```kotlin
plugins {
  kotlin("multiplatform") // or jvm, android, etc
  id("dev.zacsweers.metro") version "0.9.2"
}
```

## Basic Setup for Metro

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

One of the most common patterns in dependency injection is binding an implementation to its interface. Metro offers two
approaches depending on your needs.

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

Using `@ContributesBinding` automatically binds your implementation to its interface and contributes it to any graph
with the matching scope:

```kotlin
interface Repository {
  fun getData(): Data
}

// This class is automatically bound as Repository in any graph with AppScope
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

`@ContributesBinding` infers the bound type from the single supertype. For classes with multiple supertypes, specify it
explicitly: `binding = binding<YourInterface>()`.

## Common Patterns

### Providing third-party classes

For classes you don't control (e.g. OkHttp), use `@Provides` in a contributed interface:

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

When mixing contributions between common and platform-specific source sets, you must define your final @DependencyGraph
in the platform-specific code. This is because a graph defined in commonMain wouldn’t have full visibility of
contributions
from platform-specific types. A good pattern for this is to define your canonical graph in commonMain without
a @DependencyGraph annotation and then a {Platform}{Graph} type in the platform source set that extends it and does
have the @DependencyGraph. Metro automatically exposes bindings of the base graph type on the graph for any injections
that need it.

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

## Multi-module projects

In multi-module projects, you can define separate graphs for each module and then compose them into a single application
graph.
