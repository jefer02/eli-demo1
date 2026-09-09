package com.elyndra.app.ui.screens.settings

import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.model.MetadataSourceId
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.RomFolder
import com.elyndra.app.domain.model.UserPreferences
import com.elyndra.app.ui.UiMessage

data class SettingsUiState(
    val romFolders: List<RomFolder> = emptyList(),
    val platforms: List<Platform> = emptyList(),
    val installedEmulators: List<InstalledEmulatorApp> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val credentials: IntegrationCredentials = IntegrationCredentials(),
    val isRescrapingLibrary: Boolean = false,
    val rescrapeProgressMessage: UiMessage? = null,
) {
    fun isConfigured(source: MetadataSourceId): Boolean = credentials.hasCredentials(source)
}
