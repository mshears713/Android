# Claude Development Guide - Frontier Command Center

## Project Overview

**Frontier Command Center** is a Pioneer-themed Android educational application that teaches Android development through 10 progressive "Camps" (modules). Each camp teaches a specific Android concept while providing a functional command console for managing a simulated Raspberry Pi ecosystem.

**Target Audience:** Entry-level Android developers
**Estimated Implementation Time:** 1.5-3 hours (AI autonomous execution)
**Architecture:** MVVM with Jetpack Compose

## Technology Stack Summary

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Kotlin | 1.7+ |
| UI Framework | Jetpack Compose | 1.3+ |
| Navigation | Navigation Compose | 2.5+ |
| Architecture | MVVM | - |
| Async | Kotlin Coroutines | 1.6+ |
| Serialization | kotlinx.serialization | Latest |
| Min SDK | Android 21 | API 21 |
| Target SDK | Android 33+ | API 33+ |
| Build System | Gradle (Kotlin DSL) | - |

## Project Structure

```
app/
 ├── src/main/
 │   ├── java/com/frontiercommand/
 │   │   ├── model/              # Data classes (Camp, Command, LogEntry)
 │   │   ├── viewmodel/          # ViewModels (CampViewModel, SettingsViewModel)
 │   │   ├── view/               # Compose UI screens
 │   │   │   ├── camps/          # Individual camp screens (Camp1-10)
 │   │   │   ├── HomeScreen.kt
 │   │   │   ├── CampDetailScreen.kt
 │   │   │   └── CommandConsole.kt
 │   │   ├── navigation/         # NavGraph and route definitions
 │   │   ├── repository/         # Data layer
 │   │   │   ├── NetworkClient.kt
 │   │   │   ├── WebSocketClient.kt
 │   │   │   ├── SensorManager.kt
 │   │   │   ├── StorageManager.kt
 │   │   │   └── LogManager.kt
 │   │   ├── ui/theme/           # Pioneer theme (colors, typography)
 │   │   └── utils/              # Utilities
 │   ├── assets/                 # Diagrams, JSON demo data
 │   ├── res/                    # Resources
 │   └── AndroidManifest.xml
 └── test/                       # Unit and UI tests
```

## The 10 Camps (Educational Modules)

1. **Camp 1: REST API Basics** - HTTP GET/POST operations with placeholder client
2. **Camp 2: WebSocket Fundamentals** - Real-time communication simulation
3. **Camp 3: GPS Integration** - Location services and permissions
4. **Camp 4: Offline Data Caching** - JSON file storage and retrieval
5. **Camp 5: State Management** - Advanced StateFlow patterns
6. **Camp 6: Advanced Navigation** - Deep linking and complex nav patterns
7. **Camp 7: Data Persistence** - Extended caching strategies
8. **Camp 8: Background Processing** - WorkManager integration
9. **Camp 9: System Integration** - Notifications and system services
10. **Camp 10: Deployment** - Build variants, release preparation

## Implementation Phases

### Phase 1: Foundations & Project Setup (Steps 1-10)
**Goal:** Runnable Android app with navigation, theming, and basic UI

- Project initialization with Compose
- Gradle dependencies configuration
- MVVM package structure
- Data models (Camp, Command)
- Navigation system with NavHost
- Home screen with camp list
- Basic ViewModel
- Pioneer-themed UI
- Camp detail screen
- Complete navigation flow

### Phase 2: Core Features & Camp Modules (Steps 11-20)
**Goal:** Networking, sensors, storage, and first 4 camps

- Placeholder HTTP client (NetworkClient)
- Command Console UI component
- CommandConsole integration
- Placeholder WebSocket client
- Camp 1: REST API implementation
- Camp 2: WebSocket implementation
- GPS Sensor Manager with permissions
- Camp 3: GPS integration
- JSON StorageManager
- Camp 4: Offline caching

### Phase 3: Advanced Features (Steps 21-35)
**Goal:** Remaining camps, advanced state management, logging

- Camp 5-7 implementations
- Enhanced state management
- Centralized LogManager
- Advanced navigation patterns
- Settings screen
- Help/documentation screens
- Camp 8-9 implementations

### Phase 4: Polish & Testing (Steps 36-45)
**Goal:** Comprehensive testing, refinement, documentation

