package com.elyndra.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elyndra.app.R
import com.elyndra.app.domain.model.Game
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.components.glass.glassPress
import com.elyndra.app.ui.components.glass.rememberGlassPressState
import com.elyndra.app.ui.theme.CoverArtShape
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GridTileLabelStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameGridItem(
    game: Game,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    /** True for the centered/focused card in an immersive carousel - a glowing ring, not just a press state. */
    emphasized: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState = rememberGlassPressState(interactionSource)
    val emphasisAlpha by animateFloatAsState(targetValue = if (emphasized) 1f else 0f, label = "emphasisGlow")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassPress(pressState)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        LiquidGlassSurface(
            shape = CoverArtShape,
            style = if (emphasized) GlassMaterials.thick else GlassMaterials.thin,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .border(2.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = emphasisAlpha * 0.9f), CoverArtShape),
        ) {
            if (game.coverImagePath.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Filled.VideogameAsset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp),
                )
            } else {
                AsyncImage(
                    model = game.coverImagePath,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (game.isFavorite) {
                LiquidGlassSurface(
                    shape = CircleShape,
                    style = GlassMaterials.thick,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(26.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.action_favorite),
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(14.dp),
                    )
                }
            }
        }
        Text(
            text = game.title,
            style = GridTileLabelStyle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
}
