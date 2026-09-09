package com.elyndra.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.elyndra.app.BuildConfig
import com.elyndra.app.R
import com.elyndra.app.domain.model.AccentColor
import com.elyndra.app.domain.model.AppLanguage
import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.model.MetadataSourceId
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.RomFolder
import com.elyndra.app.domain.model.ThemeMode
import com.elyndra.app.ui.UiMessage
import com.elyndra.app.ui.resolve
import com.elyndra.app.ui.components.EmulatorPickerDialog
import com.elyndra.app.ui.components.glass.GlassButton
import com.elyndra.app.ui.components.glass.LiquidGlassSurface
import com.elyndra.app.ui.theme.GlassMaterials
import com.elyndra.app.ui.theme.GlassMetrics
import com.elyndra.app.ui.theme.palette
import com.elyndra.app.util.Constants

private enum class SettingsCategory(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val icon: ImageVector,
) {
    PERSONALIZATION(R.string.settings_personalization, R.string.settings_personalization_desc, Icons.Filled.Palette),
    ROM_FOLDERS(R.string.settings_rom_folders, R.string.settings_rom_folders_desc, Icons.Filled.Folder),
    EMULATORS(R.string.settings_emulators, R.string.settings_emulators_desc, Icons.Filled.SportsEsports),
    SCRAPING(R.string.settings_scraping, R.string.settings_scraping_desc, Icons.Filled.CloudDownload),
    INTEGRATIONS(R.string.settings_integrations, R.string.settings_integrations_desc, Icons.Filled.Extension),
    ABOUT(R.string.settings_about, R.string.settings_about_desc, Icons.Filled.Info),
}

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.LIGHT -> R.string.theme_light
    ThemeMode.DARK -> R.string.theme_dark
    ThemeMode.SYSTEM -> R.string.theme_system
}

private fun ThemeMode.icon(): ImageVector = when (this) {
    ThemeMode.LIGHT -> Icons.Filled.LightMode
    ThemeMode.DARK -> Icons.Filled.DarkMode
    ThemeMode.SYSTEM -> Icons.Filled.BrightnessAuto
}

@StringRes
private fun AppLanguage.labelRes(): Int = when (this) {
    AppLanguage.SYSTEM -> R.string.language_system
    AppLanguage.ENGLISH -> R.string.language_english
    AppLanguage.SPANISH -> R.string.language_spanish
}

