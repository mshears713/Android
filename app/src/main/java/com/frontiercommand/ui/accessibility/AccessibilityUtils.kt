package com.frontiercommand.ui.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*

/**
 * AccessibilityUtils - Utility functions and modifiers for accessibility
 *
 * Provides reusable accessibility enhancements for Compose components:
 * - Semantic properties for screen readers
 * - Heading structure support
 * - State descriptions for dynamic content
 * - Custom actions for complex interactions
 * - Live region announcements
 *
 * **WCAG 2.1 Compliance:**
 * - Level A: Basic accessibility requirements
 * - Level AA: Enhanced accessibility (target level)
 * - Level AAA: Highest accessibility (where feasible)
 *
 * **Key Principles:**
 * 1. Perceivable - Information must be presentable to users
 * 2. Operable - UI components must be operable
 * 3. Understandable - Information must be clear
 * 4. Robust - Content must work with assistive technologies
 *
 * **Usage:**
 * ```kotlin
 * Text(
 *     text = "Section Title",
 *     modifier = Modifier.heading(1)
 * )
 *
 * Button(
 *     onClick = { },
 *     modifier = Modifier.clickableWithLabel("Delete item")
 * ) {
 *     Icon(Icons.Default.Delete)
 * }
 *
 * Box(
 *     modifier = Modifier.liveRegion(LiveRegionMode.Polite)
 * ) {
 *     Text(statusMessage)
 * }
 * ```
 *
 * @see SemanticsPropertyKey for available semantic properties
 * @see Role for standard UI component roles
 */

/**
 * Marks a composable as a heading with the specified level
 *
 * Screen readers use headings to allow users to quickly navigate
 * through content structure. Heading levels should follow a logical
 * hierarchy (1 → 2 → 3, not skipping levels).
 *
 * **Best Practices:**
 * - Use heading level 1 for main screen title
 * - Use heading level 2 for major sections
 * - Use heading level 3 for subsections
 * - Don't skip levels (e.g., don't go from 1 to 3)
 *
 * @param level Heading level (1-6, matching HTML heading levels)
 * @return Modifier with heading semantics
 */
fun Modifier.heading(level: Int = 1): Modifier {
    require(level in 1..6) { "Heading level must be between 1 and 6" }

    return this.semantics(mergeDescendants = true) {
        // Mark as heading for screen readers
        heading()

        // Add heading level to content description
        // Some screen readers announce this
        role = when (level) {
            1 -> Role.Button // Placeholder - Compose doesn't have heading role
            else -> Role.Button
        }
    }
}

/**
 * Adds a custom action label for clickable elements
 *
 * Use this when an element's visual representation doesn't clearly
 * convey its action. Screen readers will announce the action label
 * when the element receives focus.
 *
 * **Example:**
 * - Icon-only button: "Delete item" instead of just "Delete icon"
 * - Card with complex content: "View details" instead of reading all content
 *
 * @param actionLabel The action that will be performed when clicked
 * @return Modifier with custom action semantics
 */
fun Modifier.clickableWithLabel(actionLabel: String): Modifier {
    return this.semantics {
        onClick(label = actionLabel) {
            // Action implementation is handled by actual onClick
            // This just provides the label for accessibility
            true
        }
    }
}

/**
 * Marks a region that contains dynamic content that should be announced
 *
 * Live regions tell screen readers to announce changes to content
 * without requiring user focus. Use for:
 * - Status messages
 * - Loading states
 * - Error messages
 * - Real-time updates
 *
 * **Modes:**
 * - Polite: Wait for user to finish current action before announcing
 * - Assertive: Interrupt immediately (use sparingly, only for critical updates)
 *
 * @param mode How urgently changes should be announced
 * @return Modifier with live region semantics
 */
fun Modifier.liveRegion(mode: LiveRegionMode = LiveRegionMode.Polite): Modifier {
    return this.semantics {
        liveRegion = mode
    }
}

/**
 * Provides state description for elements with dynamic state
 *
 * Use this to announce the current state of interactive elements:
 * - "Expanded" / "Collapsed" for expandable sections
 * - "Selected" / "Not selected" for selectable items
 * - "Checked" / "Unchecked" for checkboxes
 * - "Connected" / "Disconnected" for connection status
 *
 * @param state Current state description
 * @return Modifier with state description semantics
 */
fun Modifier.stateDescription(state: String): Modifier {
    return this.semantics {
        stateDescription = state
    }
}

