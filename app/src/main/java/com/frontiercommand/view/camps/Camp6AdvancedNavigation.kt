package com.frontiercommand.view.camps

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontiercommand.repository.LogManager

/**
 * Camp 6: Advanced Navigation & Deep Linking
 *
 * **Educational Goals:**
 * - Understand Android deep linking mechanisms
 * - Implement app links for web-to-app navigation
 * - Handle navigation arguments safely
 * - Manage navigation backstack effectively
 * - Test deep links programmatically
 * - Learn best practices for navigation architecture
 *
 * **Key Concepts Covered:**
 * 1. **Deep Links** - Custom URI schemes (frontiercommand://)
 * 2. **App Links** - Verified HTTPS links (https://frontiercommand.app)
 * 3. **Navigation Arguments** - Type-safe parameter passing
 * 4. **Backstack Management** - popUpTo, launchSingleTop
 * 5. **Intent Filters** - AndroidManifest.xml configuration
 * 6. **Testing Deep Links** - ADB commands and programmatic testing
 *
 * **Deep Link Examples:**
 * - frontiercommand://camp/1 → Opens Camp 1
 * - frontiercommand://settings → Opens Settings
 * - https://frontiercommand.app/camp/2 → Opens Camp 2
 * - frontiercommand://home → Opens Home screen
 *
 * This camp provides hands-on experience with modern Android navigation
 * patterns essential for building apps with external integrations and
 * seamless user experiences across multiple entry points.
 */