@StringRes
private fun AccentColor.labelRes(): Int = when (this) {
    AccentColor.CORAL -> R.string.accent_coral
    AccentColor.AMBER -> R.string.accent_amber
    AccentColor.LIME -> R.string.accent_lime
    AccentColor.EMERALD -> R.string.accent_emerald
    AccentColor.SKY -> R.string.accent_sky
    AccentColor.INDIGO -> R.string.accent_indigo
    AccentColor.VIOLET -> R.string.accent_violet
    AccentColor.MAGENTA -> R.string.accent_magenta
    AccentColor.SLATE -> R.string.accent_slate
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToScanner: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedCategory by rememberSaveable { mutableStateOf(SettingsCategory.PERSONALIZATION) }
    var platformForPicker by remember { mutableStateOf<Platform?>(null) }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
        )
        val displayName = DocumentFile.fromTreeUri(context, uri)?.name ?: uri.toString()
        viewModel.onAddFolder(uri.toString(), displayName)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        LiquidGlassSurface(
            shape = RoundedCornerShape(GlassMetrics.panelRadius),
            style = GlassMaterials.regular,
            modifier = Modifier
                .fillMaxHeight()
                .width(300.dp)
                .padding(end = 12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 20.dp),
                ) {
                    LiquidGlassSurface(
                        shape = CircleShape,
                        style = GlassMaterials.thin,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(onClick = onBack),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(20.dp),
                        )
                    }
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    SettingsCategory.entries.forEach { category ->
                        SettingsCategoryRow(
                            category = category,
                            selected = category == selectedCategory,
                            onClick = { selectedCategory = category },
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (selectedCategory) {
                SettingsCategory.PERSONALIZATION -> PersonalizationContent(
                    themeMode = uiState.preferences.themeMode,
                    accentColor = uiState.preferences.accentColor,
                    language = uiState.preferences.language,
                    gridColumns = uiState.preferences.gridColumns,
                    showHiddenGames = uiState.preferences.showHiddenGames,
                    onThemeModeChange = viewModel::onThemeModeChange,
                    onAccentColorChange = viewModel::onAccentColorChange,
                    onLanguageChange = viewModel::onLanguageChange,
                    onGridColumnsChange = viewModel::onGridColumnsChange,
                    onShowHiddenChange = viewModel::onShowHiddenChange,
                )
                SettingsCategory.ROM_FOLDERS -> RomFoldersContent(
                    folders = uiState.romFolders,
                    onAddFolder = { folderPicker.launch(null) },
                    onRemoveFolder = viewModel::onRemoveFolder,
                    onScanNow = onNavigateToScanner,
                )
                SettingsCategory.EMULATORS -> EmulatorsContent(
                    platforms = uiState.platforms,
                    installedEmulators = uiState.installedEmulators,
                    onChangeClick = { platformForPicker = it },
                )
                SettingsCategory.SCRAPING -> ScrapingContent(
                    credentials = uiState.credentials,
                    autoScrapeOnScan = uiState.preferences.autoScrapeOnScan,
                    isRescraping = uiState.isRescrapingLibrary,
                    progressMessage = uiState.rescrapeProgressMessage,
                    onAutoScrapeChange = viewModel::onAutoScrapeChange,
                    onRescrapeLibrary = viewModel::onRescrapeLibrary,
                )
                SettingsCategory.INTEGRATIONS -> IntegrationsContent(
                    credentials = uiState.credentials,
                    onUpdateScreenScraper = viewModel::onUpdateScreenScraperCredentials,
                    onUpdateIgdb = viewModel::onUpdateIgdbCredentials,
                    onUpdateSteamGridDb = viewModel::onUpdateSteamGridDbCredentials,
                    onUpdateRetroAchievements = viewModel::onUpdateRetroAchievementsCredentials,
                )
                SettingsCategory.ABOUT -> AboutContent()
            }
        }
    }

    val currentPlatform = platformForPicker
    if (currentPlatform != null) {
        EmulatorPickerDialog(
            platform = currentPlatform,
            installedEmulators = uiState.installedEmulators,
            onDismiss = { platformForPicker = null },
            onSelect = { packageName ->
                viewModel.onSetPlatformEmulator(currentPlatform.id, packageName)
                platformForPicker = null
            },
        )
    }
}

@Composable
private fun SettingsCategoryRow(category: SettingsCategory, selected: Boolean, onClick: () -> Unit) {
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        label = "categoryContent",
    )
    val pillColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent,
        label = "categoryPill",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
        label = "categoryBorder",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(pillColor)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Icon(category.icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = stringResource(category.title),
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
            Text(
                text = stringResource(category.subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsPanel(content: @Composable ColumnScope.() -> Unit) {
    LiquidGlassSurface(
        shape = RoundedCornerShape(GlassMetrics.panelRadius),
        style = GlassMaterials.ultraThin,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(content = content)
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailing()
    }
}

/**
 * A row whose control is too wide to sit beside the label, so it drops onto its
 * own line underneath - the accent swatches and the columns stepper both need it.
 */
@Composable
private fun SettingsStackedRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(22.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            content()
        }
    }
}

@Composable
private fun PersonalizationContent(
    themeMode: ThemeMode,
    accentColor: AccentColor,
    language: AppLanguage,
    gridColumns: Int,
    showHiddenGames: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAccentColorChange: (AccentColor) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onGridColumnsChange: (Int) -> Unit,
    onShowHiddenChange: (Boolean) -> Unit,
) {
    SettingsPanel {
        SettingsRow(
            icon = Icons.Filled.Palette,
            title = stringResource(R.string.pref_theme),
            subtitle = stringResource(R.string.pref_theme_desc),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ThemeMode.entries.forEach { mode ->
                    ThemeSwatch(mode = mode, current = themeMode, onSelect = onThemeModeChange)
                }
            }
        }
        RowDivider()
        SettingsStackedRow(
            icon = Icons.Filled.ColorLens,
            title = stringResource(R.string.pref_accent),
            subtitle = stringResource(R.string.pref_accent_desc),
        ) {
            AccentSwatchRow(selected = accentColor, onSelect = onAccentColorChange)
        }
        RowDivider()
        SettingsRow(
            icon = Icons.Filled.Language,
            title = stringResource(R.string.pref_language),
            subtitle = stringResource(R.string.pref_language_desc),
        ) {
            SettingsDropdown(
                label = stringResource(language.labelRes()),
                options = AppLanguage.entries,
                optionLabel = { stringResource(it.labelRes()) },
                onSelect = onLanguageChange,
            )
        }
        RowDivider()
        SettingsStackedRow(
            icon = Icons.Filled.GridView,
            title = stringResource(R.string.pref_grid_columns),
            subtitle = stringResource(R.string.pref_grid_columns_desc),
        ) {
            StepperSlider(
                value = gridColumns,
                range = Constants.MIN_GRID_COLUMNS..Constants.MAX_GRID_COLUMNS,
                onValueChange = onGridColumnsChange,
            )
        }
        RowDivider()
        SettingsRow(
            icon = Icons.Filled.VideogameAsset,
            title = stringResource(R.string.pref_show_hidden),
            subtitle = stringResource(R.string.pref_show_hidden_desc),
        ) {
            Switch(checked = showHiddenGames, onCheckedChange = onShowHiddenChange)
        }
    }
}

