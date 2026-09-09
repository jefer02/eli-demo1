package com.elyndra.app.ui.screens.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elyndra.app.R
import com.elyndra.app.domain.model.Game
import com.elyndra.app.ui.components.EmptyState
import com.elyndra.app.ui.components.StatusPill
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.components.glass.rememberCenteredItemIndex
import com.elyndra.app.ui.theme.GlassMaterials
import kotlinx.coroutines.launch

/** How many pagination dots the dock draws before it stops adding more. */
private const val MAX_DOTS = 7

/** Room reserved at the bottom of the column for the floating dock. */
private val DockSpace = 78.dp

/** Below this the top half stops being able to hold a wordmark worth the name. */
private val MinTitleArea = 130.dp

/**
 * Card sizes derived from the screen rather than fixed.
 *
 * A phone in landscape is only ~390dp tall, a tablet more than twice that;
 * hard-coded 200dp cards fit the second and swallow the first whole, leaving
 * the wordmark with no height to occupy.
 */
private data class RailMetrics(val focused: Dp, val resting: Dp, val slot: Dp) {
    val radius: Dp get() = focused * 0.15f
}

private fun railMetricsFor(screenHeight: Dp): RailMetrics {
    val available = screenHeight - DockSpace - MinTitleArea
    val focused = (available * 0.78f).coerceIn(112.dp, 232.dp)
    return RailMetrics(focused = focused, resting = focused * 0.76f, slot = focused + 24.dp)
}

/**
 * The shell keeps its own dark canvas instead of the theme's surfaces: it is
 * built out of white type and scrims over artwork, which the light scheme's
 * near-white background would erase. Cover art and the accent still carry all
 * the color.
 */
private val ShellCanvas = Color(0xFF0A0912)
private val ShellChrome = Color(0xFF151327)

/**
 * The immersive home shell: full-bleed art and a giant wordmark for whichever
 * game is centered in the carousel, over a floating dock.
 *
 * Everything above the rail is derived from the *centered* card rather than a
 * separately tracked "selected game", so the art, the title and the carousel
 * can never disagree - scrolling is the only thing that changes them.
 */
@Composable
fun HomeScreen(
    onGameClick: (Long) -> Unit,
    onSeeAllPlatform: (String?) -> Unit,
    onNavigateToScanner: () -> Unit,
    onAddAndroidApps: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLibraryEmpty) {
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

    val games = uiState.games
    val listState = rememberLazyListState()
    val centeredIndex by rememberCenteredItemIndex(listState)
    val focusedGame = games.getOrNull(centeredIndex ?: 0)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val rail = railMetricsFor(screenHeight)
        val titleArea = (screenHeight - rail.slot - DockSpace).coerceAtLeast(MinTitleArea)

        ImmersiveBackground(game = focusedGame)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                focusedGame?.let { game ->
                    GiantTitle(
                        title = game.title,
                        // Aim for a third of the screen, but never taller than the
                        // band actually left above the rail.
                        maxTitleHeight = minOf(screenHeight * 0.32f, titleArea - 46.dp),
                        maxTitleWidth = screenWidth * 0.78f,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 36.dp, top = 12.dp),
                    )
                }

                // One column rather than two independent alignments: on a phone in
                // landscape the top band is short enough that a top-aligned pill
                // and a bottom-aligned shelf header would sit on top of each other.
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxHeight()
                        .padding(end = 28.dp, top = 14.dp, bottom = 6.dp),
                ) {
                    StatusPill()

                    Spacer(modifier = Modifier.weight(1f))

                    ShelfHeader(
                        shelf = uiState.shelf,
                        onShelfChange = viewModel::onShelfChange,
                        onOpenLibrary = { onSeeAllPlatform(null) },
                        onNavigateToScanner = onNavigateToScanner,
                        onOpenSettings = onOpenSettings,
                    )
                }
            }

            GameRail(
                games = games,
                centeredIndex = centeredIndex ?: 0,
                listState = listState,
                railWidth = screenWidth,
                metrics = rail,
                onGameClick = onGameClick,
                modifier = Modifier.height(rail.slot),
            )

            Spacer(modifier = Modifier.height(DockSpace))
        }

        HomeDock(
            pageCount = games.size,
            activePage = centeredIndex ?: 0,
            onOpenSettings = onOpenSettings,
            onNavigateToScanner = onNavigateToScanner,
            onAddAndroidApps = onAddAndroidApps,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
                .padding(bottom = 18.dp),
        )
    }
}

