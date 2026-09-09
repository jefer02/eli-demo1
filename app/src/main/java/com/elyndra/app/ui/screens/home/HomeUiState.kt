package com.elyndra.app.ui.screens.home

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform

data class PlatformSummary(val platform: Platform, val gameCount: Int, val representativeCoverPath: String? = null)

data class HomeUiState(
    val continuePlaying: List<Game> = emptyList(),
    val recentlyAdded: List<Game> = emptyList(),
    val platformSummaries: List<PlatformSummary> = emptyList(),
    val isLoading: Boolean = true,
)
