package com.elyndra.app.ui.screens.library

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.SortOrder
import com.elyndra.app.domain.model.ViewMode

data class LibraryUiState(
    val games: List<Game> = emptyList(),
    val platforms: List<Platform> = emptyList(),
    val query: String = "",
    val selectedPlatformId: String? = null,
    val favoritesOnly: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NAME,
    val viewMode: ViewMode = ViewMode.GRID,
    val gridColumns: Int = 3,
    val isLoading: Boolean = true,
)
