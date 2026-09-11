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
import com.elyndra.app.ui.components.glass.AuroraBackdrop
import com.elyndra.app.ui.screens.assistant.AssistantScreen
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
    // Home is its own full-bleed shell with a dock of its own, so the rail (and
    // the breathing room every other screen gets) would fight it for the edges.
    val isHome = isCurrent(Home)
    val showRail = railRoutes.any(isCurrent) && !isHome

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackdrop(modifier = Modifier.fillMaxSize())

        Row(modifier = Modifier.fillMaxSize()) {
            if (showRail) {
                val navigateTop: (Any) -> Unit = { route ->
                    if (route == Home) {
                        // Home is the graph's start destination, so reaching it is a
                        // pop, not a navigate. Navigating would save the popped stack
                        // under Home's id and then restoreState would hand that very
                        // stack straight back - tapping Home from Settings landed on
                        // Settings again, and from Library it landed on Settings too.
                        navController.popBackStack<Home>(inclusive = false)
                    } else {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    .padding(if (isHome) 0.dp else 12.dp),
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
                        onOpenFolder = { platformId -> navController.navigate(PlatformDetail(platformId)) },
                        onLaunchApp = { gameId -> navController.navigate(GameDetail(gameId)) },
                        onAddGames = { navController.navigate(Scanner) },
                        onOpenAssistant = { navController.navigate(Assistant) },
                        onOpenSettings = { navController.navigate(Settings) },
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
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToScanner = { navController.navigate(Scanner) },
                    )
                }
                composable<Assistant> {
                    AssistantScreen(onBack = { navController.popBackStack() })
                }
                composable<Scanner> {
                    ScannerScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
