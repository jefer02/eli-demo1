package com.elyndra.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.util.formatPlaytime

@Composable
fun GameListItem(
    game: Game,
    platformShortName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState = rememberGlassPressState(interactionSource)

    LiquidGlassSurface(
        shape = RoundedCornerShape(GlassMetrics.cardRadius),
        style = GlassMaterials.ultraThin,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .glassPress(pressState)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            LiquidGlassSurface(
                shape = CoverArtShape,
                style = GlassMaterials.thin,
                modifier = Modifier.size(48.dp),
            ) {
                if (game.coverImagePath.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Filled.VideogameAsset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(22.dp),
                    )
                } else {
                    AsyncImage(
                        model = game.coverImagePath,
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$platformShortName · ${formatPlaytime(game.totalPlaytimeSeconds)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (game.isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.action_favorite),
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .width(20.dp)
                        .size(18.dp),
                )
            }
        }
    }
}
