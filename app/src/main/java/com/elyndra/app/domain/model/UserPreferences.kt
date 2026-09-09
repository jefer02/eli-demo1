package com.elyndra.app.domain.model

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class ViewMode { GRID, LIST }

enum class SortOrder { NAME, DATE_ADDED, LAST_PLAYED, PLAYTIME }

/**
 * The accent the whole UI is tinted with. Only the *identity* lives here - the
 * actual Color values are a UI concern and live in ui/theme/AccentPalettes.kt,
 * so the domain layer stays free of android.graphics.
 */
enum class AccentColor { CORAL, AMBER, LIME, EMERALD, SKY, INDIGO, VIOLET, MAGENTA, SLATE }

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
    val accentColor: AccentColor = AccentColor.VIOLET,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val viewMode: ViewMode = ViewMode.GRID,
    val gridColumns: Int = 3,
    val sortOrder: SortOrder = SortOrder.NAME,
    val autoScrapeOnScan: Boolean = true,
    val showHiddenGames: Boolean = false,
)
