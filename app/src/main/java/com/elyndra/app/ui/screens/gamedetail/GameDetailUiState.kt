package com.elyndra.app.ui.screens.gamedetail

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.ui.UiMessage

data class GameDetailUiState(
    val game: Game? = null,
    val platform: Platform? = null,
    val allPlatforms: List<Platform> = emptyList(),
    val isLoading: Boolean = true,
    val isLaunching: Boolean = false,
    val isScraping: Boolean = false,
    val launchError: UiMessage? = null,
    val scrapeMessage: UiMessage? = null,
)
