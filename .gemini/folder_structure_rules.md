# Folder Structure & Packaging Rules

## 1. Top-Level Strategy: Package by Feature

* **Do not** organize the top level by architectural layers (e.g., no root folders named `repositories`, `viewmodels`,
  `models`).
* **Do** organize the top level by business capability or user journey (e.g., `features/login`, `features/inventory`,
  `features/settings`).

## 2. Feature Internal Structure (Clean Architecture)

Inside every feature package (e.g., `com.app.features.login`), strict layering must be enforced:

* **`domain/`**: The pure business logic.
  * Contains: `UseCase` classes, `Repository` interfaces, Domain `Model` data classes.
  * **Rule**: NO Android dependencies, NO serialization annotations, NO Compose.

* **`data/`**: The implementation details.
  * Contains: `RepositoryImpl`, `Room` DAOs/Entities, `Ktor` DTOs/Services, Mappers.
  * **Rule**: Must implement interfaces from `domain`.

* **`presentation/`**: The UI and State.
  * Contains: `ViewModel` (extending BaseViewModel), `Screen` (Composables), `State`/`Event`/`Effect` contracts.
  * **Rule**: Depends on `domain`. NO access to `data` directly.

* **`di/`**: Feature-specific Dependency Injection.
  * Contains: Metro `@Component` or `@Module` interfaces for this feature.

## 3. Core & Shared Modules

Functionality used across multiple features must reside in a `core` package, not in a generic "utils" folder.

* **`core/`**
  * `architecture/` (BaseViewModel, UiState, etc.)
  * `network/` (Ktor Client setup)
  * `database/` (Room Database setup)
  * `designsystem/` (Shared Compose components, Typography, Colors)

## 4. Kotlin Multiplatform (KMP) Conventions

* The package structure in `commonMain` is the source of truth.
* `androidMain` and `iosMain` must mirror the `commonMain` package structure exactly when providing platform-specific
  implementations (expect/actual).

## 5. Prohibited Patterns

* ❌ **God-packages**: No huge packages named `common` or `util` that contain mixed business logic.
* ❌ **Cross-Feature coupling**: Feature A must not import Feature B's internal classes. Communication between features
  must happen via the `domain` layer or a Navigation Manager in `core`.

```
commonMain/kotlin/com/example/app/
├── core/
│   ├── architecture/ (BaseViewModel.kt)
│   ├── network/ (HttpClient.kt)
│   └── designsystem/
├── features/
│   ├── auth/
│   │   ├── di/ (AuthComponent.kt)
│   │   ├── domain/
│   │   │   ├── LoginUseCase.kt
│   │   │   └── AuthRepository.kt (Interface)
│   │   ├── data/
│   │   │   ├── AuthRepositoryImpl.kt
│   │   │   └── local/ (UserEntity.kt)
│   │   └── presentation/
│   │       ├── LoginViewModel.kt
│   │       ├── LoginScreen.kt
│   │       └── LoginContract.kt (State/Event/Effect)
│   └── inventory/
│       ├── domain/
│       ├── data/
│       └── presentation/
└── App.kt
```
