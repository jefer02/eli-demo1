package com.elyndra.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.ui.graphics.vector.ImageVector
import com.elyndra.app.R
import kotlinx.serialization.Serializable

/** Home dashboard: continue-playing shelf, recently added, platform shortcuts. */
@Serializable
data object Home

/** Full library grid/list. [platformId] pre-filters when arriving from a platform shortcut. */
@Serializable
data class Library(val platformId: String? = null)

/** One platform's "folder": hero + carousel of its games, reached by drilling into a platform shortcut. */
@Serializable
data class PlatformDetail(val platformId: String)

@Serializable
data class GameDetail(val gameId: Long)

@Serializable
data object Settings

/** ROM-folder picking + scan progress. Reached from Home's empty state or Settings. */
@Serializable
data object Scanner

data class BottomNavItem(
    val route: Any,
    @StringRes val label: Int,
    val icon: ImageVector,
)

/**
 * The rail's primary destinations. [Settings] deliberately isn't one of them -
 * it hangs off the gear button pinned to the bottom of the rail instead, so the
 * list stays "places you browse games in".
 */
val bottomNavItems = listOf(
    BottomNavItem(Home, R.string.nav_home, Icons.Filled.Home),
    BottomNavItem(Library(), R.string.nav_library, Icons.Filled.VideogameAsset),
)

/** Everything the rail is visible on - the nav items plus the gear's destination. */
val railRoutes: List<Any> = bottomNavItems.map { it.route } + Settings
