package com.frontiercommand.viewmodel

import android.app.Application
import com.frontiercommand.model.Camp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * CampViewModel - Manages state and business logic for camps
 *
 * This ViewModel provides the data source for the camp list and detail screens.
 * It maintains a reactive list of all 10 camps and exposes methods for
 * updating camp completion status.
 *
 * **State Management:**
 * - Uses StateFlow for reactive UI updates
 * - MutableStateFlow internally for state mutations
 * - StateFlow externally for read-only observation
 * - viewModelScope ensures lifecycle-aware coroutine management
 *
 * **The 10 Camps:**
 * 1. REST API Basics - HTTP GET/POST operations
 * 2. WebSocket Fundamentals - Real-time bidirectional communication
 * 3. GPS Integration - Location services and permissions
 * 4. Offline Data Caching - JSON file storage
 * 5. State Management - Advanced StateFlow patterns
 * 6. Advanced Navigation - Deep linking
 * 7. Data Persistence - Extended caching strategies
 * 8. Background Processing - WorkManager
 * 9. System Integration - Notifications
 * 10. Deployment - Build variants and release
 *
 * **Data Flow:**
 * ```
 * ViewModel._camps (MutableStateFlow) → camps (StateFlow) → UI.collectAsState()
 * ```
 *
 * @param application Application instance for context access
 *
 * @see Camp for data model
 * @see HomeScreen for UI consumer
 * @see CampDetailScreen for detail view
 */
class CampViewModel(application: Application) : BaseViewModel(application) {

    /**
     * Internal mutable state for camps list
     * Modified only within ViewModel
     */
    private val _camps = MutableStateFlow<List<Camp>>(emptyList())

    /**
     * Public read-only state for camps list
     * Collected by UI for reactive updates
     */
    val camps: StateFlow<List<Camp>> = _camps.asStateFlow()

    /**
     * Internal mutable state for selected camp (detail screen)
     */
    private val _selectedCamp = MutableStateFlow<Camp?>(null)

    /**
     * Public read-only state for selected camp
     */
    val selectedCamp: StateFlow<Camp?> = _selectedCamp.asStateFlow()

    init {
        logInfo("Initializing CampViewModel")
        loadCamps()
    }

    /**
     * Loads the initial list of 10 camps
     * Called during ViewModel initialization
     *
     * In future phases, this could load from:
     * - Local JSON file (StorageManager)
     * - Remote API (NetworkClient)
     * - Database (Room)
     */
    private fun loadCamps() {
        launchSafe {
            try {
                logDebug("Loading camps data")

                // Create the 10 educational camps with complete details
                val campList = listOf(
                    Camp(
                        id = 1,
                        title = "Camp 1: REST API Basics",
                        description = "Learn HTTP networking fundamentals with GET and POST requests. Interact with placeholder APIs and understand request-response patterns.",
                        module = "Networking",
                        isCompleted = false
                    ),
                    Camp(
                        id = 2,
                        title = "Camp 2: WebSocket Fundamentals",
                        description = "Explore real-time bidirectional communication using WebSocket connections. Send and receive messages asynchronously.",
                        module = "Real-time Communication",
                        isCompleted = false
                    ),
                    Camp(
                        id = 3,
                        title = "Camp 3: GPS Integration",
                        description = "Access device location services with runtime permissions. Display latitude, longitude, and accuracy in real-time.",
                        module = "Sensors & Permissions",
                        isCompleted = false
                    ),
                    Camp(
                        id = 4,
                        title = "Camp 4: Offline Data Caching",
                        description = "Persist data locally using JSON file storage. Implement caching strategies for offline-first applications.",
                        module = "Data Persistence",
                        isCompleted = false
                    ),
                    Camp(
                        id = 5,
                        title = "Camp 5: State Management",
                        description = "Master advanced StateFlow patterns for reactive UI. Learn state hoisting and unidirectional data flow.",
                        module = "Architecture",
                        isCompleted = false
                    ),
                    Camp(
                        id = 6,
                        title = "Camp 6: Advanced Navigation",
                        description = "Implement deep linking and complex navigation patterns. Handle navigation arguments and backstack management.",
                        module = "Navigation",
                        isCompleted = false
                    ),
                    Camp(
                        id = 7,
                        title = "Camp 7: Data Persistence",
                        description = "Explore extended caching strategies and data synchronization. Build robust offline-capable features.",
                        module = "Storage",
                        isCompleted = false
                    ),
                    Camp(
                        id = 8,
                        title = "Camp 8: Background Processing",
                        description = "Schedule background tasks with WorkManager. Learn about Android's background execution limits.",
                        module = "Background Work",
                        isCompleted = false
                    ),
                    Camp(
                        id = 9,
                        title = "Camp 9: System Integration",
                        description = "Integrate with Android system services. Display notifications and interact with system features.",
                        module = "System Services",
                        isCompleted = false
                    ),
                    Camp(
                        id = 10,
                        title = "Camp 10: Deployment",
                        description = "Prepare your app for release. Configure build variants, signing, and optimize for production.",
                        module = "Release Management",
                        isCompleted = false
                    )
                )

                _camps.value = campList
                logInfo("Successfully loaded ${campList.size} camps")
            } catch (e: Exception) {
                logError("Error loading camps", e)
                // Set empty list on error - UI can show error state
                _camps.value = emptyList()
            }
        }
    }

