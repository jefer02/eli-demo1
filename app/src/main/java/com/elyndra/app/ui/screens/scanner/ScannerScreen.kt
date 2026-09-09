package com.elyndra.app.ui.screens.scanner

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.elyndra.app.R
import com.elyndra.app.ui.components.EmptyState
import com.elyndra.app.ui.components.glass.GlassButton
import com.elyndra.app.ui.components.glass.GlassTopBar
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics

@Composable
fun ScannerScreen(
    onBack: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
        )
        val displayName = DocumentFile.fromTreeUri(context, uri)?.name ?: uri.toString()
        viewModel.onAddFolder(uri.toString(), displayName)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = stringResource(R.string.scanner_title),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            if (uiState.romFolders.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.FolderOff,
                    title = stringResource(R.string.scanner_empty_title),
                    subtitle = stringResource(R.string.scanner_empty_subtitle),
                    actionLabel = stringResource(R.string.action_choose_folder),
                    onAction = { folderPicker.launch(null) },
                    modifier = Modifier.weight(1f),
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.romFolders, key = { it.id }) { folder ->
                        ListItem(
                            headlineContent = { Text(folder.displayPath) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        )
                    }
                }
                GlassButton(
                    onClick = { folderPicker.launch(null) },
                    style = GlassMaterials.thin,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.CreateNewFolder, contentDescription = null)
                    Text(stringResource(R.string.action_add_another_folder), modifier = Modifier.padding(start = 6.dp))
                }

                GlassButton(
                    onClick = viewModel::onStartScan,
                    enabled = !uiState.isScanning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                ) {
                    if (uiState.isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.action_start_scan))
                    }
                }

                if (uiState.isScanning) {
                    Text(
                        text = stringResource(
                            R.string.scanner_progress,
                            uiState.currentFolder.orEmpty(),
                            uiState.filesFoundSoFar,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                if (uiState.isAutoScraping) {
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text(
                            stringResource(R.string.scanner_fetching_metadata),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }

                uiState.lastResult?.let { result ->
                    LiquidGlassSurface(
                        shape = RoundedCornerShape(GlassMetrics.panelRadius),
                        style = GlassMaterials.regular,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.scanner_complete), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.scanner_files_scanned, result.filesScanned))
                            Text(stringResource(R.string.scanner_games_added, result.gamesAdded))
                            Text(stringResource(R.string.scanner_games_updated, result.gamesUpdated))
                            if (result.unrecognizedFiles > 0) {
                                Text(stringResource(R.string.scanner_unrecognized, result.unrecognizedFiles))
                            }
                        }
                    }
                }
            }
        }
    }
}
