# Accessibility Guide - Frontier Command Center

## Overview

This document outlines the accessibility features and best practices implemented in the Frontier Command Center Android app. The app follows WCAG 2.1 Level AA guidelines and Android accessibility best practices.

## Accessibility Features Implemented

### 1. Semantic Structure

**Headings Hierarchy:**
- Screen titles use heading level 1 (`Modifier.heading(1)`)
- Section titles use heading level 2 (`Modifier.heading(2)`)
- Subsections use heading level 3
- Proper heading hierarchy aids navigation with screen readers

**Example:**
```kotlin
Text(
    "Frontier Command Center",
    modifier = Modifier.heading(1)
)
```

### 2. Content Descriptions

**Comprehensive Descriptions:**
- All interactive elements have clear content descriptions
- Icon-only buttons include action labels
- Complex components use `buildContentDescription()` for structured announcements

**Example:**
```kotlin
val cardDescription = buildContentDescription(
    "Camp 1",
    camp.title,
    camp.module,
    "completed",
    "Tap to open"
)
```

### 3. State Descriptions

**Dynamic State Announcements:**
- Completion status ("completed" / "not completed")
- Connection status ("connected" / "disconnected")
- Expandable sections ("expanded" / "collapsed")
- Loading states with progress information

**Example:**
```kotlin
Modifier.stateDescription(
    if (isConnected) "Connected" else "Disconnected"
)
```

### 4. Live Regions

**Real-time Updates:**
- Status messages use polite live regions
- Error messages use assertive live regions (sparingly)
- Loading states announce to screen readers
- Empty states are announced when camps list is empty

**Example:**
```kotlin
Box(
    modifier = Modifier.liveRegion(LiveRegionMode.Polite)
) {
    Text(statusMessage)
}
```

### 5. Touch Targets

**Minimum Size Requirements:**
- All interactive elements meet 48dp minimum touch target
- Buttons and cards provide adequate spacing
- Icon buttons have sufficient padding
- Complies with WCAG 2.1 Level AAA (44x44dp minimum)

### 6. Keyboard Navigation

**Navigation Support:**
- All interactive elements are keyboard accessible
- Focus order follows logical reading order
- Focus indicators are visible
- No keyboard traps

### 7. Screen Reader Support

**TalkBack Compatibility:**
- Merged semantics for complex layouts
- Proper role assignments (Button, Tab, etc.)
- Custom actions for complex interactions
- Logical reading order maintained

## Accessibility Utilities

### Available Helper Functions

Located in `ui/accessibility/AccessibilityUtils.kt`:

1. **`Modifier.heading(level: Int)`**
   - Marks text as a heading with specified level (1-6)
   - Aids screen reader navigation

2. **`Modifier.clickableWithLabel(actionLabel: String)`**
   - Adds custom action label to clickable elements
   - Clarifies purpose of icon-only buttons

3. **`Modifier.liveRegion(mode: LiveRegionMode)`**
   - Marks dynamic content for automatic announcements
   - Modes: Polite (default) or Assertive (critical)

4. **`Modifier.stateDescription(state: String)`**
   - Announces current state of interactive elements
   - Use for toggles, expansion states, connection status

5. **`Modifier.groupedElement(label: String?)`**
   - Groups related elements for unified announcement
   - Reduces verbosity for complex layouts

6. **`Modifier.disabledWithReason(disabled: Boolean, reason: String?)`**
   - Properly announces disabled state with optional reason
   - Helps users understand why action is unavailable

7. **`Modifier.progressInfo(progress: Float, text: String?)`**
   - Announces progress percentage
   - Use for loading indicators, upload progress

8. **`Modifier.expandableText(expanded: Boolean)`**
   - Announces expandable/collapsible state
   - Helps users know content can be expanded

9. **`Modifier.fieldError(hasError: Boolean, errorMessage: String?)`**
   - Announces form field errors
   - Provides error details to screen reader users

10. **`buildContentDescription(vararg parts: String?)`**
    - Combines multiple parts into clear description
    - Filters null/blank values automatically

## Best Practices

### Do's ✅

1. **Provide Text Alternatives**
   - All images have content descriptions
   - Decorative images use `contentDescription = null`
   - Icons paired with labels or descriptions

2. **Maintain Logical Order**
   - Reading order matches visual order
   - Tab order is logical and predictable
   - No unexpected focus changes

3. **Use Semantic HTML Equivalents**
   - Proper roles (Button, Tab, Checkbox)
   - Heading hierarchy (h1 → h2 → h3)
   - Lists use LazyColumn with proper semantics

4. **Provide Feedback**
   - Actions have confirmation messages
   - Errors are announced clearly
   - Loading states are communicated