/**
 * Full-bleed art for the focused game, crossfaded so scrolling the rail reads as
 * one continuous scene instead of a slideshow.
 *
 * Three scrims stack on top: a flat one to knock the art back, a vertical one so
 * the rail and dock keep their contrast, and a left-side one that buys the
 * wordmark a legible surface over bright artwork.
 */
@Composable
private fun ImmersiveBackground(game: Game?) {
    val artModel = game?.backgroundImagePath
        ?: game?.screenshotImagePath
        ?: game?.coverImagePath

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShellCanvas),
    ) {
        Crossfade(
            targetState = artModel,
            animationSpec = tween(durationMillis = 420),
            label = "homeBackdrop",
        ) { model ->
            if (!model.isNullOrBlank()) {
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        // A touch of blur keeps portrait cover art from competing
                        // with the wordmark when a game has no wide background art.
                        .blur(if (game?.backgroundImagePath.isNullOrBlank()) 18.dp else 0.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f)),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to Color.Black.copy(alpha = 0.35f),
                        1f to Color.Black.copy(alpha = 0.88f),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Black.copy(alpha = 0.72f),
                        0.55f to Color.Transparent,
                    ),
                ),
        )
    }
}

/**
 * The wordmark. Sized to fill [maxTitleHeight], then shrunk until it fits
 * [maxTitleWidth] on a single line - a fixed size would either clip "GRID
 * AUTOSPORT" or leave "GRID" looking undersized.
 */
@Composable
private fun GiantTitle(
    title: String,
    maxTitleHeight: Dp,
    maxTitleWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val text = title.uppercase()
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val fontSize = remember(text, maxTitleHeight, maxTitleWidth, density) {
        val maxWidthPx = with(density) { maxTitleWidth.toPx() }
        val minSizePx = with(density) { 34.dp.toPx() }
        var candidate = with(density) { maxTitleHeight.toPx() }
        while (candidate > minSizePx) {
            val measured = measurer.measure(
                text = AnnotatedString(text),
                style = TextStyle(
                    fontSize = with(density) { candidate.toSp() },
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.03).em,
                ),
                maxLines = 1,
                softWrap = false,
            )
            if (measured.size.width <= maxWidthPx) break
            candidate *= 0.92f
        }
        with(density) { candidate.coerceAtLeast(minSizePx).toSp() }
    }

    Text(
        text = text,
        color = Color.White,
        fontSize = fontSize,
        lineHeight = fontSize * 1.02f,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
        maxLines = 1,
        softWrap = false,
        modifier = modifier,
    )
}

/** "Recently Played" plus the overflow menu, sitting just above the rail. */
@Composable
private fun ShelfHeader(
    shelf: HomeShelf,
    onShelfChange: (HomeShelf) -> Unit,
    onOpenLibrary: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var shelfMenuOpen by remember { mutableStateOf(false) }
    var overflowOpen by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { shelfMenuOpen = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(shelf.labelRes()),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            DropdownMenu(expanded = shelfMenuOpen, onDismissRequest = { shelfMenuOpen = false }) {
                HomeShelf.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(stringResource(option.labelRes())) },
                        onClick = {
                            onShelfChange(option)
                            shelfMenuOpen = false
                        },
                    )
                }
            }
        }

        Box {
            LiquidGlassSurface(
                shape = CircleShape,
                style = GlassMaterials.thick,
                tint = ShellChrome,
                modifier = Modifier
                    .size(38.dp)
                    .clickable { overflowOpen = true },
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = stringResource(R.string.action_more_options),
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(20.dp),
                )
            }
            DropdownMenu(expanded = overflowOpen, onDismissRequest = { overflowOpen = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.home_menu_open_library)) },
                    onClick = {
                        overflowOpen = false
                        onOpenLibrary()
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.home_scan_roms)) },
                    onClick = {
                        overflowOpen = false
                        onNavigateToScanner()
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.nav_settings)) },
                    onClick = {
                        overflowOpen = false
                        onOpenSettings()
                    },
                )
            }
        }
    }
}

/**
 * Snap-scrolling rail of covers.
 *
 * The content padding centers the first and last cards, so every game can be
 * the focused one - without it the ends of the list could never reach the
 * middle of the screen.
 */
