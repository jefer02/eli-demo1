package com.elyndra.app.ui.screens.home

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform

/**
 * One card on the home rail.
 *
 * The shell shows a single unified shelf rather than one row per platform:
 * an emulator folder and an installed Android game sit side by side, sorted
 * together by name. They are drawn differently and open differently, hence the
 * two shapes, but they queue in one list.
 */
sealed interface LibraryEntry {
    /** Sort key and rail label. */
    val name: String

    /** Stable identity for selection and for LazyRow keys. */
    val key: String

    /** A whole platform's ROMs, drawn as a wide card. Opening it drills into the folder. */
    data class ConsoleFolder(
        val platform: Platform,
        val romCount: Int,
        val emulatorName: String?,
        /** Cover of some game in the folder, for the hero behind the title. */
        val coverPath: String?,
    ) : LibraryEntry {
        override val name: String get() = platform.displayName
        override val key: String get() = "console:${platform.id}"
    }

    /** One installed Android game, drawn as a square icon. Opening it launches the app. */
    data class AndroidApp(val game: Game) : LibraryEntry {
        override val name: String get() = game.title
        override val key: String get() = "app:${game.id}"

        /** Two-letter monogram for the tile when there is no scraped icon. */
        val initials: String
            get() = game.title.split(' ')
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() }
    }
}

/** The rail's segmented filter. [ALL] is the default and mixes both kinds. */
enum class LibraryFilter { ALL, ANDROID, CONSOLES }

data class HomeUiState(
    val filter: LibraryFilter = LibraryFilter.ALL,
    val query: String = "",
    val isSearchOpen: Boolean = false,
    /** Every entry, already filtered by [filter] and [query], in rail order. */
    val entries: List<LibraryEntry> = emptyList(),
    /** [LibraryEntry.key] of the tapped card - the hero and the Open button follow it. */
    val selectedKey: String? = null,
    val isLoading: Boolean = true,
) {
    val selected: LibraryEntry? = entries.firstOrNull { it.key == selectedKey } ?: entries.firstOrNull()

    /** True only once loading finished and there is nothing at all to show, filters aside. */
    val isLibraryEmpty: Boolean = !isLoading && entries.isEmpty() &&
        query.isBlank() && filter == LibraryFilter.ALL
}