- Unit tests for all repositories
- UI tests for navigation and screens
- Integration tests
- Error handling refinement
- Performance optimization
- Accessibility improvements
- In-app tutorials and diagrams
- Camp 10 implementation

### Phase 5: Deployment Preparation (Steps 46-50)
**Goal:** Production-ready app with documentation

- Splash screen
- App icons and branding
- Build configurations
- Release APK generation
- Final documentation
- Project completion verification

## Key Development Principles

### Code Quality
- **No Placeholders:** Every component must be fully functional
- **Comprehensive Comments:** KDoc for all public APIs, inline comments for complex logic
- **Error Handling:** Try-catch blocks with logging for all I/O and async operations
- **Null Safety:** Leverage Kotlin's null safety features
- **Immutability:** Prefer `val` over `var`, use immutable data classes

### Architecture Patterns
- **MVVM Separation:** Strict separation between View, ViewModel, and Model
- **Reactive UI:** All UI state via StateFlow/Flow
- **Repository Pattern:** Abstract data sources behind repository interfaces
- **Dependency Injection:** Manual or Hilt-based injection
- **Lifecycle Awareness:** Proper coroutine scope management

### Testing Strategy
- **Unit Tests:** All business logic, repositories, utilities
- **UI Tests:** Navigation flows, screen interactions
- **Integration Tests:** End-to-end user scenarios
- **Edge Cases:** Permission denials, network failures, invalid inputs

### User Experience
- **Pioneer Theme:** Consistent earthy tones (browns, greens, yellows)
- **Accessibility:** Content descriptions, proper contrast
- **Responsive:** Graceful handling of errors and loading states
- **Educational:** Clear tutorials and explanations in each camp

## Critical Implementation Notes

### Networking (Placeholder Implementation)
- No real HTTP library needed - simulate with delays and hardcoded responses
- NetworkClient returns JSON strings for known endpoints
- WebSocketClient simulates async messaging with coroutine flows
- All network operations use `Dispatchers.IO`

### GPS Integration
- Use `FusedLocationProviderClient` from Google Play Services
- Request `ACCESS_FINE_LOCATION` permission at runtime
- Handle permission denied, GPS disabled scenarios
- Expose location as `StateFlow<Location?>`

### Data Persistence
- Use Android internal storage for JSON files
- Leverage `kotlinx.serialization` for JSON handling
- All file I/O on `Dispatchers.IO`
- Robust error handling for IOException

### State Management
- ViewModel exposes `StateFlow<T>` for all UI state
- Use `MutableStateFlow` internally, expose as `StateFlow`
- Update state in viewModelScope coroutines
- Collect state in Composables with lifecycle awareness

### Theming
```kotlin
// Pioneer Color Palette
val PioneerBrown = Color(0xFF8B4513)
val PioneerGreen = Color(0xFF556B2F)
val PioneerYellow = Color(0xFFDAA520)
val PioneerBeige = Color(0xFFF5DEB3)
val PioneerDarkBrown = Color(0xFF654321)
```

## Development Workflow

### For Each Step:
1. **Read the step specification** from README.md
2. **Understand dependencies** - ensure previous steps are complete
3. **Implement fully** - no stubs or TODOs
4. **Add comprehensive comments** - explain the "why" not just the "what"
5. **Handle errors** - try-catch, logging, user-friendly messages
6. **Test manually** - run and verify functionality
7. **Write automated tests** - unit/UI tests as specified
8. **Verify acceptance criteria** - ensure all criteria met
9. **Commit work** - clear commit message describing what was built

### When Stuck:
- Re-read the step specification and dependencies
- Check the architecture overview for design patterns
- Review Kotlin/Compose best practices
- Ensure all required dependencies are in build.gradle
- Verify package structure matches specification

## Common Patterns & Code Snippets

### ViewModel Pattern
```kotlin
class CampViewModel(application: Application) : AndroidViewModel(application) {
    private val _camps = MutableStateFlow<List<Camp>>(emptyList())
    val camps: StateFlow<List<Camp>> = _camps.asStateFlow()

    init {
        loadCamps()
    }

    private fun loadCamps() {
        viewModelScope.launch {
            try {
                // Load camps logic
                _camps.value = loadedCamps
            } catch (e: Exception) {
                Log.e("CampViewModel", "Error loading camps", e)
            }
        }
    }
}
```

