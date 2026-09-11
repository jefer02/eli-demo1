package com.elyndra.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elyndra.app.R
import com.elyndra.app.ui.components.ArtworkTile
import com.elyndra.app.ui.components.EmptyState
import com.elyndra.app.ui.components.clickableNoRipple
import com.elyndra.app.ui.components.glass.DarkGlassPanel
import com.elyndra.app.ui.components.glass.GlassPanel
import com.elyndra.app.ui.theme.ElyndraInk
import com.elyndra.app.ui.theme.ElyndraInkMuted
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.ui.theme.LocalGlass
import com.elyndra.app.util.formatPlaytime

/**
 * Below this the shell is a phone in landscape - roughly 412dp tall - and the
 * hero has to give up most of its height to leave the rail and dock room.
 */
private val CompactHeightThreshold = 560.dp

/** Hero, card and type sizes, which differ enough between the two shapes to name. */
private data class ShellMetrics(
    val heroHeight: Dp,
    val tile: Dp,
    val pad: Dp,
    val heroTitleSize: TextUnit,
    val wordmarkSize: TextUnit,
) {
    /** Folder cards are landscape-ish; an Android app is a square icon. */
    val consoleWidth: Dp get() = tile * 1.5f
    val appWidth: Dp get() = tile
}

private fun shellMetricsFor(height: Dp): ShellMetrics = if (height < CompactHeightThreshold) {
    ShellMetrics(heroHeight = 132.dp, tile = 66.dp, pad = 22.dp, heroTitleSize = 34.sp, wordmarkSize = 17.sp)
} else {
    ShellMetrics(heroHeight = 330.dp, tile = 96.dp, pad = 18.dp, heroTitleSize = 40.sp, wordmarkSize = 19.sp)
}

/**
 * The home shell: a hero showing whichever card is selected, one unified rail of
 * emulator folders and Android games below it, and a dock.
 *
 * Selection is explicit rather than derived from scroll position. The design
 * asks for "one tap selects, double tap opens", so scrolling past a card must
 * not swap the hero out from under someone who is only browsing.
 */
@Composable
fun HomeScreen(
    onOpenFolder: (platformId: String) -> Unit,
    onLaunchApp: (gameId: Long) -> Unit,
    onAddGames: () -> Unit,
    onOpenAssistant: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val open: (LibraryEntry) -> Unit = { entry ->
        when (entry) {
            is LibraryEntry.ConsoleFolder -> onOpenFolder(entry.platform.id)
            is LibraryEntry.AndroidApp -> onLaunchApp(entry.game.id)
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val metrics = shellMetricsFor(maxHeight)

        Column(Modifier.fillMaxSize()) {
            Hero(
                selected = state.selected,
                metrics = metrics,
                query = state.query,
                isSearchOpen = state.isSearchOpen,
                onQueryChange = viewModel::onQueryChange,
                onToggleSearch = viewModel::onToggleSearch,
                onOpenSettings = onOpenSettings,
            )

            if (state.isLibraryEmpty) {
                EmptyState(
                    icon = Icons.Filled.VideogameAsset,
                    title = stringResource(R.string.home_empty_title),
                    subtitle = stringResource(R.string.home_empty_subtitle),
                    actionLabel = stringResource(R.string.home_scan_roms),
                    onAction = onAddGames,
                    modifier = Modifier.weight(1f),
                )
            } else {
                Shelf(
                    state = state,
                    metrics = metrics,
                    onFilterChange = viewModel::onFilterChange,
                    onSelect = viewModel::onSelect,
                    onOpen = open,
                    modifier = Modifier.weight(1f),
                )
            }

            Dock(
                metrics = metrics,
                onAddGames = onAddGames,
                onOpenSelected = { state.selected?.let(open) },
                onOpenAssistant = onOpenAssistant,
                enabled = state.selected != null,
            )
        }
    }
}

/* ─────────────────────────── hero ─────────────────────────── */

@Composable
private fun Hero(
    selected: LibraryEntry?,
    metrics: ShellMetrics,
    query: String,
    isSearchOpen: Boolean,
    onQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val glass = LocalGlass.current
    val scrim = glass.heroScrim

    Box(
        Modifier
            .fillMaxWidth()
            .height(metrics.heroHeight)
            .background(ElyndraInk),
    ) {
        ArtworkTile(
            key = selected?.name?.takeIf { it.isNotBlank() } ?: "Elyndra",
            imagePath = selected?.coverPath(),
            // Overscaled so the art bleeds past the edges instead of showing seams.
            modifier = Modifier.fillMaxSize().scale(1.15f),
        )

        // Dark at the top for the bar, darkest at the bottom for the title.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to ElyndraInk.copy(alpha = scrim * 0.85f),
                        0.42f to ElyndraInk.copy(alpha = scrim * 0.35f),
                        1f to ElyndraInk.copy(alpha = (scrim + 0.30f).coerceAtMost(0.96f)),
                    ),
                ),
        )

        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = metrics.pad, end = metrics.pad, top = 8.dp),
            ) {
                Box(
                    Modifier
                        .size(9.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(glass.accent.light),
                )
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    color = Color.White,
                    fontSize = metrics.wordmarkSize,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.16.em,
                    maxLines = 1,
                )
                Spacer(Modifier.weight(1f))
                HeroSearch(
                    query = query,
                    isOpen = isSearchOpen,
                    onQueryChange = onQueryChange,
                    onToggle = onToggleSearch,
                )
                DarkGlassPanel(Modifier.size(34.dp).clickableNoRipple(onClick = onOpenSettings)) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.nav_settings),
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center).size(16.dp),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Column(Modifier.padding(start = metrics.pad, end = metrics.pad, bottom = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(selected.kindLabel()),
                        color = glass.accent.onAccent,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.16.em,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(glass.accent.gradient(120f))
                            .padding(horizontal = 9.dp, vertical = 4.dp),
                    )
                    Text(
                        text = selected.metaLabel(),
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.1.em,
                    )
                }
                Text(
                    text = (selected?.name ?: stringResource(R.string.home_empty_title)).uppercase(),
                    color = Color.White,
                    fontSize = metrics.heroTitleSize,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.03).em,
                    lineHeight = metrics.heroTitleSize * 0.92f,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp),
                )
                Text(
                    text = stringResource(R.string.home_tap_hint),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.14.em,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

/** The search field slides out of its own button rather than replacing the bar. */
@Composable
private fun HeroSearch(
    query: String,
    isOpen: Boolean,
    onQueryChange: (String) -> Unit,
    onToggle: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(34.dp),
    ) {
        AnimatedVisibility(
            visible = isOpen,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            DarkGlassPanel(Modifier.height(34.dp)) {
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 11.dp)
                        .width(150.dp),
                    decorationBox = { field ->
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.action_search),
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                            )
                        }
                        field()
                    },
                )
            }
        }
        DarkGlassPanel(Modifier.size(34.dp).clickableNoRipple(onClick = onToggle)) {
            Icon(
                Icons.Filled.Search,
                contentDescription = stringResource(R.string.action_search),
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).size(16.dp),
            )
        }
    }
}

