package com.frontiercommand.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Pioneer Theme Color Palette
 *
 * Earthy, western-inspired colors that evoke the American frontier:
 * - Browns: Saddle leather, wood, and earth
 * - Greens: Prairie grass and forests
 * - Yellows/Golds: Sunlight, wheat fields, and gold rush
 * - Beiges: Desert sand and canvas tents
 *
 * **Color System:**
 * - Primary: Brown tones for main actions and branding
 * - Secondary: Green tones for complementary elements
 * - Tertiary: Gold/yellow for accents and highlights
 * - Surface variants for cards and backgrounds
 *
 * Colors are defined for both light and dark modes to support system preferences.
 */

// Primary colors - Brown tones (Saddle Brown family)
val PioneerBrown = Color(0xFF8B4513)           // Saddle Brown - primary actions
val PioneerDarkBrown = Color(0xFF654321)       // Dark Brown - pressed states
val PioneerLightBrown = Color(0xFFA0522D)      // Sienna - hover states

// Secondary colors - Green tones (Olive/Forest family)
val PioneerGreen = Color(0xFF556B2F)           // Dark Olive Green - secondary actions
val PioneerDarkGreen = Color(0xFF3D4F1F)       // Darker green - pressed states
val PioneerLightGreen = Color(0xFF6B8E23)      // Olive Drab - hover states

// Tertiary colors - Gold/Yellow tones
val PioneerGold = Color(0xFFDAA520)            // Goldenrod - accents
val PioneerDarkGold = Color(0xFFB8860B)        // Dark Goldenrod
val PioneerLightGold = Color(0xFFFFD700)       // Gold

// Neutral colors - Beige/Cream tones
val PioneerBeige = Color(0xFFF5DEB3)           // Wheat - light surfaces
val PioneerCream = Color(0xFFFFF8DC)           // Cornsilk - backgrounds
val PioneerTan = Color(0xFFD2B48C)             // Tan - surface variants

// Dark mode colors
val PioneerDarkBackground = Color(0xFF1A1410)  // Very dark brown
val PioneerDarkSurface = Color(0xFF2D2418)     // Dark brown surface

// Error colors
val PioneerError = Color(0xFFB00020)           // Error red
val PioneerErrorDark = Color(0xFFCF6679)       // Error red (dark mode)

// Success colors
val PioneerSuccess = Color(0xFF4CAF50)         // Success green
val PioneerSuccessDark = Color(0xFF81C784)     // Success green (dark mode)

// Warning colors
val PioneerWarning = Color(0xFFFF9800)         // Warning orange
val PioneerWarningDark = Color(0xFFFFB74D)     // Warning orange (dark mode)

// Info colors
val PioneerInfo = Color(0xFF2196F3)            // Info blue
val PioneerInfoDark = Color(0xFF64B5F6)        // Info blue (dark mode)

// Text colors
val PioneerTextPrimary = Color(0xFF3E2723)     // Very dark brown for text
val PioneerTextSecondary = Color(0xFF5D4037)   // Dark brown for secondary text
val PioneerTextPrimaryDark = Color(0xFFEFEBE9) // Light beige for dark mode text
val PioneerTextSecondaryDark = Color(0xFFD7CCC8) // Lighter beige for dark mode secondary text
