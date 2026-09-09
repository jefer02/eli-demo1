package com.elyndra.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.R
import com.elyndra.app.domain.model.AccentColor
import com.elyndra.app.domain.model.AppLanguage
import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.model.ThemeMode
import com.elyndra.app.domain.repository.GameLauncherRepository
import com.elyndra.app.domain.repository.IntegrationCredentialsRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.domain.repository.RomFolderRepository
import com.elyndra.app.domain.repository.UserPreferencesRepository
import com.elyndra.app.domain.usecase.scan.AddRomFolderUseCase
import com.elyndra.app.domain.usecase.scan.RemoveRomFolderUseCase
import com.elyndra.app.domain.usecase.scraper.ScrapeLibraryUseCase
import com.elyndra.app.domain.usecase.settings.SetPlatformEmulatorUseCase
import com.elyndra.app.domain.usecase.settings.UpdateGridSettingsUseCase
import com.elyndra.app.domain.usecase.settings.UpdateThemeModeUseCase
import com.elyndra.app.ui.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val romFolderRepository: RomFolderRepository,
    platformRepository: PlatformRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val gameLauncherRepository: GameLauncherRepository,
    private val credentialsRepository: IntegrationCredentialsRepository,
    private val addRomFolderUseCase: AddRomFolderUseCase,
    private val removeRomFolderUseCase: RemoveRomFolderUseCase,
    private val setPlatformEmulatorUseCase: SetPlatformEmulatorUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase,
    private val updateGridSettingsUseCase: UpdateGridSettingsUseCase,
    private val scrapeLibraryUseCase: ScrapeLibraryUseCase,
) : ViewModel() {

    private data class ScraperState(val isRunning: Boolean, val message: UiMessage?, val credentials: IntegrationCredentials)

    private val installedEmulators = MutableStateFlow<List<InstalledEmulatorApp>>(emptyList())
    private val isRescraping = MutableStateFlow(false)
    private val rescrapeMessage = MutableStateFlow<UiMessage?>(null)
    private val scraperState = combine(
        isRescraping,
        rescrapeMessage,
        credentialsRepository.credentials,
        ::ScraperState,
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        romFolderRepository.observeFolders(),
        platformRepository.observePlatforms(),
        userPreferencesRepository.preferences,
        installedEmulators,
        scraperState,
    ) { folders, platforms, prefs, emulators, scraper ->
        SettingsUiState(
            romFolders = folders,
            platforms = platforms,
            installedEmulators = emulators,
            preferences = prefs,
            credentials = scraper.credentials,
            isRescrapingLibrary = scraper.isRunning,
            rescrapeProgressMessage = scraper.message,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    init {
        installedEmulators.value = gameLauncherRepository.getInstalledCandidateEmulators()
    }

    fun onAddFolder(treeUri: String, displayPath: String) {
        viewModelScope.launch { addRomFolderUseCase(treeUri, displayPath) }
    }

    fun onRemoveFolder(folderId: Long) {
        viewModelScope.launch { removeRomFolderUseCase(folderId) }
    }

    fun onSetPlatformEmulator(platformId: String, packageName: String?) {
        viewModelScope.launch { setPlatformEmulatorUseCase(platformId, packageName, activityName = null) }
    }

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { updateThemeModeUseCase(mode) }
    }

    fun onAccentColorChange(accent: AccentColor) {
        viewModelScope.launch { userPreferencesRepository.updateAccentColor(accent) }
    }

    fun onLanguageChange(language: AppLanguage) {
        viewModelScope.launch { userPreferencesRepository.updateLanguage(language) }
    }

    fun onGridColumnsChange(columns: Int) {
        viewModelScope.launch { updateGridSettingsUseCase.setColumns(columns) }
    }

    fun onAutoScrapeChange(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateAutoScrapeOnScan(enabled) }
    }

    fun onShowHiddenChange(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateShowHiddenGames(enabled) }
    }

    fun onRescrapeLibrary() {
        viewModelScope.launch {
            isRescraping.value = true
            scrapeLibraryUseCase().collect { result ->
                rescrapeMessage.value = UiMessage(
                    R.string.rescrape_progress,
                    result.succeeded,
                    result.totalRequested,
                    result.notFound,
                    result.failed,
                )
            }
            isRescraping.value = false
        }
    }

    fun onUpdateScreenScraperCredentials(devId: String, devPassword: String, softName: String, ssid: String, ssPassword: String) {
        viewModelScope.launch { credentialsRepository.updateScreenScraper(devId, devPassword, softName, ssid, ssPassword) }
    }

    fun onUpdateIgdbCredentials(clientId: String, clientSecret: String) {
        viewModelScope.launch { credentialsRepository.updateIgdb(clientId, clientSecret) }
    }

    fun onUpdateSteamGridDbCredentials(apiKey: String) {
        viewModelScope.launch { credentialsRepository.updateSteamGridDb(apiKey) }
    }

    fun onUpdateRetroAchievementsCredentials(username: String, apiKey: String) {
        viewModelScope.launch { credentialsRepository.updateRetroAchievements(username, apiKey) }
    }
}
