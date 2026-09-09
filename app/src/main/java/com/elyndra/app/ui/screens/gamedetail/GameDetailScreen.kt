package com.elyndra.app.ui.screens.gamedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elyndra.app.R
import com.elyndra.app.ui.UiMessage
import com.elyndra.app.ui.resolve
import com.elyndra.app.ui.components.glass.GlassButton
import com.elyndra.app.ui.components.glass.GlassTopBar
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.util.formatFileSize
import com.elyndra.app.util.formatLastPlayed
import com.elyndra.app.util.formatPlaytime

@Composable
fun GameDetailScreen(
    gameId: Long,
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(gameId) { viewModel.load(gameId) }

    // Resolved up here rather than inside the effect: stringResource needs a
    // composable scope, and this is what makes the snackbar follow the language
    // picked in Settings.
    val launchErrorText = uiState.launchError?.resolve()
    val scrapeMessageText = uiState.scrapeMessage?.resolve()

    LaunchedEffect(launchErrorText) {
        launchErrorText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissLaunchError()
        }
    }
    LaunchedEffect(scrapeMessageText) {
        scrapeMessageText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissScrapeMessage()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = uiState.game?.title.orEmpty(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onToggleFavorite) {
                        Icon(
                            imageVector = if (uiState.game?.isFavorite == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.action_toggle_favorite),
                        )
                    }
                    IconButton(onClick = { showEditSheet = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } },
    ) { innerPadding ->
        val game = uiState.game
        if (game == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                if (uiState.isLoading) CircularProgressIndicator()
            }
            return@Scaffold
        }

        val backdrop = game.backgroundImagePath ?: game.coverImagePath
        if (!backdrop.isNullOrBlank()) {
            AsyncImage(
                model = backdrop,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.25f,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f)),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            LiquidGlassSurface(
                shape = RoundedCornerShape(GlassMetrics.panelRadius),
                style = GlassMaterials.thin,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .aspectRatio(3f / 4f)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp),
            ) {
                if (game.coverImagePath.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Filled.VideogameAsset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(56.dp),
                    )
                } else {
                    AsyncImage(
                        model = game.coverImagePath,
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Text(
                text = game.title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = listOfNotNull(uiState.platform?.displayName, game.genre).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            LiquidGlassSurface(
                shape = RoundedCornerShape(GlassMetrics.panelRadius),
                style = GlassMaterials.ultraThin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                ) {
                    StatColumn(stringResource(R.string.stat_playtime), formatPlaytime(game.totalPlaytimeSeconds))
                    StatColumn(stringResource(R.string.stat_last_played), formatLastPlayed(game.lastPlayedAt))
                    StatColumn(stringResource(R.string.stat_size), formatFileSize(game.fileSizeBytes))
                }
            }

            GlassButton(
                onClick = viewModel::onPlayClick,
                enabled = !uiState.isLaunching,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isLaunching) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Text(stringResource(R.string.action_play), modifier = Modifier.padding(start = 6.dp))
                }
            }

            GlassButton(
                onClick = viewModel::onRescrape,
                enabled = !uiState.isScraping,
                style = GlassMaterials.thin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            ) {
                if (uiState.isScraping) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Text(stringResource(R.string.action_rescrape), modifier = Modifier.padding(start = 6.dp))
                }
            }

            if (!game.description.isNullOrBlank()) {
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 20.dp),
                )
            }
        }
    }

    if (showEditSheet && uiState.game != null) {
        EditGameSheet(
            game = uiState.game!!,
            platforms = uiState.allPlatforms,
            onDismiss = { showEditSheet = false },
            onSave = { title, platformId, isHidden, coverImagePath, backgroundImagePath ->
                viewModel.onSaveEdits(title, platformId, isHidden, coverImagePath, backgroundImagePath)
                showEditSheet = false
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_body)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDelete()
                    showDeleteConfirm = false
                    onBack()
                }) { Text(stringResource(R.string.action_remove)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
