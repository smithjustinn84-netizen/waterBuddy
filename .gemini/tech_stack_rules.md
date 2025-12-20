## 2. Technology Stack Requirements

- Dependency Injection: Use Metro exclusively. All ViewModels and UseCases must be provided via the Metro graph.
- Persistence: Use Room for all local data storage.
- Networking: Use Ktor for API communication.
- UI: Use Jetpack Compose (Multiplatform).
- Navigation: Use Jetpack Compose Navigation with Type Safe operators.

Testing:

- Use `Turbine` for testing Flows (State/Effect).
- Use `Mockkery` for mocking dependencies.
- Use `Robolectric` for Android integration tests.