package com.elyndra.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.elyndra.app.domain.model.AccentColor
import com.elyndra.app.domain.model.ThemeMode

private val ElyndraLightColorScheme = lightColorScheme(
    primary = ElyndraVioletPrimary,
    onPrimary = ElyndraVioletOnPrimary,
    primaryContainer = ElyndraVioletPrimaryContainer,
    onPrimaryContainer = ElyndraVioletOnPrimaryContainer,
    secondary = ElyndraTealSecondary,
    onSecondary = ElyndraTealOnSecondary,
    secondaryContainer = ElyndraTealSecondaryContainer,
    onSecondaryContainer = ElyndraTealOnSecondaryContainer,
    tertiary = ElyndraCoralTertiary,
    onTertiary = ElyndraCoralOnTertiary,
    tertiaryContainer = ElyndraCoralTertiaryContainer,
    onTertiaryContainer = ElyndraCoralOnTertiaryContainer,
    error = ElyndraErrorRed,
    onError = ElyndraOnErrorRed,
    errorContainer = ElyndraErrorContainer,
    onErrorContainer = ElyndraOnErrorContainer,
    background = ElyndraBackgroundLight,
    onBackground = ElyndraOnBackgroundLight,
    surface = ElyndraSurfaceLight,
    onSurface = ElyndraOnSurfaceLight,
    surfaceVariant = ElyndraSurfaceVariantLight,
    onSurfaceVariant = ElyndraOnSurfaceVariantLight,
    outline = ElyndraOutlineLight,
)

private val ElyndraDarkColorScheme = darkColorScheme(
    primary = ElyndraVioletPrimary,
    onPrimary = ElyndraVioletOnPrimary,
    primaryContainer = ElyndraVioletOnPrimaryContainer,
    onPrimaryContainer = ElyndraVioletPrimaryContainer,
    secondary = ElyndraTealSecondary,
    onSecondary = ElyndraTealOnSecondary,
    secondaryContainer = ElyndraTealOnSecondaryContainer,
    onSecondaryContainer = ElyndraTealSecondaryContainer,
    tertiary = ElyndraCoralTertiary,
    onTertiary = ElyndraCoralOnTertiary,
    tertiaryContainer = ElyndraCoralOnTertiaryContainer,
    onTertiaryContainer = ElyndraCoralTertiaryContainer,
    error = ElyndraErrorRed,
    onError = ElyndraOnErrorRed,
    errorContainer = ElyndraErrorContainer,
    onErrorContainer = ElyndraOnErrorContainer,
    background = ElyndraBackgroundDark,
    onBackground = ElyndraOnBackgroundDark,
    surface = ElyndraSurfaceDark,
    onSurface = ElyndraOnSurfaceDark,
    surfaceVariant = ElyndraSurfaceVariantDark,
    onSurfaceVariant = ElyndraOnSurfaceVariantDark,
    outline = ElyndraOutlineDark,
)

@Composable
fun ElyndraTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.VIOLET,
    // Liquid Glass is a deliberately-designed color identity (violet/teal/coral
    // glow), not a generic system surface - Material You's per-device wallpaper
    // extraction would silently override it, so it defaults off here.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        useDarkTheme -> ElyndraDarkColorScheme
        else -> ElyndraLightColorScheme
    }.withAccent(accentColor, useDarkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ElyndraTypography,
        shapes = ElyndraShapes,
        content = content,
    )
}

/**
 * Repaints a base scheme with the user's chosen accent. Only the accent-carrying
 * roles move - surfaces, background and error stay put, so contrast that was
 * checked once for light/dark holds for every accent.
 *
 * Dark mode swaps container/on-container the same way [ElyndraDarkColorScheme]
 * does against the light scheme, so a container stays the *quiet* tone in both.
 */
private fun ColorScheme.withAccent(accent: AccentColor, dark: Boolean): ColorScheme {
    val p = accent.palette()
    return copy(
        primary = p.primary,
        onPrimary = p.onPrimary,
        primaryContainer = if (dark) p.onPrimaryContainer else p.primaryContainer,
        onPrimaryContainer = if (dark) p.primaryContainer else p.onPrimaryContainer,
        secondary = p.secondary,
        onSecondary = p.onSecondary,
    )
}
