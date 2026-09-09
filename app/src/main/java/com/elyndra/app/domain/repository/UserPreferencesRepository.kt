package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.AccentColor
import com.elyndra.app.domain.model.AppLanguage
import com.elyndra.app.domain.model.SortOrder
import com.elyndra.app.domain.model.ThemeMode
import com.elyndra.app.domain.model.UserPreferences
import com.elyndra.app.domain.model.ViewMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun updateThemeMode(mode: ThemeMode)
    suspend fun updateAccentColor(accent: AccentColor)
    suspend fun updateLanguage(language: AppLanguage)
    suspend fun updateViewMode(mode: ViewMode)
    suspend fun updateGridColumns(columns: Int)
    suspend fun updateSortOrder(order: SortOrder)
    suspend fun updateAutoScrapeOnScan(enabled: Boolean)
    suspend fun updateShowHiddenGames(enabled: Boolean)
}
