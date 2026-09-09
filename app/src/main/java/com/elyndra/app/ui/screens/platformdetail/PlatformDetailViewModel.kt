package com.elyndra.app.ui.screens.platformdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.model.InstalledAndroidApp
import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.SortOrder
import com.elyndra.app.domain.repository.GameLauncherRepository
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.InstalledAppsRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.domain.repository.UserPreferencesRepository
import com.elyndra.app.domain.usecase.game.SearchGamesUseCase
import com.elyndra.app.domain.usecase.nativeapp.AddNativeAppUseCase
import com.elyndra.app.domain.usecase.settings.SetPlatformEmulatorUseCase
import com.elyndra.app.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlatformDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    platformRepository: PlatformRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val installedAppsRepository: InstalledAppsRepository,
    private val gameLauncherRepository: GameLauncherRepository,
    private val searchGamesUseCase: SearchGamesUseCase,
    private val addNativeAppUseCase: AddNativeAppUseCase,
    private val setPlatformEmulatorUseCase: SetPlatformEmulatorUseCase,
) : ViewModel() {

    private data class Extras(
        val emulators: List<InstalledEmulatorApp>,
        val launchableApps: List<InstalledAndroidApp>,
    )

    private val platformId = MutableStateFlow<String?>(null)
    private val installedEmulators = MutableStateFlow<List<InstalledEmulatorApp>>(emptyList())
    private val launchableApps = MutableStateFlow<List<InstalledAndroidApp>>(emptyList())
    private val extras = combine(installedEmulators, launchableApps, ::Extras)

    val uiState: StateFlow<PlatformDetailUiState> = combine(
        platformId,
        gameRepository.observeGames(includeHidden = false),
        platformRepository.observePlatforms(),
        userPreferencesRepository.preferences,
        extras,
    ) { id, games, platforms, prefs, extra ->
        PlatformDetailUiState(
            platform = platforms.find { it.id == id },
            games = if (id == null) {
                emptyList()
            } else {
                searchGamesUseCase(games = games, platformId = id, sortOrder = prefs.sortOrder)
            },
            sortOrder = prefs.sortOrder,
            installedEmulators = extra.emulators,
            launchableApps = extra.launchableApps,
            isAndroidPlatform = id == Constants.ANDROID_PLATFORM_ID,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlatformDetailUiState())

    fun setPlatformId(id: String) {
        if (platformId.value == id) return
        platformId.value = id
        installedEmulators.value = gameLauncherRepository.getInstalledCandidateEmulators()
        if (id == Constants.ANDROID_PLATFORM_ID) {
            launchableApps.value = installedAppsRepository.getLaunchableApps()
        }
    }

    fun onSortOrderChange(order: SortOrder) {
        viewModelScope.launch { userPreferencesRepository.updateSortOrder(order) }
    }

    fun onSetEmulator(packageName: String?) {
        val id = platformId.value ?: return
        viewModelScope.launch { setPlatformEmulatorUseCase(id, packageName, activityName = null) }
    }

    fun onAddNativeApp(app: InstalledAndroidApp) {
        viewModelScope.launch { addNativeAppUseCase(app) }
    }
}
