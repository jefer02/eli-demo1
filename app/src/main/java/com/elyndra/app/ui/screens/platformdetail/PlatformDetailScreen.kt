package com.elyndra.app.ui.screens.platformdetail

import androidx.annotation.StringRes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elyndra.app.domain.model.InstalledAndroidApp
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.SortOrder
import com.elyndra.app.R
import com.elyndra.app.ui.components.EmptyState
import com.elyndra.app.ui.components.EmulatorPickerDialog
import com.elyndra.app.ui.components.GameCarousel
import com.elyndra.app.ui.components.SectionHeader
import com.elyndra.app.ui.components.glass.GlassButton
import com.elyndra.app.ui.components.glass.GlassTopBar
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics

@Composable
fun PlatformDetailScreen(
    platformId: String,
    onBack: () -> Unit,
    onGameClick: (Long) -> Unit,
    viewModel: PlatformDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(platformId) { viewModel.setPlatformId(platformId) }

    var showAddAppsDialog by remember { mutableStateOf(false) }
    var showEmulatorDialog by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val heroArt = remember(uiState.games) {
        uiState.games.firstNotNullOfOrNull { it.backgroundImagePath?.takeIf(String::isNotBlank) }
            ?: uiState.games.firstNotNullOfOrNull { it.coverImagePath?.takeIf(String::isNotBlank) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = uiState.platform?.displayName ?: "",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    Box {
                        TextButton(onClick = { sortMenuExpanded = true }) {
                            Text(stringResource(uiState.sortOrder.labelRes()))
                        }
                        DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(order.labelRes())) },
                                    onClick = {
                                        viewModel.onSortOrderChange(order)
                                        sortMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 8.dp),
        ) {
            val platform = uiState.platform
            if (platform != null) {
                PlatformHero(
                    platform = platform,
                    gameCount = uiState.games.size,
                    heroArt = heroArt,
                    isAndroidPlatform = uiState.isAndroidPlatform,
                    onPrimaryAction = {
                        if (uiState.isAndroidPlatform) showAddAppsDialog = true else showEmulatorDialog = true
                    },
                )
            }

            if (!uiState.isLoading && uiState.games.isEmpty()) {
                EmptyState(
                    icon = if (uiState.isAndroidPlatform) Icons.Filled.Apps else Icons.Filled.VideogameAsset,
                    title = stringResource(
                        if (uiState.isAndroidPlatform) R.string.platform_empty_apps_title else R.string.platform_empty_games_title,
                    ),
                    subtitle = stringResource(
                        if (uiState.isAndroidPlatform) {
                            R.string.platform_empty_apps_subtitle
                        } else {
                            R.string.platform_empty_games_subtitle
                        },
                    ),
                    actionLabel = if (uiState.isAndroidPlatform) stringResource(R.string.action_add_apps) else null,
                    onAction = if (uiState.isAndroidPlatform) {
                        { showAddAppsDialog = true }
                    } else {
                        null
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )
            } else {
                SectionHeader(stringResource(if (uiState.isAndroidPlatform) R.string.section_apps else R.string.section_games))
                GameCarousel(games = uiState.games, onGameClick = onGameClick)
            }
        }
    }

    if (showAddAppsDialog) {
        AddNativeAppsDialog(
            apps = uiState.launchableApps,
            alreadyAdded = remember(uiState.games) { uiState.games.filter { it.isNativeApp }.map { it.romUri }.toSet() },
            onDismiss = { showAddAppsDialog = false },
            onAddApp = viewModel::onAddNativeApp,
        )
    }

    val platformForEmulatorDialog = uiState.platform
    if (showEmulatorDialog && platformForEmulatorDialog != null) {
        EmulatorPickerDialog(
            platform = platformForEmulatorDialog,
            installedEmulators = uiState.installedEmulators,
            onDismiss = { showEmulatorDialog = false },
            onSelect = { packageName -> viewModel.onSetEmulator(packageName) },
        )
    }
}

@Composable
private fun PlatformHero(
    platform: Platform,
    gameCount: Int,
    heroArt: String?,
    isAndroidPlatform: Boolean,
    onPrimaryAction: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(GlassMetrics.panelRadius)),
    ) {
        if (!heroArt.isNullOrBlank()) {
            AsyncImage(
                model = heroArt,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))),
                    ),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }

        val textColor = if (heroArt.isNullOrBlank()) MaterialTheme.colorScheme.onSurface else Color.White
        val subtitleColor = if (heroArt.isNullOrBlank()) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            Color.White.copy(alpha = 0.85f)
        }

        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text(
                text = platform.displayName.uppercase(),
                color = textColor,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = pluralStringResource(
                    id = if (isAndroidPlatform) R.plurals.app_count else R.plurals.game_count,
                    count = gameCount,
                    gameCount,
                ),
                color = subtitleColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        GlassButton(
            onClick = onPrimaryAction,
            style = GlassMaterials.thick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        ) {
            Icon(
                imageVector = if (isAndroidPlatform) Icons.Filled.Add else Icons.Filled.SportsEsports,
                contentDescription = null,
                tint = Color.White,
            )
            Text(
                text = stringResource(if (isAndroidPlatform) R.string.action_add_apps else R.string.action_emulator),
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun AddNativeAppsDialog(
    apps: List<InstalledAndroidApp>,
    alreadyAdded: Set<String>,
    onDismiss: () -> Unit,
    onAddApp: (InstalledAndroidApp) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(apps, query) {
        if (query.isBlank()) apps else apps.filter { it.label.contains(query, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_apps_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(stringResource(R.string.add_apps_search)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (filtered.isEmpty()) {
                    Text(
                        text = stringResource(R.string.add_apps_none),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 360.dp)
                            .padding(top = 8.dp),
                    ) {
                        items(filtered, key = { it.packageName }) { app ->
                            val added = app.packageName in alreadyAdded
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                            ) {
                                Text(app.label, modifier = Modifier.weight(1f))
                                if (added) {
                                    Text(
                                        text = stringResource(R.string.state_added),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                } else {
                                    TextButton(onClick = { onAddApp(app) }) { Text(stringResource(R.string.action_add)) }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_done)) }
        },
    )
}

@StringRes
private fun SortOrder.labelRes(): Int = when (this) {
    SortOrder.NAME -> R.string.sort_name
    SortOrder.DATE_ADDED -> R.string.sort_date_added
    SortOrder.LAST_PLAYED -> R.string.sort_last_played
    SortOrder.PLAYTIME -> R.string.sort_playtime
}
