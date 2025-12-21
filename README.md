# Water Buddy 💧

Water Buddy is a Kotlin Multiplatform application designed to help users track their daily water intake and stay hydrated. Built with modern Android
and KMP practices.

## Features

- **Water Tracking**: Easily log your daily water intake.
- **Hydration Insights**: Visualize your progress with weekly charts and monthly heatmaps.
- **Daily Goals**: Set and monitor your hydration targets.
- **Cross-Platform**: Available on Android and iOS using Compose Multiplatform.

## Tech Stack

- **[Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)**: Shared business logic and UI across Android
  and iOS.
- **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)**: Declarative UI for all platforms.
- **[Room](https://developer.android.com/kotlin/multiplatform/room)**: Multiplatform local database for data persistence.
- **[Metro](https://github.com/nacular/metro)**: Dependency injection.
- **[Kover](https://github.com/Kotlin/kotlinx-kover)**: Code coverage reporting and verification (Strict 100% coverage target).
- **[Spotless](https://github.com/diffplug/spotless)**: Code formatting with KtLint.
- **[Kotest](https://kotest.io/) & [Turbine](https://github.com/cashapp/turbine)**: Modern testing and Flow verification.

## Project Structure

* `/composeApp`: Shared code for Android and iOS.
    - `commonMain`: Shared logic, UI (Material 3), and database.
    - `androidMain`: Android-specific implementations and configurations.
    - `iosMain`: iOS-specific implementations.
* `/iosApp`: Native iOS entry point.

## Development

### Code Style

The project uses **Spotless** with **KtLint**. To format the code, run:

```shell
./gradlew spotlessApply
```

### Code Coverage

We aim for high quality with **Kover**. To generate reports:

```shell
./gradlew koverReport
```

Reports are generated in `composeApp/build/reports/kover/html/index.html`.

### Build & Run

#### Android

```shell
./gradlew :composeApp:assembleDebug
```

#### iOS

Open `/iosApp` in Xcode or use the run configuration in Android Studio.

#### JVM (Desktop)

```shell
./gradlew :composeApp:run
```
