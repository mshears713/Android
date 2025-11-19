package com.frontiercommand.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Pioneer Light Color Scheme
 *
 * Earthy, warm colors optimized for light mode.
 * Emphasizes browns, greens, and golds for a frontier aesthetic.
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors - Brown tones
    primary = PioneerBrown,
    onPrimary = PioneerCream,
    primaryContainer = PioneerLightBrown,
    onPrimaryContainer = PioneerTextPrimary,

    // Secondary colors - Green tones
    secondary = PioneerGreen,
    onSecondary = PioneerCream,
    secondaryContainer = PioneerLightGreen,
    onSecondaryContainer = PioneerTextPrimary,

    // Tertiary colors - Gold tones
    tertiary = PioneerGold,
    onTertiary = PioneerTextPrimary,
    tertiaryContainer = PioneerLightGold,
    onTertiaryContainer = PioneerTextPrimary,

    // Background and surface
    background = PioneerCream,
    onBackground = PioneerTextPrimary,
    surface = PioneerBeige,
    onSurface = PioneerTextPrimary,
    surfaceVariant = PioneerTan,
    onSurfaceVariant = PioneerTextSecondary,

    // Error
    error = PioneerError,
    onError = PioneerCream,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Outline and scrim
    outline = PioneerTextSecondary,
    scrim = Color(0xFF000000).copy(alpha = 0.32f)
)

/**
 * Pioneer Dark Color Scheme
 *
 * Muted, darker variant of the Pioneer theme for dark mode.
 * Maintains the frontier aesthetic while being comfortable for night viewing.
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors - Lighter browns for dark background
    primary = PioneerLightBrown,
    onPrimary = PioneerDarkBackground,
    primaryContainer = PioneerDarkBrown,
    onPrimaryContainer = PioneerTextPrimaryDark,

    // Secondary colors - Lighter greens
    secondary = PioneerLightGreen,
    onSecondary = PioneerDarkBackground,
    secondaryContainer = PioneerDarkGreen,
    onSecondaryContainer = PioneerTextPrimaryDark,

    // Tertiary colors - Lighter golds
    tertiary = PioneerLightGold,
    onTertiary = PioneerDarkBackground,
    tertiaryContainer = PioneerDarkGold,
    onTertiaryContainer = PioneerTextPrimaryDark,

    // Background and surface
    background = PioneerDarkBackground,
    onBackground = PioneerTextPrimaryDark,
    surface = PioneerDarkSurface,
    onSurface = PioneerTextPrimaryDark,
    surfaceVariant = Color(0xFF3D3227),
    onSurfaceVariant = PioneerTextSecondaryDark,

    // Error
    error = PioneerErrorDark,
    onError = PioneerDarkBackground,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Outline and scrim
    outline = PioneerTextSecondaryDark,
    scrim = Color(0xFF000000).copy(alpha = 0.32f)
)

/**
 * PioneerTheme - Main theme composable for the Frontier Command Center
 *
 * Applies the Pioneer color scheme, typography, and shapes to all child composables.
 * Supports both light and dark modes, with optional dynamic color support on Android 12+.
 *
 * **Features:**
 * - Automatic dark/light mode based on system settings
 * - Dynamic color support (Android 12+) with Pioneer fallback
 * - System bars color matching
 * - Edge-to-edge display support
 *
 * **Usage:**
 * ```kotlin
 * setContent {
 *     PioneerTheme {
 *         // Your app content
 *     }
 * }
 * ```
 *
 * @param darkTheme Whether to use dark theme (defaults to system preference)
 * @param dynamicColor Whether to use dynamic colors on Android 12+ (defaults to true)
 * @param content The composable content to theme
 */
@Composable
fun PioneerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine color scheme based on theme and platform
    val colorScheme = when {
        // Dynamic color is available on Android 12+ (API 31+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Use Pioneer dark theme
        darkTheme -> DarkColorScheme

        // Use Pioneer light theme (default)
        else -> LightColorScheme
    }

    // Update system bars to match theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()

            // Update system bar appearance for text/icons
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Apply Material 3 theme with Pioneer customizations
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PioneerTypography,
        content = content
    )
}

/**
 * Import statement for Color to fix reference
 */
import androidx.compose.ui.graphics.Color