@Composable
fun Camp6AdvancedNavigation() {
    val context = LocalContext.current
    val logManager = remember { LogManager.getInstance(context) }

    // Track when camp is opened
    LaunchedEffect(Unit) {
        logManager.info("Camp6", "Advanced Navigation camp opened")
    }

    // Sample deep links to demonstrate
    val deepLinkExamples = remember {
        listOf(
            DeepLinkExample(
                title = "Camp 1 Deep Link",
                uri = "frontiercommand://camp/1",
                description = "Opens Camp 1: REST API Basics"
            ),
            DeepLinkExample(
                title = "Camp 5 Deep Link",
                uri = "frontiercommand://camp/5",
                description = "Opens Camp 5: State Management"
            ),
            DeepLinkExample(
                title = "Home Deep Link",
                uri = "frontiercommand://home",
                description = "Returns to Home screen"
            ),
            DeepLinkExample(
                title = "Settings Deep Link",
                uri = "frontiercommand://settings",
                description = "Opens Settings screen"
            ),
            DeepLinkExample(
                title = "HTTPS App Link",
                uri = "https://frontiercommand.app/camp/3",
                description = "Opens Camp 3 via verified HTTPS link"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Camp Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🏕️ Camp 6: Advanced Navigation",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Master deep linking and navigation patterns",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Tutorial Section
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📚 Deep Linking Fundamentals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **What are Deep Links?**

                        Deep links allow users to navigate directly to specific
                        content within your app from external sources like:
                        • Web browsers
                        • Other apps
                        • Notifications
                        • Email campaigns
                        • QR codes

                        **Types of Deep Links:**

                        1. **Deep Links (Custom Scheme)**
                           - URI: frontiercommand://camp/1
                           - Works offline
                           - Not verified
                           - Can be intercepted by other apps

                        2. **App Links (HTTPS)**
                           - URI: https://frontiercommand.app/camp/1
                           - Verified ownership
                           - Opens directly in your app
                           - Fallback to browser if app not installed

                        **AndroidManifest Configuration:**

                        <intent-filter android:autoVerify="true">
                            <action android:name="android.intent.action.VIEW" />
                            <category android:name="android.intent.category.DEFAULT" />
                            <category android:name="android.intent.category.BROWSABLE" />
                            <data
                                android:scheme="frontiercommand"
                                android:host="camp"
                                android:pathPrefix="/" />
                        </intent-filter>

                        **Navigation Compose Integration:**

                        composable(
                            route = "camp_detail/{campId}",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "frontiercommand://camp/{campId}"
                                }
                            )
                        ) { ... }
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Deep Link Testing Section
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧪 Test Deep Links",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Click any button below to test deep link navigation:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display deep link examples
                    deepLinkExamples.forEach { example ->
                        DeepLinkCard(
                            example = example,
                            onTestClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(example.uri))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                    logManager.info("Camp6", "Tested deep link: ${example.uri}")
                                } catch (e: Exception) {
                                    logManager.error("Camp6", "Failed to test deep link", e)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // ADB Testing Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔧 Testing with ADB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        Test deep links from command line using ADB:

                        # Test custom scheme deep link
                        adb shell am start -W -a android.intent.action.VIEW \
                          -d "frontiercommand://camp/1" \
                          com.frontiercommand

                        # Test HTTPS app link
                        adb shell am start -W -a android.intent.action.VIEW \
                          -d "https://frontiercommand.app/camp/2" \
                          com.frontiercommand

                        # Test settings deep link
                        adb shell am start -W -a android.intent.action.VIEW \
                          -d "frontiercommand://settings" \
                          com.frontiercommand

                        # View app link verification status
                        adb shell dumpsys package domain-preferred-apps

                        # Verify specific package
                        adb shell dumpsys package com.frontiercommand | grep -A 5 "Domain verification"
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        // Navigation Backstack Section
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📚 Navigation Backstack Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        **Backstack Strategies:**

                        1. **Standard Navigation**
                           navController.navigate("camp_detail/1")
                           → Adds to backstack normally

                        2. **Single Top**
                           navController.navigate("camp_detail/1") {
                               launchSingleTop = true
                           }
                           → Reuses existing instance if on top

                        3. **Pop Up To**
                           navController.navigate("home") {
                               popUpTo("home") { inclusive = true }
                           }
                           → Clears backstack up to destination

                        4. **Replace Backstack**
                           navController.navigate("camp_detail/1") {
                               popUpTo(0) // Clear entire backstack
                           }

                        **Deep Link Backstack Behavior:**

                        When opening via deep link, Navigation creates
                        a synthetic backstack:
                        • Deep link destination is top
                        • Start destination is added below
                        • Ensures Back button works correctly
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Best Practices Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✅ Best Practices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        1. **Use Type-Safe Arguments**
                           - Define argument types in navArgument()
                           - Provide default values
                           - Validate arguments before use

                        2. **Handle Deep Link Errors**
                           - Gracefully handle invalid URIs
                           - Show error UI if arguments missing
                           - Log deep link events for debugging

                        3. **Test Thoroughly**
                           - Test all deep link variations
                           - Verify backstack behavior
                           - Test with app in different states

                        4. **Verify App Links**
                           - Upload assetlinks.json to website
                           - Use autoVerify="true" in manifest
                           - Test verification status

                        5. **Document Deep Links**
                           - Maintain list of all deep links
                           - Document required parameters
                           - Share with marketing/support teams

                        6. **Analytics Integration**
                           - Track deep link sources
                           - Measure conversion rates
                           - Monitor error rates
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Key Takeaways
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 Key Takeaways",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                        ✓ Deep links enable direct navigation to app content
                        ✓ Custom schemes (frontiercommand://) work offline
                        ✓ HTTPS app links are verified and more secure
                        ✓ Navigation Compose handles deep links automatically
                        ✓ Synthetic backstack ensures proper Back behavior
                        ✓ Test with both UI buttons and ADB commands
                        ✓ AndroidManifest configuration is crucial

                        **Next Steps:**
                        - Explore Camp 7 for data persistence
                        - Implement deep links in your own apps
                        - Set up App Links verification
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Code Example
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💻 Complete Example",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = """
                            // Screen.kt - Route definition
                            object CampDetail : Screen("camp_detail/{campId}") {
                                fun createRoute(campId: Int) = "camp_detail/${'$'}campId"
                                const val ARG_CAMP_ID = "campId"
                            }

                            // NavGraph.kt - Deep link setup
                            composable(
                                route = CampDetail.route,
                                arguments = listOf(
                                    navArgument(CampDetail.ARG_CAMP_ID) {
                                        type = NavType.IntType
                                        defaultValue = 1
                                    }
                                ),
                                deepLinks = listOf(
                                    navDeepLink {
                                        uriPattern = "frontiercommand://camp/{campId}"
                                    },
                                    navDeepLink {
                                        uriPattern = "https://frontiercommand.app/camp/{campId}"
                                    }
                                )
                            ) { backStackEntry ->
                                val campId = backStackEntry.arguments?.getInt("campId") ?: 1
                                CampDetailScreen(campId = campId)
                            }

                            // Testing programmatically
                            fun testDeepLink(context: Context, uri: String) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                context.startActivity(intent)
                            }
                            """.trimIndent(),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Data class representing a deep link example
 */
data class DeepLinkExample(
    val title: String,
    val uri: String,
    val description: String
)

/**
 * Card displaying a deep link example with test button
 */
@Composable
fun DeepLinkCard(
    example: DeepLinkExample,
    onTestClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = example.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = example.uri,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = example.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onTestClick) {
                    Text("Test")
                }
            }
        }
    }
}