@Composable
private fun ThemeSwatch(mode: ThemeMode, current: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    val selected = mode == current
    LiquidGlassSurface(
        shape = CircleShape,
        style = if (selected) GlassMaterials.thick else GlassMaterials.thin,
        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .size(44.dp)
            .clickable(onClick = { onSelect(mode) }),
    ) {
        Icon(
            imageVector = mode.icon(),
            contentDescription = stringResource(mode.labelRes()),
            tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Center)
                .size(20.dp),
        )
    }
}

/** The accent picker: one filled circle per [AccentColor], check mark on the active one. */
@Composable
private fun AccentSwatchRow(selected: AccentColor, onSelect: (AccentColor) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AccentColor.entries.forEach { accent ->
            val isSelected = accent == selected
            val palette = accent.palette()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.swatch)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        },
                        shape = CircleShape,
                    )
                    .clickable(onClick = { onSelect(accent) }),
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(accent.labelRes()),
                        tint = palette.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

/** Glass pill that opens a menu - the dropdown look the reference shell uses for enum settings. */
@Composable
private fun <T> SettingsDropdown(
    label: String,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        GlassButton(onClick = { expanded = true }, style = GlassMaterials.thin) {
            Text(label)
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Slider flanked by -/+ so it stays usable with a d-pad, not just a touch drag. */
@Composable
private fun StepperSlider(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(28.dp),
        )
        IconButton(
            onClick = { onValueChange((value - 1).coerceIn(range.first, range.last)) },
            enabled = value > range.first,
        ) {
            Icon(Icons.Filled.Remove, contentDescription = null)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = { onValueChange((value + 1).coerceIn(range.first, range.last)) },
            enabled = value < range.last,
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
        }
    }
}

