# Navigation Package

## Responsibility

This package contains **navigation infrastructure** for screen transitions.

## Contents

- **NavGraph.kt** - Defines navigation graph with all routes
- **Screen.kt** - Sealed class representing all possible destinations
- **NavigationComponent.kt** - Root navigation composable

## Architecture Role

Navigation manages:
- **Screen routing** - Define destinations and arguments
- **Backstack** - Handle back navigation
- **Deep linking** - Support external navigation
- **Type safety** - Compile-time route checking

## Usage

Navigation uses:
- **NavController** - Programmatic navigation
- **NavHost** - Container for routed composables
- **Routes** - String-based or type-safe routes
- **Arguments** - Pass data between screens

