# Model Package

## Responsibility

This package contains all **data models** (domain entities) for the Frontier Command Center application.

## Contents

- **Camp.kt** - Represents an educational camp/module with metadata
- **Command.kt** - Represents a user command with text and response
- **LogEntry.kt** - Represents a log entry for system events

## Architecture Role

In the MVVM pattern, the Model layer represents:
- **Data structures** - Immutable Kotlin data classes
- **Business logic** - Validation methods and domain rules
- **Serialization** - Annotated with kotlinx.serialization for JSON persistence

Models are:
- **Immutable** - Use `val` properties to prevent accidental modification
- **Serializable** - Annotated with `@Serializable` for JSON conversion
- **Self-validating** - Include validation methods where appropriate
- **Well-documented** - KDoc comments explain purpose and usage

## Usage

Models are consumed by:
- **ViewModels** - Manage and transform model data for UI
- **Repositories** - Load, store, and network operations
- **Views** - Display model data in Composables (read-only)

Models should never directly reference View or ViewModel classes to maintain separation of concerns.
