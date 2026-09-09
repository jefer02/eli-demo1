package com.elyndra.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elyndra.app.domain.model.Game
import com.elyndra.app.R
import com.elyndra.app.ui.components.EmptyState
import com.elyndra.app.ui.components.GameCarousel
import com.elyndra.app.ui.components.SectionHeader
import com.elyndra.app.ui.components.glass.GlassButton
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.util.Constants

@Composable
fun HomeScreen(
    onGameClick: (Long) -> Unit,
    onSeeAllPlatform: (String?) -> Unit,
    onNavigateToScanner: () -> Unit,
    onAddAndroidApps: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEmpty = uiState.continuePlaying.isEmpty() &&
        uiState.recentlyAdded.isEmpty() &&
        uiState.platformSummaries.none { it.gameCount > 0 }

    if (!uiState.isLoading && isEmpty) {
        EmptyState(
            icon = Icons.Filled.VideogameAsset,
            title = stringResource(R.string.home_empty_title),
            subtitle = stringResource(R.string.home_empty_subtitle),
            actionLabel = stringResource(R.string.home_scan_roms),
            onAction = onNavigateToScanner,
            secondaryActionLabel = stringResource(R.string.home_add_android_apps),
            onSecondaryAction = onAddAndroidApps,
            modifier = Modifier.fillMaxSize(),
        )
        return
    }

    val platformLabels = remember(uiState.platformSummaries) {
        uiState.platformSummaries.associate { it.platform.id to it.platform.shortName }
    }
    val featured = uiState.continuePlaying.firstOrNull() ?: uiState.recentlyAdded.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        if (featured != null) {
            item {
                HeroBanner(
                    game = featured,
                    platformLabel = platformLabels[featured.platformId],
                    onClick = { onGameClick(featured.id) },
                )
            }
        }
        if (uiState.continuePlaying.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.section_continue_playing)) }
            item { GameCarousel(games = uiState.continuePlaying, onGameClick = onGameClick) }
        }
        if (uiState.recentlyAdded.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.section_recently_added),
                    actionLabel = stringResource(R.string.action_see_all),
                    onActionClick = { onSeeAllPlatform(null) },
                )
            }
            item { GameCarousel(games = uiState.recentlyAdded, onGameClick = onGameClick) }
        }
        if (uiState.platformSummaries.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.section_platforms)) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.platformSummaries, key = { it.platform.id }) { summary ->
                        val countPlural = if (summary.platform.id == Constants.ANDROID_PLATFORM_ID) {
                            R.plurals.app_count
                        } else {
                            R.plurals.game_count
                        }
                        PlatformGlassCard(
                            title = summary.platform.shortName,
                            subtitle = pluralStringResource(countPlural, summary.gameCount, summary.gameCount),
                            coverPath = summary.representativeCoverPath,
                            onClick = { onSeeAllPlatform(summary.platform.id) },
                        )
                    }
                }
            }
        }
    }
}

/** Big immersive banner for whatever's most worth jumping back into. */
@Composable
private fun HeroBanner(game: Game, platformLabel: String?, onClick: () -> Unit) {
    val artModel = game.screenshotImagePath ?: game.logoImagePath ?: game.coverImagePath

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(GlassMetrics.panelRadius))
            .clickable(onClick = onClick),
    ) {
        if (!artModel.isNullOrBlank()) {
            AsyncImage(
                model = artModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }

        // Scrim so the title stays legible over any artwork.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.80f)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
        ) {
            if (platformLabel != null) {
                LiquidGlassSurface(
                    shape = RoundedCornerShape(GlassMetrics.chipRadius),
                    style = GlassMaterials.thick,
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    Text(
                        text = platformLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    )
                }
            }
            Text(
                text = game.title.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                lineHeight = 32.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        GlassButton(
            onClick = onClick,
            style = GlassMaterials.thick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
        }
    }
}

/** A "folder" tile for one platform - a stack of that platform's games to drill into. */
@Composable
private fun PlatformGlassCard(title: String, subtitle: String, coverPath: String?, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    LiquidGlassSurface(
        shape = RoundedCornerShape(GlassMetrics.cardRadius),
        style = GlassMaterials.thin,
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!coverPath.isNullOrBlank()) {
                AsyncImage(
                    model = coverPath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.80f)),
                            ),
                        ),
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (coverPath.isNullOrBlank()) MaterialTheme.colorScheme.onSurface else Color.White,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (coverPath.isNullOrBlank()) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.8f),
                )
            }
        }
    }
}