### Composable Screen Pattern
```kotlin
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: CampViewModel = viewModel()
) {
    val camps by viewModel.camps.collectAsState()

    Scaffold(
        topBar = { /* TopAppBar */ }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(camps) { camp ->
                CampListItem(
                    camp = camp,
                    onClick = { navController.navigate("camp_detail/${camp.id}") }
                )
            }
        }
    }
}
```

### Repository Pattern
```kotlin
class StorageManager(private val context: Context) {
    suspend fun saveJson(filename: String, data: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                output.write(data.toByteArray())
            }
            true
        } catch (e: IOException) {
            Log.e("StorageManager", "Error saving JSON", e)
            false
        }
    }
}
```

## Testing Checklist

### Navigation Tests
- [ ] Home screen loads with camp list
- [ ] Clicking camp navigates to detail screen
- [ ] Back button returns to home
- [ ] Invalid camp ID shows error
- [ ] Deep links work correctly

### Feature Tests
- [ ] CommandConsole accepts input and shows output
- [ ] NetworkClient simulates HTTP calls
- [ ] WebSocketClient manages connection lifecycle
- [ ] GPS permissions requested and handled
- [ ] Location updates stream to UI
- [ ] StorageManager saves and loads JSON
- [ ] LogManager captures events

### UI Tests
- [ ] Theme applied consistently
- [ ] All camps render correctly
- [ ] Settings toggles work
- [ ] Help screens accessible
- [ ] Accessibility labels present
- [ ] Error states display properly

### Edge Case Tests
- [ ] Empty camp list handled
- [ ] Permission permanently denied
- [ ] GPS disabled
- [ ] File I/O errors caught
- [ ] Network timeouts simulated
- [ ] Invalid user inputs rejected

## Progress Tracking

Use this section to track completion:

### Phase 1: Foundations ✓/✗
- [ ] Step 1: Initialize project
- [ ] Step 2: Setup dependencies
- [ ] Step 3: Create MVVM packages
- [ ] Step 4: Define data models
- [ ] Step 5: Implement navigation
- [ ] Step 6: Build home screen
- [ ] Step 7: Implement CampViewModel
- [ ] Step 8: Design Pioneer theme
- [ ] Step 9: Create camp detail screen
- [ ] Step 10: Complete navigation flow

### Phase 2: Core Features ✓/✗
- [ ] Step 11: HTTP placeholder client
- [ ] Step 12: CommandConsole UI
- [ ] Step 13: Integrate CommandConsole
- [ ] Step 14: WebSocket placeholder
- [ ] Step 15: Camp 1 - REST API
- [ ] Step 16: Camp 2 - WebSocket
- [ ] Step 17: GPS Sensor Manager
- [ ] Step 18: Camp 3 - GPS
- [ ] Step 19: JSON StorageManager
- [ ] Step 20: Camp 4 - Offline caching

### Phase 3-5: Advanced, Polish, Deployment
(Continue tracking remaining 30 steps)

## Success Criteria

- ✅ All 50 steps completed sequentially
- ✅ 10 fully functional camp modules
- ✅ Reactive Compose UI with consistent theming
- ✅ Placeholder networking for REST and WebSocket
- ✅ GPS integration with permission handling
- ✅ JSON storage for caching and logs
- ✅ Centralized logging system
- ✅ Comprehensive automated tests passing
- ✅ No TODO/stub code remaining
- ✅ APK builds and runs on emulator
- ✅ Complete documentation

## Quick Reference

**Build Commands:**
```bash
./gradlew assembleDebug    # Build debug APK
./gradlew test             # Run unit tests
./gradlew connectedAndroidTest  # Run instrumented tests
./gradlew clean            # Clean build
```

**Common Issues:**
- Compose version mismatch → Check kotlin compiler extension version
- Navigation not working → Verify NavHost setup and route definitions
- StateFlow not updating UI → Ensure collectAsState() in Composable
- Permission errors → Check AndroidManifest.xml declarations
- File I/O errors → Verify Dispatchers.IO usage

## Next Steps

1. ✅ Create Android Studio project named `PioneerCamps`
2. ✅ Configure build.gradle with all dependencies
3. ✅ Set up package structure
4. ✅ Begin Phase 1, Step 1

---

**Remember:** This is an autonomous execution project. Every step must be complete, functional, and thoroughly documented before moving to the next. No shortcuts, no placeholders, no compromises on quality.