@Composable
private fun RomFoldersContent(
    folders: List<RomFolder>,
    onAddFolder: () -> Unit,
    onRemoveFolder: (Long) -> Unit,
    onScanNow: () -> Unit,
) {
    SettingsPanel {
        if (folders.isEmpty()) {
            SettingsRow(
                icon = Icons.Filled.Folder,
                title = stringResource(R.string.rom_folders_empty),
                subtitle = stringResource(R.string.rom_folders_empty_desc),
            ) {}
        } else {
            folders.forEachIndexed { index, folder ->
                ListItem(
                    headlineContent = { Text(folder.displayPath) },
                    trailingContent = {
                        IconButton(onClick = { onRemoveFolder(folder.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_remove_folder))
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
                if (index != folders.lastIndex) RowDivider()
            }
        }
    }
    Row(modifier = Modifier.padding(horizontal = 4.dp)) {
        GlassButton(onClick = onAddFolder, style = GlassMaterials.thin) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text(stringResource(R.string.action_add_folder), modifier = Modifier.padding(start = 6.dp))
        }
        GlassButton(onClick = onScanNow, enabled = folders.isNotEmpty(), modifier = Modifier.padding(start = 8.dp)) {
            Text(stringResource(R.string.action_scan_now))
        }
    }
}

@Composable
private fun EmulatorsContent(
    platforms: List<Platform>,
    installedEmulators: List<InstalledEmulatorApp>,
    onChangeClick: (Platform) -> Unit,
) {
    SettingsPanel {
        platforms.forEachIndexed { index, platform ->
            ListItem(
                headlineContent = { Text(platform.displayName) },
                supportingContent = {
                    val label = platform.emulatorPackageName?.let { pkg ->
                        installedEmulators.find { it.packageName == pkg }?.label ?: pkg
                    } ?: stringResource(R.string.emulator_not_configured)
                    Text(label)
                },
                trailingContent = {
                    TextButton(onClick = { onChangeClick(platform) }) { Text(stringResource(R.string.action_change)) }
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
            if (index != platforms.lastIndex) RowDivider()
        }
    }
}

@Composable
private fun ScrapingContent(
    credentials: IntegrationCredentials,
    autoScrapeOnScan: Boolean,
    isRescraping: Boolean,
    progressMessage: UiMessage?,
    onAutoScrapeChange: (Boolean) -> Unit,
    onRescrapeLibrary: () -> Unit,
) {
    val configuredSources = MetadataSourceId.entries.filter { credentials.hasCredentials(it) }
    SettingsPanel {
        SettingsRow(
            icon = Icons.Filled.CloudDownload,
            title = stringResource(R.string.scraping_sources),
            subtitle = if (configuredSources.isEmpty()) {
                stringResource(R.string.scraping_sources_none)
            } else {
                configuredSources.joinToString { it.name.lowercase().replaceFirstChar(Char::uppercase) }
            },
        ) {}
        RowDivider()
        SettingsRow(
            icon = Icons.Filled.CloudDownload,
            title = stringResource(R.string.scraping_auto),
            subtitle = stringResource(R.string.scraping_auto_desc),
        ) {
            Switch(checked = autoScrapeOnScan, onCheckedChange = onAutoScrapeChange)
        }
    }
    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
        GlassButton(onClick = onRescrapeLibrary, enabled = !isRescraping && configuredSources.isNotEmpty()) {
            if (isRescraping) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.scraping_rescrape))
            }
        }
        progressMessage?.let {
            Text(
                text = it.resolve(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp),
            )
        }
    }
}

@Composable
private fun IntegrationsContent(
    credentials: IntegrationCredentials,
    onUpdateScreenScraper: (String, String, String, String, String) -> Unit,
    onUpdateIgdb: (String, String) -> Unit,
    onUpdateSteamGridDb: (String) -> Unit,
    onUpdateRetroAchievements: (String, String) -> Unit,
) {
    ScreenScraperCard(credentials, onUpdateScreenScraper)
    IgdbCard(credentials, onUpdateIgdb)
    SteamGridDbCard(credentials, onUpdateSteamGridDb)
    RetroAchievementsCard(credentials, onUpdateRetroAchievements)
    LaunchBoxInfoCard()
}

@Composable
private fun IntegrationCard(
    title: String,
    subtitle: String,
    configured: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    LiquidGlassSurface(
        shape = RoundedCornerShape(GlassMetrics.panelRadius),
        style = GlassMaterials.ultraThin,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(configured)
            }
            content()
        }
    }
}

@Composable
private fun StatusBadge(configured: Boolean) {
    val color = if (configured) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = stringResource(if (configured) R.string.integration_connected else R.string.integration_not_connected),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Composable
private fun CredentialField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun IntegrationHelp(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun SaveRow(onSave: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onSave) { Text(stringResource(R.string.action_save)) }
    }
}

