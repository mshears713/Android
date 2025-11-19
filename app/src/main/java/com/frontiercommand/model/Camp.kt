package com.frontiercommand.model

import kotlinx.serialization.Serializable

/**
 * Camp - Represents an educational camp/module in the Frontier Command Center
 *
 * Each Camp is a self-contained learning module teaching a specific Android development concept.
 * The Pioneer theme presents camps as "stops" along a westward journey, combining functional
 * features with educational tutorials.
 *
 * **The 10 Camps:**
 * 1. REST API Basics - HTTP networking fundamentals
 * 2. WebSocket Fundamentals - Real-time bidirectional communication
 * 3. GPS Integration - Location services and permissions
 * 4. Offline Data Caching - JSON file persistence
 * 5. State Management - Advanced StateFlow patterns
 * 6. Advanced Navigation - Deep linking and complex nav
 * 7. Data Persistence - Extended caching strategies
 * 8. Background Processing - WorkManager integration
 * 9. System Integration - Notifications and system services
 * 10. Deployment - Build variants and release preparation
 *
 * @property id Unique identifier for the camp (1-10)
 * @property title Display name of the camp (e.g., "Camp 1: REST API Basics")
 * @property description Short description of what the camp teaches
 * @property module The Android concept being taught (e.g., "Networking", "GPS", "Storage")
 * @property isCompleted Whether the user has completed this camp's activities
 */
@Serializable
data class Camp(
    val id: Int,
    val title: String,
    val description: String,
    val module: String,
    val isCompleted: Boolean = false
) {
    /**
     * Validates that the camp data is well-formed
     *
     * @return true if the camp is valid, false otherwise
     */
    fun isValid(): Boolean {
        return id in 1..10 &&
                title.isNotBlank() &&
                description.isNotBlank() &&
                module.isNotBlank()
    }

    /**
     * Returns a copy of this camp marked as completed
     *
     * @return New Camp instance with isCompleted = true
     */
    fun complete(): Camp {
        return copy(isCompleted = true)
    }

    /**
     * Returns a copy of this camp marked as incomplete
     *
     * @return New Camp instance with isCompleted = false
     */
    fun reset(): Camp {
        return copy(isCompleted = false)
    }

    companion object {
        /**
         * Creates a mock/placeholder camp for testing and development
         *
         * @param id The camp ID
         * @return A placeholder Camp instance
         */
        fun createPlaceholder(id: Int): Camp {
            return Camp(
                id = id,
                title = "Camp $id: Module Title",
                description = "Learn about Android development concept $id",
                module = "Module$id",
                isCompleted = false
            )
        }
    }
}
