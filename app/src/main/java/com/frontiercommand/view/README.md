# View Package

## Responsibility

This package contains all **Compose UI screens and components** for the application.

## Contents

- **HomeScreen.kt** - Main screen displaying camp list
- **CampDetailScreen.kt** - Detail screen for individual camps
- **CommandConsole.kt** - Reusable command console UI component
- **camps/** - Subdirectory containing camp-specific screens (Camp1-10)
- **SettingsScreen.kt** - Application settings UI
- **HelpScreen.kt** - Help and documentation UI

## Architecture Role

In the MVVM pattern, the View layer:
- **Renders UI declaratively** - Jetpack Compose functions
- **Observes ViewModel state** - Recomposes on StateFlow changes
- **Captures user input** - Calls ViewModel methods
- **No business logic** - Purely presentational

Views should:
- **Be stateless** - State hoisted to ViewModels
- **Be reusable** - Extract common components
- **Handle loading/error states** - Display appropriate UI for all states
- **Be accessible** - Include content descriptions
- **Be previewed** - Add @Preview annotations

## Usage

Composables:
- **Receive ViewModels** - Via parameter injection
- **Collect state** - Using `collectAsState()`
- **Navigate** - Via NavController parameter
- **Render** - Based on observed state only