@Composable
private fun ScreenScraperCard(credentials: IntegrationCredentials, onSave: (String, String, String, String, String) -> Unit) {
    var devId by remember(credentials) { mutableStateOf(credentials.screenScraperDevId) }
    var devPassword by remember(credentials) { mutableStateOf(credentials.screenScraperDevPassword) }
    var softName by remember(credentials) { mutableStateOf(credentials.screenScraperSoftName) }
    var ssid by remember(credentials) { mutableStateOf(credentials.screenScraperSsid) }
    var ssPassword by remember(credentials) { mutableStateOf(credentials.screenScraperSsPassword) }

    IntegrationCard(
        title = "ScreenScraper",
        subtitle = stringResource(R.string.integration_screenscraper_desc),
        configured = credentials.hasCredentials(MetadataSourceId.SCREENSCRAPER),
    ) {
        IntegrationHelp(stringResource(R.string.integration_screenscraper_help))
        CredentialField(stringResource(R.string.field_dev_id), devId, { devId = it })
        CredentialField(stringResource(R.string.field_dev_password), devPassword, { devPassword = it }, isPassword = true)
        CredentialField(stringResource(R.string.field_software_name), softName, { softName = it })
        RowDivider()
        CredentialField(stringResource(R.string.field_ss_user), ssid, { ssid = it })
        CredentialField(stringResource(R.string.field_ss_password), ssPassword, { ssPassword = it }, isPassword = true)
        SaveRow { onSave(devId, devPassword, softName, ssid, ssPassword) }
    }
}

@Composable
private fun IgdbCard(credentials: IntegrationCredentials, onSave: (String, String) -> Unit) {
    var clientId by remember(credentials) { mutableStateOf(credentials.igdbClientId) }
    var clientSecret by remember(credentials) { mutableStateOf(credentials.igdbClientSecret) }

    IntegrationCard(
        title = "IGDB",
        subtitle = stringResource(R.string.integration_igdb_desc),
        configured = credentials.hasCredentials(MetadataSourceId.IGDB),
    ) {
        IntegrationHelp(stringResource(R.string.integration_igdb_help))
        CredentialField(stringResource(R.string.field_client_id), clientId, { clientId = it })
        CredentialField(stringResource(R.string.field_client_secret), clientSecret, { clientSecret = it }, isPassword = true)
        SaveRow { onSave(clientId, clientSecret) }
    }
}

@Composable
private fun SteamGridDbCard(credentials: IntegrationCredentials, onSave: (String) -> Unit) {
    var apiKey by remember(credentials) { mutableStateOf(credentials.steamGridDbApiKey) }

    IntegrationCard(
        title = "SteamGridDB",
        subtitle = stringResource(R.string.integration_steamgriddb_desc),
        configured = credentials.hasCredentials(MetadataSourceId.STEAMGRIDDB),
    ) {
        IntegrationHelp(stringResource(R.string.integration_steamgriddb_help))
        CredentialField(stringResource(R.string.field_api_key), apiKey, { apiKey = it }, isPassword = true)
        SaveRow { onSave(apiKey) }
    }
}

@Composable
private fun RetroAchievementsCard(credentials: IntegrationCredentials, onSave: (String, String) -> Unit) {
    var username by remember(credentials) { mutableStateOf(credentials.retroAchievementsUsername) }
    var apiKey by remember(credentials) { mutableStateOf(credentials.retroAchievementsApiKey) }

    IntegrationCard(
        title = "RetroAchievements",
        subtitle = stringResource(R.string.integration_retroachievements_desc),
        configured = credentials.hasCredentials(MetadataSourceId.RETROACHIEVEMENTS),
    ) {
        IntegrationHelp(stringResource(R.string.integration_retroachievements_help))
        CredentialField(stringResource(R.string.field_username), username, { username = it })
        CredentialField(stringResource(R.string.field_api_key), apiKey, { apiKey = it }, isPassword = true)
        SaveRow { onSave(username, apiKey) }
    }
}

@Composable
private fun LaunchBoxInfoCard() {
    IntegrationCard(
        title = "LaunchBox",
        subtitle = stringResource(R.string.integration_launchbox_desc),
        configured = false,
    ) {
        IntegrationHelp(stringResource(R.string.integration_launchbox_help))
    }
}

@Composable
private fun AboutContent() {
    SettingsPanel {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.about_body),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}
