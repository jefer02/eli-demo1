package com.elyndra.app.ui.screens.home

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform

data class PlatformSummary(val platform: Platform, val gameCount: Int, val representativeCoverPath: String? = null)

/** Which slice of the library the home carousel is showing right now. */
enum class HomeShelf { RECENTLY_PLAYED, RECENTLY_ADDED, FAVORITES }

data class HomeUiState(
    val shelf: HomeShelf = HomeShelf.RECENTLY_PLAYED,
    /** The games in [shelf], in the order the carousel shows them. */
    val games: List<Game> = emptyList(),
    val platformSummaries: List<PlatformSummary> = emptyList(),
    val isLoading: Boolean = true,
) {
    /** True only once loading finished and the whole library is empty, not just this shelf. */
    val isLibraryEmpty: Boolean = !isLoading && platformSummaries.all { it.gameCount == 0 }
}
