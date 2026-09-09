package com.elyndra.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val SHELF_SIZE = 12

@HiltViewModel
class HomeViewModel @Inject constructor(
    gameRepository: GameRepository,
    platformRepository: PlatformRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        gameRepository.observeGames(includeHidden = false),
        platformRepository.observePlatforms(),
    ) { games, platforms ->
        HomeUiState(
            continuePlaying = games
                .filter { it.lastPlayedAt != null }
                .sortedByDescending { it.lastPlayedAt }
                .take(SHELF_SIZE),
            recentlyAdded = games
                .sortedByDescending { it.dateAdded }
                .take(SHELF_SIZE),
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
}
