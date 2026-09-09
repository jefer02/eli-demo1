package com.elyndra.app.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elyndra.app.domain.model.SortOrder
import com.elyndra.app.domain.model.ViewMode
import com.elyndra.app.ui.components.AlphabetScrubber
import com.elyndra.app.R
import com.elyndra.app.ui.components.ElyndraSearchField
import com.elyndra.app.ui.components.EmptyState
import com.elyndra.app.ui.components.GameGridItem
import com.elyndra.app.ui.components.GameListItem
import com.elyndra.app.ui.components.glass.GlassChip
import com.elyndra.app.ui.components.glass.GlassTopBar
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    initialPlatformFilter: String?,
    onGameClick: (Long) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(initialPlatformFilter) {
        if (initialPlatformFilter != null) viewModel.onPlatformFilterChange(initialPlatformFilter)
    }

    fun jumpToLetter(letter: Char) {
        val index = uiState.games.indexOfFirst { it.title.firstOrNull()?.uppercaseChar() == letter }
        if (index < 0) return
        coroutineScope.launch {
            if (uiState.viewMode == ViewMode.GRID) gridState.animateScrollToItem(index) else listState.animateScrollToItem(index)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = stringResource(R.string.library_title),
                actions = {
                    IconButton(onClick = viewModel::onToggleViewMode) {
                        Icon(
                            imageVector = if (uiState.viewMode == ViewMode.GRID) Icons.Filled.ViewList else Icons.Filled.GridView,
                            contentDescription = stringResource(R.string.library_toggle_view),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            ElyndraSearchField(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                item {
                    GlassChip(
                        selected = uiState.favoritesOnly,
                        onClick = { viewModel.onFavoritesOnlyChange(!uiState.favoritesOnly) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (uiState.favoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                            )
                        },
                        label = { Text(stringResource(R.string.filter_favorites)) },
                    )
                }
                item {
                    GlassChip(
                        selected = uiState.selectedPlatformId == null,
                        onClick = { viewModel.onPlatformFilterChange(null) },
                        label = { Text(stringResource(R.string.filter_all)) },
                    )
                }
                items(uiState.platforms, key = { it.id }) { platform ->
                    GlassChip(
                        selected = uiState.selectedPlatformId == platform.id,
                        onClick = {
                            viewModel.onPlatformFilterChange(
                                if (uiState.selectedPlatformId == platform.id) null else platform.id,
                            )
                        },
                        label = { Text(platform.shortName) },
                    )
                }
            }

            if (uiState.games.isEmpty() && !uiState.isLoading) {
                EmptyState(
                    icon = Icons.Filled.SearchOff,
                    title = stringResource(R.string.library_empty_title),
                    subtitle = stringResource(R.string.library_empty_subtitle),
                    modifier = Modifier.padding(top = 16.dp),
                )
            } else {
                val platformShortNames = uiState.platforms.associate { it.id to it.shortName }
                val showScrubber = uiState.sortOrder == SortOrder.NAME && uiState.games.size > 12

                Row(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (uiState.viewMode == ViewMode.GRID) {
                            LazyVerticalGrid(
                                state = gridState,
                                columns = GridCells.Fixed(uiState.gridColumns),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(uiState.games, key = { it.id }) { game ->
                                    GameGridItem(
                                        game = game,
                                        onClick = { onGameClick(game.id) },
                                        onLongClick = { viewModel.onToggleFavorite(game.id, !game.isFavorite) },
                                    )
                                }
                            }
                        } else {
                            LazyColumn(state = listState) {
                                items(uiState.games, key = { it.id }) { game ->
                                    GameListItem(
                                        game = game,
                                        platformShortName = platformShortNames[game.platformId] ?: game.platformId,
                                        onClick = { onGameClick(game.id) },
                                    )
                                }
                            }
                        }
                    }

                    if (showScrubber) {
                        val availableLetters = remember(uiState.games) {
                            uiState.games.mapNotNullTo(mutableSetOf()) { it.title.firstOrNull()?.uppercaseChar() }
                        }
                        AlphabetScrubber(
                            availableLetters = availableLetters,
                            onLetterSelected = ::jumpToLetter,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
