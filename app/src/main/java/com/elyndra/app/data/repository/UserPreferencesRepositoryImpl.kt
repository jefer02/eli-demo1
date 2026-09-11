package com.elyndra.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elyndra.app.domain.model.AccentColor
import com.elyndra.app.domain.model.AppLanguage
import com.elyndra.app.domain.model.GlassTint
import com.elyndra.app.domain.model.SortOrder
import com.elyndra.app.domain.model.ThemeMode
import com.elyndra.app.domain.model.UserPreferences
import com.elyndra.app.domain.model.ViewMode
import com.elyndra.app.domain.repository.UserPreferencesRepository
import com.elyndra.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val GLASS_TINT = stringPreferencesKey("glass_tint")
        val GLASS_BLUR = intPreferencesKey("glass_blur")
        val GLASS_OPACITY = intPreferencesKey("glass_opacity")
        val HERO_SCRIM = intPreferencesKey("hero_scrim")
        val LANGUAGE = stringPreferencesKey("language")
        val VIEW_MODE = stringPreferencesKey("view_mode")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val AUTO_SCRAPE = booleanPreferencesKey("auto_scrape_on_scan")
        val SHOW_HIDDEN = booleanPreferencesKey("show_hidden_games")
    }

    override val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[Keys.THEME_MODE].toEnum<ThemeMode>() ?: ThemeMode.SYSTEM,
            // A stored name that is no longer an enum constant - the accents
            // were renamed wholesale - falls back to the default rather than
            // throwing, so an upgrade never crashes on first read.
            accentColor = prefs[Keys.ACCENT_COLOR].toEnum<AccentColor>() ?: AccentColor.MANDARINA,
            glassTint = prefs[Keys.GLASS_TINT].toEnum<GlassTint>() ?: GlassTint.PAPEL,
            glassBlur = (prefs[Keys.GLASS_BLUR] ?: UserPreferences.DEFAULT_GLASS_BLUR)
                .coerceIn(UserPreferences.GLASS_BLUR_RANGE),
            glassOpacity = (prefs[Keys.GLASS_OPACITY] ?: UserPreferences.DEFAULT_GLASS_OPACITY)
                .coerceIn(UserPreferences.GLASS_OPACITY_RANGE),
            heroScrim = (prefs[Keys.HERO_SCRIM] ?: UserPreferences.DEFAULT_HERO_SCRIM)
                .coerceIn(UserPreferences.HERO_SCRIM_RANGE),
            language = prefs[Keys.LANGUAGE].toEnum<AppLanguage>() ?: AppLanguage.SYSTEM,
            viewMode = prefs[Keys.VIEW_MODE].toEnum<ViewMode>() ?: ViewMode.GRID,
            gridColumns = prefs[Keys.GRID_COLUMNS] ?: Constants.DEFAULT_GRID_COLUMNS,
            sortOrder = prefs[Keys.SORT_ORDER].toEnum<SortOrder>() ?: SortOrder.NAME,
            autoScrapeOnScan = prefs[Keys.AUTO_SCRAPE] ?: true,
            showHiddenGames = prefs[Keys.SHOW_HIDDEN] ?: false,
        )
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override suspend fun updateAccentColor(accent: AccentColor) {
        dataStore.edit { it[Keys.ACCENT_COLOR] = accent.name }
    }

    override suspend fun updateGlassTint(tint: GlassTint) {
        dataStore.edit { it[Keys.GLASS_TINT] = tint.name }
    }

    override suspend fun updateGlassBlur(blur: Int) {
        dataStore.edit { it[Keys.GLASS_BLUR] = blur.coerceIn(UserPreferences.GLASS_BLUR_RANGE) }
    }

    override suspend fun updateGlassOpacity(opacity: Int) {
        dataStore.edit { it[Keys.GLASS_OPACITY] = opacity.coerceIn(UserPreferences.GLASS_OPACITY_RANGE) }
    }

    override suspend fun updateHeroScrim(scrim: Int) {
        dataStore.edit { it[Keys.HERO_SCRIM] = scrim.coerceIn(UserPreferences.HERO_SCRIM_RANGE) }
    }

    override suspend fun updateLanguage(language: AppLanguage) {
        dataStore.edit { it[Keys.LANGUAGE] = language.name }
    }

    override suspend fun updateViewMode(mode: ViewMode) {
        dataStore.edit { it[Keys.VIEW_MODE] = mode.name }
    }

    override suspend fun updateGridColumns(columns: Int) {
        val clamped = columns.coerceIn(Constants.MIN_GRID_COLUMNS, Constants.MAX_GRID_COLUMNS)
        dataStore.edit { it[Keys.GRID_COLUMNS] = clamped }
    }

    override suspend fun updateSortOrder(order: SortOrder) {
        dataStore.edit { it[Keys.SORT_ORDER] = order.name }
    }

    override suspend fun updateAutoScrapeOnScan(enabled: Boolean) {
        dataStore.edit { it[Keys.AUTO_SCRAPE] = enabled }
    }

    override suspend fun updateShowHiddenGames(enabled: Boolean) {
        dataStore.edit { it[Keys.SHOW_HIDDEN] = enabled }
    }

    private inline fun <reified T : Enum<T>> String?.toEnum(): T? =
        this?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() }
}