/* ─────────────────────────── shelf ─────────────────────────── */

@Composable
private fun Shelf(
    state: HomeUiState,
    metrics: ShellMetrics,
    onFilterChange: (LibraryFilter) -> Unit,
    onSelect: (String) -> Unit,
    onOpen: (LibraryEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(top = 10.dp)) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = metrics.pad, end = metrics.pad, bottom = 6.dp),
        ) {
            LibraryFilter.entries.forEach { filter ->
                FilterTab(
                    label = stringResource(filter.labelRes()),
                    selected = state.filter == filter,
                    onClick = { onFilterChange(filter) },
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.home_item_count, state.entries.size).uppercase(),
                color = ElyndraInkMuted.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.18.em,
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = metrics.pad, vertical = 6.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(state.entries, key = { it.key }) { entry ->
                RailCard(
                    entry = entry,
                    metrics = metrics,
                    selected = entry.key == state.selected?.key,
                    onSelect = { onSelect(entry.key) },
                    onOpen = { onOpen(entry) },
                )
            }
        }
    }
}

/** A text tab with the accent underline the design draws beneath the active one. */
@Composable
private fun FilterTab(label: String, selected: Boolean, onClick: () -> Unit) {
    val glass = LocalGlass.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickableNoRipple(onClick = onClick).padding(horizontal = 9.dp),
    ) {
        Text(
            text = label,
            color = if (selected) ElyndraInk else ElyndraInkMuted.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp, bottom = 5.dp),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) glass.accent.gradient(90f) else SolidColor(Color.Transparent)),
        )
    }
}

/**
 * One card on the rail. A single tap selects it - moving the hero - and a
 * double tap opens it, which is the whole interaction model of the shell.
 */