5. **Support Multiple Input Methods**
   - Touch, keyboard, and switch access
   - No hover-only interactions
   - All actions have keyboard equivalents

### Don'ts ❌

1. **Don't Rely on Color Alone**
   - Use icons + color for status
   - Provide text labels with colors
   - Ensure sufficient contrast (4.5:1 for text)

2. **Don't Create Accessibility Barriers**
   - No auto-playing audio
   - No time limits without extensions
   - No flashing content (seizure risk)

3. **Don't Duplicate Information**
   - Avoid redundant announcements
   - Use `mergeDescendants` for complex layouts
   - Don't say "button" in contentDescription (role provides this)

4. **Don't Skip Heading Levels**
   - Don't jump from h1 to h3
   - Maintain logical hierarchy
   - Only one h1 per screen

5. **Don't Ignore Dynamic Content**
   - Update live regions for status changes
   - Announce errors immediately
   - Provide progress feedback

## Testing Accessibility

### Manual Testing with TalkBack

1. **Enable TalkBack:**
   - Settings → Accessibility → TalkBack → On
   - Or triple-press volume keys (if configured)

2. **Test Navigation:**
   - Swipe right/left to navigate
   - Double-tap to activate
   - Verify all elements are announced

3. **Test Interactions:**
   - Execute all user flows
   - Verify state changes are announced
   - Check error messages are clear

4. **Test Complex Screens:**
   - Camp detail screens
   - Settings with expandable sections
   - Interactive demos (WebSocket, GPS)

### Automated Testing

Run accessibility scanner:
```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.frontiercommand.AccessibilityTest
```

### Accessibility Test Checklist

- [ ] All images have content descriptions
- [ ] All buttons have clear labels
- [ ] Heading hierarchy is correct
- [ ] Touch targets meet 48dp minimum
- [ ] Color contrast meets 4.5:1 ratio
- [ ] All interactions work with TalkBack
- [ ] Form errors are announced
- [ ] Loading states are communicated
- [ ] No keyboard traps
- [ ] Focus order is logical

## Screen-Specific Accessibility

### HomeScreen

- **Main Title:** Heading level 1
- **Camp Cards:** Merged semantics with complete description
- **Empty State:** Live region for status announcement
- **Action Buttons:** Clear labels for Help and Settings

### CampDetailScreen

- **Camp Title:** Heading level 1
- **Sections:** Heading level 2 for major sections
- **Completion Button:** State description (completed/not completed)
- **Command Console:** Live region for command output

### SettingsScreen

- **Settings Title:** Heading level 1
- **Sections:** Expandable sections with state descriptions
- **Theme Selection:** Radio group with clear labels
- **Log Viewer:** Live region for dynamic log updates

### HelpScreen

- **Help Title:** Heading level 1
- **Camp Guides:** Heading level 2 for each guide
- **Expandable Sections:** State descriptions for expand/collapse
- **FAQ Items:** Grouped semantics for questions and answers

## Color Contrast

All text meets WCAG 2.1 Level AA contrast requirements:

- **Normal text:** 4.5:1 minimum
- **Large text (18pt+):** 3:1 minimum
- **Interactive elements:** Clear focus indicators
- **Error states:** High contrast red with icons

## Text Scaling

App supports Android's text scaling preferences:

- Minimum: 85% (small)
- Default: 100% (normal)
- Maximum: 200% (huge)

All layouts adapt to large text sizes without overflow.

## Future Enhancements

### Planned Improvements

1. **Voice Commands**
   - Integrate with Google Assistant
   - Voice navigation between camps
   - Voice input for command console

2. **Customizable Contrast**
   - High contrast mode
   - User-selectable color themes
   - Dark mode optimization

3. **Enhanced Keyboard Navigation**
   - Keyboard shortcuts
   - Jump to section commands
   - Custom key bindings

4. **Captions and Transcripts**
   - If video content added
   - Audio descriptions for images
   - Text alternatives for all media

## Resources

### Documentation

- [Android Accessibility Guide](https://developer.android.com/guide/topics/ui/accessibility)
- [Jetpack Compose Accessibility](https://developer.android.com/jetpack/compose/accessibility)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Material Design Accessibility](https://material.io/design/usability/accessibility.html)

### Testing Tools

- **TalkBack:** Android's built-in screen reader
- **Accessibility Scanner:** Automated testing app
- **Color Contrast Analyzer:** WCAG compliance checker
- **Espresso with Accessibility Checks:** Automated UI testing

## Contact

For accessibility issues or suggestions:
- File an issue in the project repository
- Tag with `accessibility` label
- Include device, Android version, and assistive technology used

---

**Last Updated:** 2024-01-19
**WCAG Level:** AA
**Testing Coverage:** Manual + Automated
