package com.elyndra.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.elyndra.app.domain.model.AccentColor

/**
 * One accent's worth of scheme colors. [swatch] is what the settings picker
 * draws in its circle - it is the primary tone, kept separate so a future
 * gradient swatch doesn't have to change the scheme colors.
 */
data class AccentPalette(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
) {
    val swatch: Color get() = primary
}

/**
 * The accents offered in Settings > Personalization. Hand-picked rather than
 * generated: each one carries its own paired secondary so a warm accent doesn't
 * end up sitting next to the stock teal.
 */
private val accentPalettes: Map<AccentColor, AccentPalette> = mapOf(
    AccentColor.CORAL to AccentPalette(
        primary = Color(0xFFFF5A6E),
        onPrimary = Color(0xFF3F0009),
        primaryContainer = Color(0xFFFFDAD9),
        onPrimaryContainer = Color(0xFF410008),
        secondary = Color(0xFFFF9E80),
        onSecondary = Color(0xFF3F1400),
    ),
    AccentColor.AMBER to AccentPalette(
        primary = Color(0xFFFFA726),
        onPrimary = Color(0xFF3E2600),
        primaryContainer = Color(0xFFFFDDB3),
        onPrimaryContainer = Color(0xFF291800),
        secondary = Color(0xFFFFD54F),
        onSecondary = Color(0xFF3B2E00),
    ),
    AccentColor.LIME to AccentPalette(
        primary = Color(0xFFC6D94A),
        onPrimary = Color(0xFF2A3200),
        primaryContainer = Color(0xFFE6F2A8),
        onPrimaryContainer = Color(0xFF1C2200),
        secondary = Color(0xFF9CCC65),
        onSecondary = Color(0xFF1B3300),
    ),
    AccentColor.EMERALD to AccentPalette(
        primary = Color(0xFF2ECC8F),
        onPrimary = Color(0xFF00382A),
        primaryContainer = Color(0xFFA8F2D5),
        onPrimaryContainer = Color(0xFF002019),
        secondary = Color(0xFF4DD0B1),
        onSecondary = Color(0xFF00382F),
    ),
    AccentColor.SKY to AccentPalette(
        primary = Color(0xFF4FA8FF),
        onPrimary = Color(0xFF00305C),
        primaryContainer = Color(0xFFCFE4FF),
        onPrimaryContainer = Color(0xFF001B3C),
        secondary = Color(0xFF7FD4FF),
        onSecondary = Color(0xFF003547),
    ),
    AccentColor.INDIGO to AccentPalette(
        primary = Color(0xFF5B6CFF),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFDDE1FF),
        onPrimaryContainer = Color(0xFF00105C),
        secondary = Color(0xFF9DA5FF),
        onSecondary = Color(0xFF0B1258),
    ),
    AccentColor.VIOLET to AccentPalette(
        primary = ElyndraVioletPrimary,
        onPrimary = ElyndraVioletOnPrimary,
        primaryContainer = ElyndraVioletPrimaryContainer,
        onPrimaryContainer = ElyndraVioletOnPrimaryContainer,
        secondary = ElyndraTealSecondary,
        onSecondary = ElyndraTealOnSecondary,
    ),
    AccentColor.MAGENTA to AccentPalette(
        primary = Color(0xFFE05BD8),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFD6F7),
        onPrimaryContainer = Color(0xFF390036),
        secondary = Color(0xFFFF9BE0),
        onSecondary = Color(0xFF470039),
    ),
    AccentColor.SLATE to AccentPalette(
        primary = Color(0xFF8E99B0),
        onPrimary = Color(0xFF0F1420),
        primaryContainer = Color(0xFFDDE2EE),
        onPrimaryContainer = Color(0xFF121722),
        secondary = Color(0xFFB9C2D6),
        onSecondary = Color(0xFF141A26),
    ),
)

fun AccentColor.palette(): AccentPalette = accentPalettes.getValue(this)
