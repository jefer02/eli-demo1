package com.elyndra.app.ui.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elyndra.app.domain.model.Game
import com.elyndra.app.ui.components.glass.rememberCenteredItemIndex

/** Horizontal, snap-scrolling shelf where the centered card gets a focus glow. */
@Composable
fun GameCarousel(games: List<Game>, onGameClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val centeredIndex by rememberCenteredItemIndex(listState)

    LazyRow(
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(listState),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        itemsIndexed(games, key = { _, game -> game.id }) { index, game ->
            GameGridItem(
                game = game,
                onClick = { onGameClick(game.id) },
                emphasized = index == centeredIndex,
                modifier = Modifier.width(140.dp),
            )
        }
    }
}
