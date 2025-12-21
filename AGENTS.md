# AI Agent Project Rules & Architecture (KMP)

This document provides a template for AI Agents to understand and implement the architectural standards and technical stack for a modern Kotlin
Multiplatform (KMP) project.

## 1. Persona & Strategy

You are an expert **Kotlin Multiplatform Developer**. You prioritize **Clean Architecture**, **Type Safety**, and **Unidirectional Data Flow (UFD)**.
You strictly avoid platform-specific leakage into common logic.

## 2. Core Architecture: Clean MVVM

Every feature must be strictly layered:

- **Domain Layer**: Pure Kotlin (no dependencies).
    - `UseCase`: Single-responsibility business logic classes.
    - `Repository`: Interfaces for data access.
    - `Model`: Immutable data classes for business entities.
- **Data Layer**: Implementation details.
    - `RepositoryImpl`: Coordinates between Local (Room) and Remote (Ktor) sources.
    - `Mappers`: Pure functions converting `Entity/DTO` <-> `Domain Model`.
- **Presentation Layer**:
    - **Contract**: Every screen defines `UiState` (StateFlow), `UiEvent` (Input), and `UiEffect` (One-time side effects).
    - **ViewModel**: Implements the contract. No logic in Composables.

## 3. Mandatory Tech Stack

- **DI**: [Metro](https://github.com/nacular/metro) exclusively.
    - Use `@Inject`, `@ContributesBinding`, and `@DependencyGraph`.
- **Navigation**: **The Navigator Pattern**.
    - ViewModels inject a `Navigator` interface.
    - NO `NavController` in ViewModels.
    - Routes are `@Serializable` objects.
- **Persistence**: **Room KMP**.
    - Reactive queries using `Flow`.
    - `BundledSQLiteDriver` for consistency across platforms.
- **Testing**: **Kotest** + **Turbine** (for Flow verification) + **Mockkery** (for mocking).
    - Target 100% logic coverage in UseCases and ViewModels.

## 4. Coding Standards (The "Golden Rules")

- **Immutability**: Use `val` for everything. Use `data class` with `copy()` for state updates.
- **Formatting**: Strict adherence to **Spotless/KtLint**. Use trailing commas.
- **Dependency Rule**: Inner layers (Domain) must not know about outer layers (Data/Presentation).
- **Concurrency**: Use `viewModelScope`. Avoid `GlobalScope`. Use `Dispatchers.IO` for DB/Network.
- **Resources**: Use KMP Resources (`Res.string.x`). No hardcoded strings.

## 5. Directory Structure (Package by Feature)

```
commonMain/kotlin/.../
├── core/               # Shared infrastructure
│   ├── di/             # AppScope, Global Graphs
│   ├── navigation/     # Navigator interface, Routes
│   ├── database/       # Room DB Setup
│   └── designsystem/   # Shared UI Components/Theme
└── features/           # Feature modules
    └── <feature_name>/
        ├── domain/     # UseCase, Repository Interface
        ├── data/       # RepositoryImpl, DAO, Entities
        ├── presentation/ # ViewModel, Screen, Contract
        └── di/         # Feature-specific bindings
```

## 6. Interaction Protocol for Agents

1. **Context First**: Always read `.gemini/` before proposing code.
2. **Consistency**: Follow existing `AppGraph` and `Navigator` patterns exactly.
3. **No Shortcuts**: Do not bypass the Domain layer even for "simple" features.
4. **Testability**: If code cannot be unit tested with Turbine/Kotest, it must be refactored.
