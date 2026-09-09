package com.elyndra.app.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.model.SortOrder
import com.elyndra.app.domain.model.ViewMode
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.domain.repository.UserPreferencesRepository
import com.elyndra.app.domain.usecase.game.SearchGamesUseCase
import com.elyndra.app.domain.usecase.game.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    platformRepository: PlatformRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val searchGamesUseCase: SearchGamesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private data class FilterState(val query: String, val platformId: String?, val favoritesOnly: Boolean)

    private val query = MutableStateFlow("")
    private val selectedPlatformId = MutableStateFlow<String?>(null)
    private val favoritesOnly = MutableStateFlow(false)
    private val filterState = combine(query, selectedPlatformId, favoritesOnly, ::FilterState)

    val uiState: StateFlow<LibraryUiState> = combine(
        gameRepository.observeGames(includeHidden = false),
        platformRepository.observePlatforms(),
        userPreferencesRepository.preferences,
        filterState,
    ) { games, platforms, prefs, filters ->
        LibraryUiState(
            games = searchGamesUseCase(
                games = games,
                query = filters.query,
                platformId = filters.platformId,
                favoritesOnly = filters.favoritesOnly,
                sortOrder = prefs.sortOrder,
            ),
            platforms = platforms,
            query = filters.query,
            selectedPlatformId = filters.platformId,
            favoritesOnly = filters.favoritesOnly,
            sortOrder = prefs.sortOrder,
            viewMode = prefs.viewMode,
            gridColumns = prefs.gridColumns,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onPlatformFilterChange(platformId: String?) {
        selectedPlatformId.value = platformId
    }

    fun onFavoritesOnlyChange(value: Boolean) {
        favoritesOnly.value = value
    }

    fun onSortOrderChange(order: SortOrder) {
        viewModelScope.launch { userPreferencesRepository.updateSortOrder(order) }
    }

    fun onToggleViewMode() {
        val next = if (uiState.value.viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
        viewModelScope.launch { userPreferencesRepository.updateViewMode(next) }
    }

    fun onToggleFavorite(gameId: Long, isFavorite: Boolean) {
        viewModelScope.launch { toggleFavoriteUseCase(gameId, isFavorite) }
    }
}
