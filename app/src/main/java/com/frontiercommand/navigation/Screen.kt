package com.frontiercommand.navigation

/**
 * Screen - Sealed class representing all possible navigation destinations
 *
 * This sealed class provides type-safe navigation routes for the entire application.
 * Each destination is a separate object or class, allowing for compile-time verification
 * of navigation paths and arguments.
 *
 * **Navigation Architecture:**
 * - Sealed class ensures exhaustive when expressions
 * - Each screen defines its own route string
 * - Parameterized routes include argument placeholders
 * - Helper methods construct routes with actual values
 *
 * **Screen Hierarchy:**
 * ```
 * Home (Camp List)
 *   ├─→ CampDetail(1-10)
 *   │     └─→ CommandConsole (embedded)
 *   ├─→ Settings
 *   └─→ Help
 * ```
 *
 * @see NavGraph for route definitions and composable assignments
 */
sealed class Screen(val route: String) {

    /**
     * Home - Main screen displaying the list of all 10 camps
     * No parameters required
     */
    object Home : Screen("home")

    /**
     * CampDetail - Detail screen for a specific camp
     * Requires campId parameter to identify which camp to display
     *
     * **Route format:** `camp_detail/{campId}`
     * **Arguments:** campId (Int) - The camp ID from 1 to 10
     *
     * @property route Base route with parameter placeholder
     */
    object CampDetail : Screen("camp_detail/{campId}") {
        /**
         * Constructs the complete route string with actual campId value
         *
         * @param campId The ID of the camp to navigate to
         * @return Complete route string (e.g., "camp_detail/3")
         */
        fun createRoute(campId: Int): String {
            return "camp_detail/$campId"
        }

        /**
         * Argument key for extracting campId from NavBackStackEntry
         */
        const val ARG_CAMP_ID = "campId"
    }

    /**
     * Settings - Application settings screen
     * Displays logs, theme preferences, and other configuration options
     */
    object Settings : Screen("settings")

    /**
     * Help - Help and documentation screen
     * Shows tutorials, diagrams, and user guides
     */
    object Help : Screen("help")

    companion object {
        /**
         * Returns a list of all available screens
         * Useful for debugging and validation
         *
         * @return List of all Screen instances
         */
        fun getAllScreens(): List<Screen> {
            return listOf(Home, CampDetail, Settings, Help)
        }
    }
}