    /**
     * Selects a camp by ID for the detail screen
     *
     * @param campId The ID of the camp to select (1-10)
     */
    fun selectCamp(campId: Int) {
        launchSafe {
            try {
                logDebug("Selecting camp with ID: $campId")

                val camp = _camps.value.find { it.id == campId }
                if (camp != null) {
                    _selectedCamp.value = camp
                    logInfo("Selected camp: ${camp.title}")
                } else {
                    logWarning("Camp with ID $campId not found")
                    _selectedCamp.value = null
                }
            } catch (e: Exception) {
                logError("Error selecting camp", e)
                _selectedCamp.value = null
            }
        }
    }

    /**
     * Marks a camp as completed
     *
     * @param campId The ID of the camp to complete
     */
    fun completeCamp(campId: Int) {
        launchSafe {
            try {
                logDebug("Completing camp with ID: $campId")

                val updatedCamps = _camps.value.map { camp ->
                    if (camp.id == campId) {
                        camp.complete().also {
                            logInfo("Marked camp as completed: ${camp.title}")
                        }
                    } else {
                        camp
                    }
                }

                _camps.value = updatedCamps

                // Also update selected camp if it's the one being completed
                if (_selectedCamp.value?.id == campId) {
                    _selectedCamp.value = _selectedCamp.value?.complete()
                }
            } catch (e: Exception) {
                logError("Error completing camp", e)
            }
        }
    }

    /**
     * Resets a camp to incomplete status
     *
     * @param campId The ID of the camp to reset
     */
    fun resetCamp(campId: Int) {
        launchSafe {
            try {
                logDebug("Resetting camp with ID: $campId")

                val updatedCamps = _camps.value.map { camp ->
                    if (camp.id == campId) {
                        camp.reset().also {
                            logInfo("Reset camp: ${camp.title}")
                        }
                    } else {
                        camp
                    }
                }

                _camps.value = updatedCamps

                // Also update selected camp if it's the one being reset
                if (_selectedCamp.value?.id == campId) {
                    _selectedCamp.value = _selectedCamp.value?.reset()
                }
            } catch (e: Exception) {
                logError("Error resetting camp", e)
            }
        }
    }

    /**
     * Resets all camps to incomplete status
     * Useful for testing and resetting progress
     */
    fun resetAllCamps() {
        launchSafe {
            try {
                logInfo("Resetting all camps")

                val updatedCamps = _camps.value.map { it.reset() }
                _camps.value = updatedCamps

                // Also reset selected camp
                _selectedCamp.value = _selectedCamp.value?.reset()
            } catch (e: Exception) {
                logError("Error resetting all camps", e)
            }
        }
    }

    /**
     * Gets a camp by ID (synchronous)
     * Returns null if camp not found
     *
     * @param campId The camp ID to find
     * @return The camp, or null if not found
     */
    fun getCampById(campId: Int): Camp? {
        return _camps.value.find { it.id == campId }
    }
}
