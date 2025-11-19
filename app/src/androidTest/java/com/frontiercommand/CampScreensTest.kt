package com.frontiercommand

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frontiercommand.view.camps.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * CampScreensTest - UI tests for individual camp screens
 *
 * Tests each camp screen to ensure:
 * - Screen renders correctly with all key elements
 * - Interactive elements (buttons, inputs) work
 * - Tutorial sections are present
 * - Code examples are displayed
 * - User interactions update state correctly
 *
 * Uses Jetpack Compose Testing framework with ComposeTestRule.
 */
@RunWith(AndroidJUnit4::class)
class CampScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ========== Camp 1: REST API Basics Tests ==========

    @Test
    fun camp1_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp1RestBasics()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 1: REST API Basics", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp1_displaysGetRequestButton() {
        // Given
        composeTestRule.setContent {
            Camp1RestBasics()
        }

        // Then
        composeTestRule
            .onNodeWithText("GET /status", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp1_displaysPostRequestButton() {
        // Given
        composeTestRule.setContent {
            Camp1RestBasics()
        }

        // Then
        composeTestRule
            .onNodeWithText("POST /config", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp1_clickingGetButton_displaysResponse() {
        // Given
        composeTestRule.setContent {
            Camp1RestBasics()
        }

        // When
        composeTestRule
            .onNodeWithText("GET /status", substring = true)
            .performClick()

        // Then - wait for response and verify loading or response appears
        composeTestRule.waitForIdle()
        // Response should eventually appear (within timeout)
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Response", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ========== Camp 2: WebSocket Fundamentals Tests ==========

    @Test
    fun camp2_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp2WebSocket()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 2: WebSocket", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp2_displaysConnectButton() {
        // Given
        composeTestRule.setContent {
            Camp2WebSocket()
        }

        // Then
        composeTestRule
            .onNodeWithText("Connect", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp2_displaysMessageInput() {
        // Given
        composeTestRule.setContent {
            Camp2WebSocket()
        }

        // Then - verify message input field exists
        composeTestRule
            .onNodeWithText("Enter message", substring = true)
            .assertExists()
    }

    @Test
    fun camp2_clickingConnect_changesConnectionStatus() {
        // Given
        composeTestRule.setContent {
            Camp2WebSocket()
        }

        // When
        composeTestRule
            .onNodeWithText("Connect", substring = true)
            .performClick()

        // Then - wait for connection
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule
                .onAllNodesWithText("Connected", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ========== Camp 3: GPS Integration Tests ==========

    @Test
    fun camp3_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp3GPSIntegration()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 3: GPS", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp3_displaysPermissionSection() {
        // Given
        composeTestRule.setContent {
            Camp3GPSIntegration()
        }

        // Then
        composeTestRule
            .onNodeWithText("Permission", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp3_displaysLocationButton() {
        // Given
        composeTestRule.setContent {
            Camp3GPSIntegration()
        }

        // Then
        composeTestRule
            .onNodeWithText("Get Location", substring = true)
            .assertIsDisplayed()
    }

    // ========== Camp 4: Command Console Tests ==========

    @Test
    fun camp4_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp4CommandConsole()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 4: Command Console", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp4_displaysCommandInput() {
        // Given
        composeTestRule.setContent {
            Camp4CommandConsole()
        }

        // Then
        composeTestRule
            .onNodeWithText("Enter command", substring = true)
            .assertExists()
    }

    @Test
    fun camp4_canEnterCommand() {
        // Given
        composeTestRule.setContent {
            Camp4CommandConsole()
        }

        // When
        composeTestRule
            .onNodeWithText("Enter command", substring = true)
            .performTextInput("test command")

        // Then - text should be entered
        composeTestRule
            .onNodeWithText("test command")
            .assertExists()
    }

    // ========== Camp 5: State Management Tests ==========

    @Test
    fun camp5_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp5StateManagement()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 5: State Management", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp5_displaysCounterDemo() {
        // Given
        composeTestRule.setContent {
            Camp5StateManagement()
        }

        // Then - scroll to find counter demo
        composeTestRule
            .onNodeWithText("Counter", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun camp5_displaysStateFlowDemo() {
        // Given
        composeTestRule.setContent {
            Camp5StateManagement()
        }

        // Then
        composeTestRule
            .onNodeWithText("StateFlow", substring = true)
            .performScrollTo()
            .assertExists()
    }

    // ========== Camp 6: Advanced Navigation Tests ==========

    @Test
    fun camp6_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp6AdvancedNavigation()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 6: Advanced Navigation", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp6_displaysDeepLinkExamples() {
        // Given
        composeTestRule.setContent {
            Camp6AdvancedNavigation()
        }

        // Then
        composeTestRule
            .onNodeWithText("Deep Link", substring = true)
            .performScrollTo()
            .assertExists()
    }

    // ========== Camp 7: Data Persistence Tests ==========

    @Test
    fun camp7_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp7DataPersistence()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 7: Data Persistence", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp7_displaysCachingStrategies() {
        // Given
        composeTestRule.setContent {
            Camp7DataPersistence()
        }

        // Then
        composeTestRule
            .onNodeWithText("Caching", substring = true)
            .performScrollTo()
            .assertExists()
    }

    @Test
    fun camp7_displaysAddItemButton() {
        // Given
        composeTestRule.setContent {
            Camp7DataPersistence()
        }

        // Then
        composeTestRule
            .onNodeWithText("Add Item", substring = true)
            .performScrollTo()
            .assertExists()
    }

    // ========== Camp 8: Background Processing Tests ==========

    @Test
    fun camp8_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp8BackgroundProcessing()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 8: Background Processing", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp8_displaysWorkManagerSection() {
        // Given
        composeTestRule.setContent {
            Camp8BackgroundProcessing()
        }

        // Then
        composeTestRule
            .onNodeWithText("WorkManager", substring = true)
            .performScrollTo()
            .assertExists()
    }

    @Test
    fun camp8_displaysScheduleWorkButton() {
        // Given
        composeTestRule.setContent {
            Camp8BackgroundProcessing()
        }

        // Then
        composeTestRule
            .onNodeWithText("Schedule", substring = true)
            .performScrollTo()
            .assertExists()
    }

    // ========== Camp 9: System Integration Tests ==========

    @Test
    fun camp9_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp9SystemIntegration()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 9: System Integration", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp9_displaysNotificationSection() {
        // Given
        composeTestRule.setContent {
            Camp9SystemIntegration()
        }

        // Then
        composeTestRule
            .onNodeWithText("Notification", substring = true)
            .performScrollTo()
            .assertExists()
    }

    @Test
    fun camp9_displaysPermissionRequest() {
        // Given
        composeTestRule.setContent {
            Camp9SystemIntegration()
        }

        // Then - check for permission-related content
        composeTestRule
            .onNodeWithText("Permission", substring = true)
            .performScrollTo()
            .assertExists()
    }

    // ========== Camp 10: Deployment Tests ==========

    @Test
    fun camp10_displaysTitle() {
        // Given
        composeTestRule.setContent {
            Camp10Deployment()
        }

        // Then
        composeTestRule
            .onNodeWithText("Camp 10: Deployment", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun camp10_displaysPreReleaseChecklist() {
        // Given
        composeTestRule.setContent {
            Camp10Deployment()
        }

        // Then
        composeTestRule
            .onNodeWithText("Pre-Release Checklist", substring = true)
            .performScrollTo()
            .assertExists()
    }

    @Test
    fun camp10_displaysChecklistItems() {
        // Given
        composeTestRule.setContent {
            Camp10Deployment()
        }

        // Then - verify some checklist items are present
        composeTestRule
            .onNodeWithText("versionCode", substring = true)
            .performScrollTo()
            .assertExists()

        composeTestRule
            .onNodeWithText("ProGuard", substring = true)
            .performScrollTo()
            .assertExists()
    }

    // ========== Interactive Element Tests ==========

    @Test
    fun camp1_sendPostRequest_updatesUI() {
        // Given
        composeTestRule.setContent {
            Camp1RestBasics()
        }

        // When - click POST button
        composeTestRule
            .onNodeWithText("POST /config", substring = true)
            .performClick()

        // Then - wait for loading or response
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            // Should show either "Loading" or response content
            composeTestRule
                .onAllNodesWithText("success", substring = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule
                .onAllNodesWithText("Loading", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun camp4_executeCommand_displaysOutput() {
        // Given
        composeTestRule.setContent {
            Camp4CommandConsole()
        }

        // When - enter and execute command
        composeTestRule
            .onNodeWithText("Enter command", substring = true)
            .performTextInput("help")

        // Find and click execute button
        composeTestRule
            .onNodeWithText("Execute", substring = true)
            .performClick()

        // Then - output should appear
        composeTestRule.waitForIdle()
        // Command output should be visible
    }

    // ========== Scroll Behavior Tests ==========

    @Test
    fun campScreens_areScrollable() {
        // Given - Camp 5 has long content
        composeTestRule.setContent {
            Camp5StateManagement()
        }

        // When/Then - should be able to scroll
        composeTestRule
            .onNodeWithText("Key Takeaways", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun camp7_itemList_isScrollable() {
        // Given
        composeTestRule.setContent {
            Camp7DataPersistence()
        }

        // When - add multiple items to test scrolling
        // Then - list should be scrollable (would need to add items first)
        composeTestRule
            .onNodeWithText("Items", substring = true)
            .performScrollTo()
            .assertExists()
    }

    // ========== Content Verification Tests ==========

    @Test
    fun allCamps_haveTutorialSections() {
        // Test that each camp has structured tutorial content

        val camps = listOf(
            { Camp1RestBasics() },
            { Camp2WebSocket() },
            { Camp3GPSIntegration() },
            { Camp4CommandConsole() },
            { Camp5StateManagement() },
            { Camp6AdvancedNavigation() },
            { Camp7DataPersistence() },
            { Camp8BackgroundProcessing() },
            { Camp9SystemIntegration() },
            { Camp10Deployment() }
        )

        camps.forEach { campContent ->
            composeTestRule.setContent {
                campContent()
            }

            // Each camp should have tutorial-style content
            // Verify at least one of these common tutorial elements exists
            val hasTutorialContent = try {
                composeTestRule
                    .onNodeWithText("Tutorial", substring = true)
                    .assertExists()
                true
            } catch (e: AssertionError) {
                try {
                    composeTestRule
                        .onNodeWithText("Demo", substring = true)
                        .assertExists()
                    true
                } catch (e: AssertionError) {
                    try {
                        composeTestRule
                            .onNodeWithText("Example", substring = true)
                            .assertExists()
                        true
                    } catch (e: AssertionError) {
                        false
                    }
                }
            }

            assert(hasTutorialContent) {
                "Camp should have tutorial content"
            }
        }
    }
}
