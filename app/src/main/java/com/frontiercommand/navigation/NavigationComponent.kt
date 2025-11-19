package com.frontiercommand.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

/**
 * NavigationComponent - Root composable that sets up the navigation system
 *
 * This is the top-level navigation composable that should be called from MainActivity.
 * It creates the NavController and initializes the navigation graph.
 *
 * **Usage in MainActivity:**
 * ```kotlin
 * setContent {
 *     PioneerTheme {
 *         NavigationComponent()
 *     }
 * }
 * ```
 *
 * **Responsibilities:**
 * - Creates and manages the NavController instance
 * - Initializes the NavGraph with all routes
 * - Provides navigation context to all child composables
 *
 * **Deep Linking Support:**
 * Future phases will add deep link handling here to support:
 * - Direct navigation to specific camps
 * - External URL handling
 * - Notification taps
 *
 * @param modifier Optional modifier for customization
 *
 * @see NavGraph for route definitions
 * @see rememberNavController for NavController lifecycle
 */
@Composable
fun NavigationComponent(modifier: Modifier = Modifier) {
    // Create NavController - survives recompositions
    // rememberNavController() caches the controller across recompositions
    val navController = rememberNavController()

    // Set up the navigation graph with all routes
    NavGraph(
        navController = navController,
        modifier = modifier
    )
}
