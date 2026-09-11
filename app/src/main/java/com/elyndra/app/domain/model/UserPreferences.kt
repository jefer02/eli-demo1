package com.elyndra.app.domain.model

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class ViewMode { GRID, LIST }

enum class SortOrder { NAME, DATE_ADDED, LAST_PLAYED, PLAYTIME }

/**
 * The accent the whole UI is tinted with. Only the *identity* lives here - the
 * actual Color values are a UI concern and live in ui/theme/AccentPalettes.kt,
 * so the domain layer stays free of android.graphics.
 *
 * The three brand tones come first ([MANDARINA], [FUEGO], [MENTA]); the rest
 * widen the range without leaving the warm-to-cool arc the shell is built on.
 */
enum class AccentColor {
    MANDARINA, FUEGO, MENTA, COBALTO, LILA, CORAL, TURQUESA, ORO, CHICLE, GRAFITO
}

/**
 * The color the glass panels are tinted with before their blur. [PAPEL] is the
 * neutral white the shell is designed around; the others pull the whole
 * chrome warm, cool or dark without touching the accent.
 *
 * As with [AccentColor], the RGB values live in ui/theme/GlassStyle.kt.
 */
enum class GlassTint { PAPEL, ARENA, AMBAR, COBRE, MENTA, CIELO, LILA, HUMO }

/**
 * [SYSTEM] follows the device locale; the rest force a language regardless of it.
 * [tag] is a BCP-47 tag, empty for SYSTEM.
 */
enum class AppLanguage(val tag: String) {
    SYSTEM(""),
    ENGLISH("en"),
    SPANISH("es"),
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.MANDARINA,
    val glassTint: GlassTint = GlassTint.PAPEL,
    /** Backdrop blur radius behind every glass panel, in dp. */
    val glassBlur: Int = DEFAULT_GLASS_BLUR,
    /** How opaque the glass tint sits over that blur, as a percentage. */
    val glassOpacity: Int = DEFAULT_GLASS_OPACITY,
    /** How hard the hero's scrim darkens the artwork behind its title, as a percentage. */
    val heroScrim: Int = DEFAULT_HERO_SCRIM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val viewMode: ViewMode = ViewMode.GRID,
    val gridColumns: Int = 3,
    val sortOrder: SortOrder = SortOrder.NAME,
    val autoScrapeOnScan: Boolean = true,
    val showHiddenGames: Boolean = false,
) {
    companion object {
        const val DEFAULT_GLASS_BLUR = 16
        const val DEFAULT_GLASS_OPACITY = 55
        const val DEFAULT_HERO_SCRIM = 62

        /** The ranges the Settings sliders offer, and what the values are clamped to. */
        val GLASS_BLUR_RANGE = 0..40
        val GLASS_OPACITY_RANGE = 5..90
        val HERO_SCRIM_RANGE = 20..85
    }
}
