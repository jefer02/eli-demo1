package com.elyndra.app.domain.usecase.game

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.SortOrder
import javax.inject.Inject

/** Pure filter/sort transform for the Library screen; the game list itself
 *  comes from a live [com.elyndra.app.domain.repository.GameRepository] flow. */
class SearchGamesUseCase @Inject constructor() {

    operator fun invoke(
        games: List<Game>,
        query: String = "",
        platformId: String? = null,
        favoritesOnly: Boolean = false,
        sortOrder: SortOrder = SortOrder.NAME,
    ): List<Game> = games
        .asSequence()
        .filter { platformId == null || it.platformId == platformId }
        .filter { !favoritesOnly || it.isFavorite }
        .filter { query.isBlank() || it.title.contains(query, ignoreCase = true) }
        .sortedWith(sortOrder.toComparator())
        .toList()

    private fun SortOrder.toComparator(): Comparator<Game> = when (this) {
        SortOrder.NAME -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.title }
        SortOrder.DATE_ADDED -> compareByDescending { it.dateAdded }
        SortOrder.LAST_PLAYED -> compareByDescending { it.lastPlayedAt ?: -1L }
        SortOrder.PLAYTIME -> compareByDescending { it.totalPlaytimeSeconds }
    }
}
