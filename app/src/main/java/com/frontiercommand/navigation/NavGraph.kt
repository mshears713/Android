package com.frontiercommand.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.frontiercommand.view.CampDetailScreen
import com.frontiercommand.view.HelpScreen
import com.frontiercommand.view.HomeScreen
import com.frontiercommand.view.SettingsScreen
import com.frontiercommand.view.camps.*

/**
 * NavGraph - Defines the navigation graph for the entire application
 *
 * The navigation graph is a data structure that defines all possible navigation paths
 * and screen transitions in the app. It uses Jetpack Navigation Compose to manage
 * the backstack and screen lifecycle.
 *
 * **Navigation Flow:**
 * 1. NavHost creates a container for all screens
 * 2. Each composable() defines a route and its associated UI
 * 3. navArgument() extracts route parameters type-safely
 * 4. NavController.navigate() triggers transitions
 * 5. Back button pops the backstack automatically
 *
 * **Error Handling:**
 * - Invalid campId values are caught and logged
 * - Navigation to unknown routes shows error screen
 * - Null arguments are handled with defaults or error states
 *
 * @param navController The NavHostController managing navigation state
 * @param modifier Optional modifier for the NavHost container
 *
 * @see Screen for route definitions
 * @see NavHostController for programmatic navigation
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home screen - displays list of all camps
        composable(
            route = Screen.Home.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "frontiercommand://home"
                },
                navDeepLink {
                    uriPattern = "https://frontiercommand.app/"
                }
            )
        ) {
            HomeScreen(
                navController = navController,
                onNavigateToCamp = { campId ->
                    // Validate campId before navigation
                    if (campId in 1..10) {
                        navController.navigate(Screen.CampDetail.createRoute(campId))
                    } else {
                        Log.w("NavGraph", "Attempted to navigate to invalid campId: $campId")
                    }
                }
            )
        }

        // Camp detail screen - displays individual camp content
        composable(
            route = Screen.CampDetail.route,
            arguments = listOf(
                navArgument(Screen.CampDetail.ARG_CAMP_ID) {
                    type = NavType.IntType
                    defaultValue = 1 // Fallback to Camp 1 if parsing fails
                }
            ),
            deepLinks = listOf(
                // Deep link: frontiercommand://camp/1
                navDeepLink {
                    uriPattern = "frontiercommand://camp/{${Screen.CampDetail.ARG_CAMP_ID}}"
                },
                // Deep link: https://frontiercommand.app/camp/1
                navDeepLink {
                    uriPattern = "https://frontiercommand.app/camp/{${Screen.CampDetail.ARG_CAMP_ID}}"
                }
            )
        ) { backStackEntry ->
            // Extract campId argument from route
            val campId = try {
                backStackEntry.arguments?.getInt(Screen.CampDetail.ARG_CAMP_ID) ?: 1
            } catch (e: Exception) {
                Log.e("NavGraph", "Error parsing campId argument", e)
                1 // Fallback to Camp 1
            }

            // Validate campId is in valid range
            if (campId !in 1..10) {
                Log.w("NavGraph", "Invalid campId: $campId, defaulting to 1")
            }

            // Route to specific camp implementation based on campId
            when (campId.coerceIn(1, 10)) {
                1 -> Camp1RestBasics()
                2 -> Camp2WebSocket()
                3 -> Camp3GpsIntegration()
                4 -> Camp4OfflineCaching()
                5 -> Camp5StateManagement()
                6 -> Camp6AdvancedNavigation()
                7 -> Camp7DataPersistence()
                8 -> Camp8BackgroundProcessing()
                9 -> Camp9SystemIntegration()
                10 -> Camp10Deployment()
                else -> CampDetailScreen(
                    campId = campId.coerceIn(1, 10),
                    navController = navController,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Settings screen - application configuration
        composable(
            route = Screen.Settings.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "frontiercommand://settings"
                },
                navDeepLink {
                    uriPattern = "https://frontiercommand.app/settings"
                }
            )
        ) {
            SettingsScreen(navController = navController)
        }

        // Help screen - tutorials and documentation
        composable(
            route = Screen.Help.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "frontiercommand://help"
                },
                navDeepLink {
                    uriPattern = "https://frontiercommand.app/help"
                }
            )
        ) {
            HelpScreen(navController = navController)
        }
    }
}
