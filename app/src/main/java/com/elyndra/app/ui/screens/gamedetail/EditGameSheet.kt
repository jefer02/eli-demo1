package com.elyndra.app.ui.screens.gamedetail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.R
import com.elyndra.app.ui.components.glass.GlassButton
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.util.LocalImageStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGameSheet(
    game: Game,
    platforms: List<Platform>,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        platformId: String,
        isHidden: Boolean,
        coverImagePath: String?,
        backgroundImagePath: String?,
    ) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember(game.id) { mutableStateOf(game.title) }
    var platformId by remember(game.id) { mutableStateOf(game.platformId) }
    var isHidden by remember(game.id) { mutableStateOf(game.isHidden) }
    var coverImagePath by remember(game.id) { mutableStateOf(game.coverImagePath) }
    var backgroundImagePath by remember(game.id) { mutableStateOf(game.backgroundImagePath) }
    var isImportingCover by remember { mutableStateOf(false) }
    var isImportingBackground by remember { mutableStateOf(false) }
    var platformMenuExpanded by remember { mutableStateOf(false) }

    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        isImportingCover = true
        coroutineScope.launch {
            LocalImageStore.importImage(context, uri, "covers")?.let { coverImagePath = it }
            isImportingCover = false
        }
    }
    val backgroundPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        isImportingBackground = true
        coroutineScope.launch {
            LocalImageStore.importImage(context, uri, "backgrounds")?.let { backgroundImagePath = it }
            isImportingBackground = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = GlassMetrics.panelRadius, topEnd = GlassMetrics.panelRadius),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(stringResource(R.string.edit_game_title), style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.field_title)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )

            ExposedDropdownMenuBox(
                expanded = platformMenuExpanded,
                onExpandedChange = { platformMenuExpanded = it },
                modifier = Modifier.padding(top = 12.dp),
            ) {
                OutlinedTextField(
                    value = platforms.find { it.id == platformId }?.displayName ?: platformId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.field_platform)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = platformMenuExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(
                    expanded = platformMenuExpanded,
                    onDismissRequest = { platformMenuExpanded = false },
                ) {
                    platforms.forEach { platform ->
                        DropdownMenuItem(
                            text = { Text(platform.displayName) },
                            onClick = {
                                platformId = platform.id
                                platformMenuExpanded = false
                            },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                GlassButton(
                    onClick = { coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    style = GlassMaterials.thin,
                    modifier = Modifier.weight(1f),
                ) {
                    if (isImportingCover) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(if (coverImagePath.isNullOrBlank()) R.string.action_set_cover else R.string.action_change_cover))
                    }
                }
                GlassButton(
                    onClick = { backgroundPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    style = GlassMaterials.thin,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                ) {
                    if (isImportingBackground) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(if (backgroundImagePath.isNullOrBlank()) R.string.action_set_background else R.string.action_change_background))
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(stringResource(R.string.edit_hidden), modifier = Modifier.weight(1f))
                Switch(checked = isHidden, onCheckedChange = { isHidden = it })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
            ) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.action_cancel)) }
                GlassButton(
                    onClick = {
                        onSave(
                            title.trim().ifBlank { game.title },
                            platformId,
                            isHidden,
                            coverImagePath,
                            backgroundImagePath,
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                ) { Text(stringResource(R.string.action_save)) }
            }
        }
    }
}
