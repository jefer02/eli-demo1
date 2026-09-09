package com.elyndra.app.ui.screens.platformdetail

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.InstalledAndroidApp
import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.SortOrder

data class PlatformDetailUiState(
    val platform: Platform? = null,
    val games: List<Game> = emptyList(),
    val sortOrder: SortOrder = SortOrder.NAME,
    val installedEmulators: List<InstalledEmulatorApp> = emptyList(),
    val launchableApps: List<InstalledAndroidApp> = emptyList(),
    val isAndroidPlatform: Boolean = false,
    val isLoading: Boolean = true,
)