@Composable
private fun RailCard(
    entry: LibraryEntry,
    metrics: ShellMetrics,
    selected: Boolean,
    onSelect: () -> Unit,
    onOpen: () -> Unit,
) {
    val glass = LocalGlass.current
    val lift by animateDpAsState(if (selected) (-6).dp else 0.dp, tween(300), label = "lift")
    val width = when (entry) {
        is LibraryEntry.ConsoleFolder -> metrics.consoleWidth
        is LibraryEntry.AndroidApp -> metrics.appWidth
    }
    val shape = RoundedCornerShape(GlassMetrics.cardRadius)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .width(width)
            .padding(top = 8.dp)
            .offset(y = lift)
            .pointerInput(entry.key) {
                detectTapGestures(onTap = { onSelect() }, onDoubleTap = { onOpen() })
            },
    ) {
        ArtworkTile(
            key = entry.name,
            imagePath = entry.coverPath(),
            contentDescription = entry.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(metrics.tile)
                .clip(shape)
                .border(
                    width = if (selected) 2.5.dp else 1.dp,
                    color = if (selected) glass.accent.light else Color.White.copy(alpha = 0.6f),
                    shape = shape,
                ),
        ) {
            when (entry) {
                is LibraryEntry.ConsoleFolder -> ConsoleFace(entry, metrics)
                is LibraryEntry.AndroidApp -> Text(
                    text = entry.initials,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        Text(
            text = entry.name,
            color = if (selected) ElyndraInk else ElyndraInkMuted,
            fontSize = 9.5.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** A folder card carries its own name plate: the art behind it is one of its ROMs. */
@Composable
private fun BoxScope.ConsoleFace(entry: LibraryEntry.ConsoleFolder, metrics: ShellMetrics) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(ElyndraInk.copy(alpha = 0.55f), ElyndraInk.copy(alpha = 0.12f)),
                ),
            ),
    )
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
    ) {
        Text(
            text = entry.platform.shortName.take(3).uppercase(),
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.2.em,
        )
        Text(
            text = entry.platform.shortName.uppercase(),
            color = Color.White,
            fontSize = if (metrics.tile < 80.dp) 16.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = entry.countLabel(),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.08.em,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/* ─────────────────────────── dock ─────────────────────────── */

@Composable
private fun Dock(
    metrics: ShellMetrics,
    onAddGames: () -> Unit,
    onOpenSelected: () -> Unit,
    onOpenAssistant: () -> Unit,
    enabled: Boolean,
) {
    val glass = LocalGlass.current
    val shape = RoundedCornerShape(GlassMetrics.buttonRadius)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding()
            .padding(start = metrics.pad, end = metrics.pad, bottom = 10.dp),
    ) {
        GlassPanel(
            shape = shape,
            modifier = Modifier.height(42.dp).clickableNoRipple(onClick = onAddGames),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 14.dp),
            ) {
                Box(
                    Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color.White),
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = glass.accent.deep,
                        modifier = Modifier.align(Alignment.Center).size(14.dp),
                    )
                }
                Text(
                    text = stringResource(R.string.home_add_games),
                    color = ElyndraInk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }

        Box(
            Modifier
                .weight(1f)
                .height(42.dp)
                .clip(shape)
                .background(glass.accent.gradient())
                .clickableNoRipple(enabled = enabled, onClick = onOpenSelected),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.align(Alignment.Center),
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = glass.accent.onAccent,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(R.string.action_open),
                    color = glass.accent.onAccent,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        AssistantButton(onClick = onOpenAssistant)
    }
}

/** Lucy's way in: a glass square with an accent ring pulsing out of it. */
@Composable
private fun AssistantButton(onClick: () -> Unit) {
    val glass = LocalGlass.current
    val transition = rememberInfiniteTransition(label = "assistant")
    val ring by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Restart),
        label = "ring",
    )
    val shape = RoundedCornerShape(16.dp)

    Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxSize()
                .scale(1f + 0.85f * ring)
                .border(1.5.dp, glass.accent.light.copy(alpha = 0.5f * (1f - ring)), shape),
        )
        GlassPanel(
            shape = shape,
            modifier = Modifier.fillMaxSize().clickableNoRipple(onClick = onClick),
        ) {
            Text(
                text = stringResource(R.string.assistant_initial),
                color = glass.accent.deep,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

/* ─────────────────────────── labels ─────────────────────────── */

private fun LibraryFilter.labelRes(): Int = when (this) {
    LibraryFilter.ALL -> R.string.library_filter_all
    LibraryFilter.ANDROID -> R.string.library_filter_android
    LibraryFilter.CONSOLES -> R.string.library_filter_consoles
}

private fun LibraryEntry?.kindLabel(): Int = when (this) {
    is LibraryEntry.ConsoleFolder -> R.string.entry_kind_folder
    is LibraryEntry.AndroidApp -> R.string.entry_kind_android_app
    null -> R.string.entry_kind_empty
}

private fun LibraryEntry.coverPath(): String? = when (this) {
    is LibraryEntry.ConsoleFolder -> coverPath
    is LibraryEntry.AndroidApp -> game.coverImagePath
}

@Composable
private fun LibraryEntry?.metaLabel(): String = when (this) {
    is LibraryEntry.ConsoleFolder -> countLabel()
    is LibraryEntry.AndroidApp -> formatPlaytime(game.totalPlaytimeSeconds)
    null -> ""
}

@Composable
private fun LibraryEntry.ConsoleFolder.countLabel(): String {
    val roms = stringResource(R.string.entry_rom_count, romCount)
    return emulatorName?.let { "$roms · $it" } ?: roms
}
