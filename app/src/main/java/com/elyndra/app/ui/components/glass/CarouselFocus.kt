package com.elyndra.app.ui.components.glass

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import kotlin.math.abs

/** Index of whichever visible item's center is closest to the viewport's
 *  center - drives the "focused card" glow in an immersive carousel. */
@Composable
fun rememberCenteredItemIndex(listState: LazyListState): State<Int?> =
    remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo
                .minByOrNull { item -> abs(item.offset + item.size / 2 - viewportCenter) }
                ?.index
        }
    }
