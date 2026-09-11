package com.elyndra.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.elyndra.app.ui.theme.artworkPairFor

/**
 * Cover art if it has been scraped, and a stable per-title gradient if it has
 * not. Never an empty gray rectangle: a rail of unscraped games has to read as
 * a library, not as a loading failure.
 *
 * [key] is what the fallback gradient is derived from - pass the title, so the
 * same game keeps its color even after its id changes or it is re-scanned.
 */
@Composable
fun ArtworkTile(
    key: String,
    imagePath: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier) {
        if (imagePath.isNullOrBlank()) {
            val (light, deep) = artworkPairFor(key)
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(light, deep))),
            )
        } else {
            AsyncImage(
                model = imagePath,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        // The soft top-left sheen every tile in the design carries, art or not.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.34f), Color.Transparent),
                        start = Offset.Zero,
                        end = Offset(320f, 420f),
                    ),
                ),
        )
        overlay()
    }
}
