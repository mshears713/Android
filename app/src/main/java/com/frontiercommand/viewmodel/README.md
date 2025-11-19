# ViewModel Package

## Responsibility

This package contains all **ViewModels** for the Frontier Command Center application.

## Contents

- **BaseViewModel.kt** - Abstract base class providing common ViewModel functionality
- **CampViewModel.kt** - Manages state for camp list and detail screens
- **SettingsViewModel.kt** - Manages application settings and preferences
- **CommandViewModel.kt** - Handles command console logic and state

## Architecture Role

In the MVVM pattern, the ViewModel layer:
- **Manages UI state** - Exposes StateFlow/Flow for reactive UI updates
- **Handles business logic** - Coordinates repositories and data transformations
- **Survives configuration changes** - Persists across screen rotations
- **Lifecycle-aware** - Uses viewModelScope for coroutine management

ViewModels should:
- **Never reference Views** - Only expose data via StateFlow/Flow
- **Use coroutines** - All async operations in viewModelScope
- **Handle errors** - Try-catch blocks with logging
- **Be testable** - Pure logic without Android dependencies where possible

## Usage

ViewModels are:
- **Created once** - Injected into Composables via `viewModel()` or Hilt
- **Observed** - UI collects StateFlow with `collectAsState()`
- **Called from UI** - Public methods for user actions
- **Cleaned up** - viewModelScope automatically cancelled on clear

