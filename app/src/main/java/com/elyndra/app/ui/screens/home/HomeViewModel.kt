package com.elyndra.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val SHELF_SIZE = 20

@HiltViewModel
class HomeViewModel @Inject constructor(
    gameRepository: GameRepository,
    platformRepository: PlatformRepository,
) : ViewModel() {

    private val shelf = MutableStateFlow(HomeShelf.RECENTLY_PLAYED)

    val uiState: StateFlow<HomeUiState> = combine(
        gameRepository.observeGames(includeHidden = false),
        platformRepository.observePlatforms(),
        shelf,
    ) { games, platforms, selectedShelf ->
        HomeUiState(
            shelf = selectedShelf,
            games = games.forShelf(selectedShelf),
            platformSummaries = platforms.mapNotNull { platform ->
                val platformGames = games.filter { it.platformId == platform.id }
                // The Android platform is never populated by scanning, only by the
                // "Add apps" picker inside it - always show it so there's a way in.
                val alwaysShow = platform.id == Constants.ANDROID_PLATFORM_ID
                if (platformGames.isEmpty() && !alwaysShow) return@mapNotNull null
                val cover = platformGames.firstOrNull { !it.coverImagePath.isNullOrBlank() }?.coverImagePath
                PlatformSummary(platform, platformGames.size, cover)
            },
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onShelfChange(newShelf: HomeShelf) {
        shelf.value = newShelf
    }

    /**
     * "Recently played" falls back to the newest games when nothing has been
     * launched yet - an empty carousel on first run would leave the home shell
     * with no art and no title to show.
     */
    private fun List<Game>.forShelf(shelf: HomeShelf): List<Game> = when (shelf) {
        HomeShelf.RECENTLY_PLAYED -> filter { it.lastPlayedAt != null }
            .sortedByDescending { it.lastPlayedAt }
            .ifEmpty { sortedByDescending { it.dateAdded } }

        HomeShelf.RECENTLY_ADDED -> sortedByDescending { it.dateAdded }
        HomeShelf.FAVORITES -> filter { it.isFavorite }.sortedBy { it.title }
    }.take(SHELF_SIZE)
}
