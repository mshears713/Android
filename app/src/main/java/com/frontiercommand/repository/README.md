# Repository Package

## Responsibility

This package contains **data layer abstractions** for networking, storage, sensors, and logging.

## Contents

- **NetworkClient.kt** - Placeholder HTTP client for REST APIs
- **WebSocketClient.kt** - Placeholder WebSocket client for real-time messaging
- **SensorManager.kt** - GPS and location services wrapper
- **StorageManager.kt** - JSON file persistence for caching and logs
- **LogManager.kt** - Centralized logging system

## Architecture Role

Repositories:
- **Abstract data sources** - Hide implementation details
- **Provide clean APIs** - Expose suspend functions and Flows
- **Handle errors** - Catch and log exceptions
- **Support testing** - Can be mocked or replaced

## Usage

Repositories are:
- **Called from ViewModels** - Never directly from Views
- **Asynchronous** - Use coroutines (Dispatchers.IO)
- **Singleton or injected** - Single instance per app
- **Thread-safe** - Handle concurrent access properly

