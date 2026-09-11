package com.elyndra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.elyndra.app.domain.model.UserPreferences

/**
 * The shell has exactly one look: warm paper, ink type, glass panels over
 * artwork. A dark scheme is not a variant of it - the whole material depends on
 * white specular edges and a light canvas showing through the blur - so there
 * is a single scheme here and [UserPreferences.themeMode] no longer selects
 * between two. Color comes from the accent instead.
 */
private val ElyndraBaseScheme = lightColorScheme(
    error = ElyndraErrorRed,
    onError = ElyndraOnErrorRed,
    errorContainer = ElyndraErrorContainer,
    onErrorContainer = ElyndraOnErrorContainer,
    background = ElyndraPaper,
    onBackground = ElyndraInk,
    surface = ElyndraSurface,
    onSurface = ElyndraInk,
    surfaceVariant = ElyndraSurfaceVariant,
    onSurfaceVariant = ElyndraInkMuted,
    outline = ElyndraOutline,
)

@Composable
fun ElyndraTheme(
    preferences: UserPreferences = UserPreferences(),
    content: @Composable () -> Unit,
) {
    val glass = remember(preferences) { GlassSettings.from(preferences) }
    val accent = glass.accent

    // Only the accent-carrying roles move; surfaces and error stay put, so the
    // contrast checked once for the paper canvas holds for all ten accents.
    val colorScheme = remember(accent) {
        ElyndraBaseScheme.copy(
            primary = accent.deep,
            onPrimary = accent.onAccent,
            primaryContainer = accent.container,
            onPrimaryContainer = accent.onContainer,
            secondary = accent.light,
            onSecondary = accent.onAccent,
            secondaryContainer = accent.container,
            onSecondaryContainer = accent.onContainer,
            tertiary = ElyndraGreen,
            onTertiary = ElyndraInk,
        )
    }

    CompositionLocalProvider(LocalGlass provides glass) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ElyndraTypography,
            shapes = ElyndraShapes,
            content = content,
        )
    }
}
