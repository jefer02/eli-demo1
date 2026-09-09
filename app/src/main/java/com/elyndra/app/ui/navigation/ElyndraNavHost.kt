package com.elyndra.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.elyndra.app.ui.components.glass.GlassBackdrop
import com.elyndra.app.ui.screens.gamedetail.GameDetailScreen
import com.elyndra.app.ui.screens.home.HomeScreen
import com.elyndra.app.ui.screens.library.LibraryScreen
import com.elyndra.app.ui.screens.platformdetail.PlatformDetailScreen
import com.elyndra.app.ui.screens.scanner.ScannerScreen
import com.elyndra.app.ui.screens.settings.SettingsScreen
import com.elyndra.app.util.Constants

/** Fluid fade+scale in place of the platform's default hard-cut screen swap. */
private const val TRANSITION_DURATION_MS = 260

@Composable
fun ElyndraNavHost() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val isCurrent: (Any) -> Boolean = { route ->
        currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
    }
    val showRail = railRoutes.any(isCurrent)

    Box(modifier = Modifier.fillMaxSize()) {
        GlassBackdrop(modifier = Modifier.fillMaxSize())

        Row(modifier = Modifier.fillMaxSize()) {
            if (showRail) {
                val navigateTop: (Any) -> Unit = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                GlassSideRail(
                    items = bottomNavItems,
                    isSelected = isCurrent,
                    onItemClick = navigateTop,
                    onSettingsClick = { navigateTop(Settings) },
                    settingsSelected = isCurrent(Settings),
                )
            }

            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(12.dp),
                enterTransition = {
                    fadeIn(tween(TRANSITION_DURATION_MS)) + scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(TRANSITION_DURATION_MS),
                    )
                },
                exitTransition = {
                    fadeOut(tween(TRANSITION_DURATION_MS)) + scaleOut(
                        targetScale = 1.03f,
                        animationSpec = tween(TRANSITION_DURATION_MS),
                    )
                },
                popEnterTransition = {
                    fadeIn(tween(TRANSITION_DURATION_MS)) + scaleIn(
                        initialScale = 1.03f,
                        animationSpec = tween(TRANSITION_DURATION_MS),
                    )
                },
                popExitTransition = {
                    fadeOut(tween(TRANSITION_DURATION_MS)) + scaleOut(
                        targetScale = 0.96f,
                        animationSpec = tween(TRANSITION_DURATION_MS),
                    )
                },
            ) {
                composable<Home> {
                    HomeScreen(
                        onGameClick = { gameId -> navController.navigate(GameDetail(gameId)) },
                        onSeeAllPlatform = { platformId ->
                            if (platformId == null) {
                                navController.navigate(Library())
                            } else {
                                navController.navigate(PlatformDetail(platformId))
                            }
                        },
                        onNavigateToScanner = { navController.navigate(Scanner) },
                        onAddAndroidApps = { navController.navigate(PlatformDetail(Constants.ANDROID_PLATFORM_ID)) },
                    )
                }
                composable<Library> { backStackEntry ->
                    val args = backStackEntry.toRoute<Library>()
                    LibraryScreen(
                        initialPlatformFilter = args.platformId,
                        onGameClick = { gameId -> navController.navigate(GameDetail(gameId)) },
                    )
                }
                composable<PlatformDetail> { backStackEntry ->
                    val args = backStackEntry.toRoute<PlatformDetail>()
                    PlatformDetailScreen(
                        platformId = args.platformId,
                        onBack = { navController.popBackStack() },
                        onGameClick = { gameId -> navController.navigate(GameDetail(gameId)) },
                    )
                }
                composable<GameDetail> { backStackEntry ->
                    val args = backStackEntry.toRoute<GameDetail>()
                    GameDetailScreen(
                        gameId = args.gameId,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable<Settings> {
                    SettingsScreen(onNavigateToScanner = { navController.navigate(Scanner) })
                }
                composable<Scanner> {
                    ScannerScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
