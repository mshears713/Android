package com.frontiercommand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.frontiercommand.navigation.NavigationComponent
import com.frontiercommand.ui.theme.PioneerTheme

/**
 * MainActivity - Entry point for the Frontier Command Center Android application
 *
 * This activity serves as the container for the entire Compose UI hierarchy.
 * ComponentActivity is the base class required for Jetpack Compose integration,
 * providing lifecycle management and composition hosting.
 *
 * **Architecture:**
 * - ComponentActivity handles Android lifecycle events
 * - NavigationComponent sets up the navigation graph
 * - Compose UI renders declaratively based on state
 * - MVVM pattern separates UI from business logic
 *
 * **Lifecycle Flow:**
 * ```
 * onCreate() → setContent() → NavigationComponent() → NavGraph() → HomeScreen()
 * ```
 *
 * The app uses Jetpack Navigation Compose for screen transitions and state management.
 * All screens are defined in the navigation graph and managed by NavController.
 *
 * @see ComponentActivity for lifecycle details
 * @see NavigationComponent for navigation setup
 * @see NavGraph for route definitions
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting.
     * This is where we initialize the Compose UI tree.
     *
     * @param savedInstanceState Previously saved state (if any)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Jetpack Compose UI with navigation
        // setContent establishes the root of the Compose hierarchy
        setContent {
            // Apply Pioneer theme with frontier-inspired colors and typography
            PioneerTheme {
                // Surface provides the background color from theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Initialize navigation system
                    // NavigationComponent handles all screen routing
                    NavigationComponent()
                }
            }
        }
    }
}
