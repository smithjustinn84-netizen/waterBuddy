# Water Tracker - KMP Demo Project

## Overview
A modern water intake habit tracker built with Kotlin Multiplatform (KMP), following Clean Architecture principles and using Metro DI framework.

## Architecture

### Tech Stack
- **Kotlin Multiplatform (KMP)**: Shared code for Android & iOS
- **Compose Multiplatform**: UI framework
- **Metro DI**: Dependency injection (v0.9.2)
- **Kotlinx DateTime**: Date and time handling
- **Kotlinx Coroutines**: Asynchronous programming
- **StateFlow/SharedFlow**: Reactive state management

### Project Structure

```
composeApp/src/commonMain/kotlin/com/example/demometro/
├── di/                          # Dependency Injection
│   ├── AppScope.kt             # DI Scope definition
│   └── AppComponent.kt         # Main dependency graph
│
├── domain/                      # Business Logic Layer
│   ├── model/
│   │   ├── WaterIntake.kt      # Water entry model
│   │   └── DailyWaterStats.kt  # Statistics model
│   ├── repository/
│   │   └── WaterRepository.kt  # Repository interface
│   └── usecase/
│       ├── ObserveDailyWaterStatsUseCase.kt
│       ├── AddWaterIntakeUseCase.kt
│       ├── DeleteWaterIntakeUseCase.kt
│       └── UpdateDailyGoalUseCase.kt
│
├── data/                        # Data Layer
│   └── repository/
│       └── WaterRepositoryImpl.kt  # Repository implementation (in-memory)
│
└── presentation/                # UI Layer
    └── water/
        ├── WaterTrackerContract.kt    # UI State/Intent/Event
        ├── WaterTrackerViewModel.kt   # ViewModel
        └── WaterTrackerScreen.kt      # Composable UI
```

## Features

### Water Tracker
- ✅ Track daily water intake with quick-add buttons (250ml, 500ml, 750ml)
- ✅ Visual progress indicator showing percentage of daily goal
- ✅ Real-time statistics (consumed, remaining, goal)
- ✅ List of today's water entries with timestamps
- ✅ Delete individual entries
- ✅ Customize daily goal
- ✅ Goal celebration when target is reached

### Design Patterns

#### MVVM + UDF (Unidirectional Data Flow)
```kotlin
// State: Single source of truth
StateFlow<WaterTrackerUiState>

// Intents: User actions
sealed interface WaterTrackerUiIntent {
    data class AddWater(val amountMl: Int)
    data class DeleteEntry(val id: String)
    data class UpdateGoal(val goalMl: Int)
}

// Events: One-time side effects
sealed interface WaterTrackerUiEvent {
    data class ShowSuccess(val message: String)
    data class ShowError(val message: String)
    object GoalReached
}
```

#### Clean Architecture Layers
1. **Presentation Layer**: Compose UI + ViewModels
2. **Domain Layer**: Use Cases + Repository Interfaces + Models
3. **Data Layer**: Repository Implementations + Data Sources

#### Metro Dependency Injection
```kotlin
// Automatic binding with @ContributesBinding
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class WaterRepositoryImpl : WaterRepository

// Use Cases injected via constructor
@Inject
class WaterTrackerViewModel(
    private val observeDailyWaterStatsUseCase: ObserveDailyWaterStatsUseCase,
    private val addWaterIntakeUseCase: AddWaterIntakeUseCase,
    // ...
)

// Graph creation
val component = createGraph<AppComponent>()
```

## How to Build

### Android
```bash
./gradlew composeApp:assembleDebug
```

### iOS
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Build and run

## Metro DI Setup

Metro is configured in the project with:
- Plugin version: `0.9.2`
- Applied in `composeApp/build.gradle.kts`
- Main scope: `AppScope` (object)
- Main graph: `AppComponent` (abstract class with @DependencyGraph)

### Key Metro Features Used
- `@Inject`: Constructor injection
- `@ContributesBinding`: Automatic interface-implementation binding
- `@SingleIn`: Singleton scope
- `createGraph<T>()`: Graph instantiation
- Automatic dependency resolution

## Future Enhancements
- [ ] Persist data with Room database
- [ ] Weekly/monthly statistics and charts
- [ ] Reminders and notifications
- [ ] Multiple drink types (water, coffee, tea)
- [ ] Integration with health/fitness apps
- [ ] Dark mode support
- [ ] Localization

## Credits
Built following KMP best practices and Clean Architecture principles.