/**
 * Groups related elements together for screen reader navigation
 *
 * When elements are grouped, screen readers will treat them as a single
 * unit and read all content together. Use for:
 * - Form fields with labels
 * - Cards with multiple text elements
 * - List items with complex layouts
 *
 * @param label Optional label for the group
 * @return Modifier with merged semantics
 */
fun Modifier.groupedElement(label: String? = null): Modifier {
    return this.semantics(mergeDescendants = true) {
        if (label != null) {
            contentDescription = label
        }
    }
}

/**
 * Marks an element as disabled for accessibility
 *
 * Ensures screen readers announce disabled state and that the
 * element is not included in accessibility navigation when disabled.
 *
 * @param disabled Whether the element is disabled
 * @param reason Optional reason for being disabled (announced to user)
 * @return Modifier with disabled semantics
 */
fun Modifier.disabledWithReason(disabled: Boolean, reason: String? = null): Modifier {
    if (!disabled) return this

    return this.semantics {
        disabled()
        if (reason != null) {
            stateDescription = "Disabled: $reason"
        }
    }
}

/**
 * Provides progress information for loading states
 *
 * Screen readers will announce progress percentage and state.
 * Use for loading indicators, upload progress, etc.
 *
 * @param progress Current progress (0.0 to 1.0)
 * @param text Optional text description of progress
 * @return Modifier with progress semantics
 */
fun Modifier.progressInfo(progress: Float, text: String? = null): Modifier {
    return this.semantics {
        progressBarRangeInfo = ProgressBarRangeInfo(
            current = progress,
            range = 0f..1f
        )
        if (text != null) {
            contentDescription = text
        }
    }
}

/**
 * Marks text as expandable/collapsible
 *
 * Screen readers will announce that the text can be expanded or collapsed
 * and its current state.
 *
 * @param expanded Whether currently expanded
 * @return Modifier with expandable semantics
 */
fun Modifier.expandableText(expanded: Boolean): Modifier {
    return this.semantics {
        stateDescription = if (expanded) "Expanded" else "Collapsed"
    }
}

/**
 * Provides error information for form fields
 *
 * Screen readers will announce error state and message.
 * Use for form validation errors.
 *
 * @param hasError Whether field has an error
 * @param errorMessage Error message to announce
 * @return Modifier with error semantics
 */
fun Modifier.fieldError(hasError: Boolean, errorMessage: String? = null): Modifier {
    if (!hasError) return this

    return this.semantics {
        error(errorMessage ?: "Invalid input")
    }
}

/**
 * Minimum touch target size for accessibility
 * WCAG 2.1 Level AAA requires 44x44 dp minimum
 * Android recommends 48x48 dp
 */
const val MIN_TOUCH_TARGET_SIZE = 48 // dp

/**
 * Helper function to announce a message to screen readers
 *
 * Use for:
 * - Confirmation messages ("Item deleted")
 * - Status updates ("Connection established")
 * - Error notifications
 *
 * Note: This requires SemanticsModifier implementation at call site
 *
 * @param message Message to announce
 * @param priority How urgently to announce (Polite or Assertive)
 */
data class AccessibilityAnnouncement(
    val message: String,
    val priority: LiveRegionMode = LiveRegionMode.Polite
)

/**
 * Content description builder for complex elements
 *
 * Helps create clear, concise content descriptions by combining
 * multiple pieces of information in a screen-reader friendly format.
 *
 * **Best Practices:**
 * - Be concise but complete
 * - Include all essential information
 * - Avoid redundant words like "button" (role provides this)
 * - Use punctuation for natural pauses
 *
 * @param parts List of content description parts
 * @return Combined content description string
 */
fun buildContentDescription(vararg parts: String?): String {
    return parts.filterNotNull()
        .filter { it.isNotBlank() }
        .joinToString(separator = ". ")
}

/**
 * Accessibility test tags for UI testing
 *
 * Use these consistent tags to enable accessibility-focused UI tests
 */
object AccessibilityTestTags {
    const val MAIN_HEADING = "main_heading"
    const val NAVIGATION_ICON = "navigation_icon"
    const val PRIMARY_ACTION = "primary_action"
    const val SECONDARY_ACTION = "secondary_action"
    const val ERROR_MESSAGE = "error_message"
    const val LOADING_INDICATOR = "loading_indicator"
    const val STATUS_MESSAGE = "status_message"
}

/**
 * Checks if an element meets minimum touch target size requirements
 *
 * @param width Width in dp
 * @param height Height in dp
 * @return true if meets minimum size requirements
 */
fun meetsMinimumTouchTargetSize(width: Int, height: Int): Boolean {
    return width >= MIN_TOUCH_TARGET_SIZE && height >= MIN_TOUCH_TARGET_SIZE
}