@Composable
private fun GameRail(
    games: List<Game>,
    centeredIndex: Int,
    listState: LazyListState,
    railWidth: Dp,
    metrics: RailMetrics,
    onGameClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val sidePadding = ((railWidth - metrics.slot) / 2).coerceAtLeast(16.dp)

    LazyRow(
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(listState),
        contentPadding = PaddingValues(horizontal = sidePadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        itemsIndexed(games, key = { _, game -> game.id }) { index, game ->
            CarouselCard(
                game = game,
                focused = index == centeredIndex,
                metrics = metrics,
                // A card that isn't centered yet gets pulled into focus first:
                // launching straight from the edge of the screen would make the
                // giant title and the game you opened disagree.
                onClick = {
                    if (index == centeredIndex) {
                        onGameClick(game.id)
                    } else {
                        scope.launch { listState.animateScrollToItem(index) }
                    }
                },
            )
        }
    }
}

@Composable
private fun CarouselCard(game: Game, focused: Boolean, metrics: RailMetrics, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val size by animateDpAsState(
        targetValue = if (focused) metrics.focused else metrics.resting,
        animationSpec = tween(durationMillis = 260),
        label = "cardSize",
    )
    val ringAlpha by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "cardRing",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(metrics.slot),
    ) {
        // Outer halo ring - the gap between it and the cover is what reads as a glow.
        Box(
            modifier = Modifier
                .size(size + 16.dp)
                .border(
                    width = 3.dp,
                    color = accent.copy(alpha = 0.55f * ringAlpha),
                    shape = RoundedCornerShape(metrics.radius + 8.dp),
                ),
        )
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (focused) 30.dp else 0.dp,
                    shape = RoundedCornerShape(metrics.radius),
                    ambientColor = accent,
                    spotColor = accent,
                )
                .clip(RoundedCornerShape(metrics.radius))
                .background(ShellChrome)
                .border(
                    width = if (focused) 4.dp else 2.5.dp,
                    color = if (focused) accent else accent.copy(alpha = 0.38f),
                    shape = RoundedCornerShape(metrics.radius),
                )
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        ) {
            val cover = game.coverImagePath
            if (!cover.isNullOrBlank()) {
                AsyncImage(
                    model = cover,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(metrics.radius - 5.dp)),
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.VideogameAsset,
                    contentDescription = game.title,
                    tint = Color.White.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(44.dp),
                )
            }
        }
    }
}

/** Floating pill: settings on the left, carousel position in the middle, add on the right. */
@Composable
private fun HomeDock(
    pageCount: Int,
    activePage: Int,
    onOpenSettings: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onAddAndroidApps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var addMenuOpen by remember { mutableStateOf(false) }

    LiquidGlassSurface(
        shape = CircleShape,
        style = GlassMaterials.thick,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.nav_settings),
                    tint = Color.White,
                )
            }

            PageDots(
                pageCount = pageCount,
                activePage = activePage,
                modifier = Modifier.padding(horizontal = 6.dp),
            )

            Box {
                IconButton(onClick = { addMenuOpen = true }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.action_add_games),
                        tint = Color.White,
                    )
                }
                DropdownMenu(expanded = addMenuOpen, onDismissRequest = { addMenuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.home_scan_roms)) },
                        onClick = {
                            addMenuOpen = false
                            onNavigateToScanner()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.home_add_android_apps)) },
                        onClick = {
                            addMenuOpen = false
                            onAddAndroidApps()
                        },
                    )
                }
            }
        }
    }
}

/**
 * Position indicator. Long libraries get a sliding window of [MAX_DOTS] rather
 * than one dot per game, so the dock keeps its shape at 3 games and at 30.
 */
@Composable
private fun PageDots(pageCount: Int, activePage: Int, modifier: Modifier = Modifier) {
    if (pageCount <= 1) {
        Spacer(modifier = modifier.size(width = 48.dp, height = 8.dp))
        return
    }
    val dots = minOf(pageCount, MAX_DOTS)
    val activeDot = if (pageCount <= MAX_DOTS) {
        activePage
    } else {
        (activePage.toFloat() / (pageCount - 1) * (dots - 1)).toInt()
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        repeat(dots) { index ->
            val active = index == activeDot
            Box(
                modifier = Modifier
                    .size(if (active) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = if (active) 0.95f else 0.28f)),
            )
        }
    }
}

private fun HomeShelf.labelRes(): Int = when (this) {
    HomeShelf.RECENTLY_PLAYED -> R.string.home_shelf_recently_played
    HomeShelf.RECENTLY_ADDED -> R.string.section_recently_added
    HomeShelf.FAVORITES -> R.string.filter_favorites
}
