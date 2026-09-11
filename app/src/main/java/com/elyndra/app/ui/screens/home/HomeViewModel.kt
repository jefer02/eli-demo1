package com.elyndra.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.repository.GameLauncherRepository
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

/** The parts of home state the user drives, kept apart from what the library reports. */
private data class HomeSelection(
    val filter: LibraryFilter = LibraryFilter.ALL,
    val query: String = "",
    val isSearchOpen: Boolean = false,
    val selectedKey: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    gameRepository: GameRepository,
    platformRepository: PlatformRepository,
    private val launcherRepository: GameLauncherRepository,
) : ViewModel() {

    private val selection = MutableStateFlow(HomeSelection())

    val uiState: StateFlow<HomeUiState> = combine(
        gameRepository.observeGames(includeHidden = false),
        platformRepository.observePlatforms(),
        selection,
    ) { games, platforms, sel ->
        val entries = buildEntries(games, platforms)
            .filter { it.matches(sel.filter) }
            .filter { sel.query.isBlank() || it.name.contains(sel.query, ignoreCase = true) }
            .sortedBy { it.name.lowercase() }

        HomeUiState(
            filter = sel.filter,
            query = sel.query,
            isSearchOpen = sel.isSearchOpen,
            entries = entries,
            selectedKey = sel.selectedKey,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onFilterChange(filter: LibraryFilter) {
        selection.value = selection.value.copy(filter = filter)
    }

    fun onQueryChange(query: String) {
        selection.value = selection.value.copy(query = query)
    }

    /** Closing search clears the query too, so a hidden filter can't keep the rail short. */
    fun onToggleSearch() {
        val current = selection.value
        selection.value = current.copy(
            isSearchOpen = !current.isSearchOpen,
            query = if (current.isSearchOpen) "" else current.query,
        )
    }

    fun onSelect(key: String) {
        selection.value = selection.value.copy(selectedKey = key)
    }

    /**
     * Builds the unified rail: one card per platform that actually holds ROMs,
     * plus one card per installed Android game.
     *
     * The Android platform never appears as a folder - its games are the cards -
     * so it is skipped on the console side rather than showing up empty.
     */
    private fun buildEntries(games: List<Game>, platforms: List<Platform>): List<LibraryEntry> {
        val emulatorLabels = launcherRepository.getInstalledCandidateEmulators()
            .associate { it.packageName to it.label }

        val consoles = platforms.mapNotNull { platform ->
            if (platform.id == Constants.ANDROID_PLATFORM_ID) return@mapNotNull null
            val platformGames = games.filter { it.platformId == platform.id }
            if (platformGames.isEmpty()) return@mapNotNull null
            LibraryEntry.ConsoleFolder(
                platform = platform,
                romCount = platformGames.size,
                emulatorName = platform.emulatorPackageName?.let { emulatorLabels[it] ?: it },
                coverPath = platformGames.firstNotNullOfOrNull { it.coverImagePath },
            )
        }

        val apps = games.filter { it.isNativeApp }.map(LibraryEntry::AndroidApp)

        return consoles + apps
    }

    private fun LibraryEntry.matches(filter: LibraryFilter): Boolean = when (filter) {
        LibraryFilter.ALL -> true
        LibraryFilter.ANDROID -> this is LibraryEntry.AndroidApp
        LibraryFilter.CONSOLES -> this is LibraryEntry.ConsoleFolder
    }
}
