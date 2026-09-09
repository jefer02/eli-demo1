package com.elyndra.app.ui.screens.gamedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.model.LaunchOutcome
import com.elyndra.app.domain.model.ScrapeOutcome
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.domain.usecase.game.DeleteGameUseCase
import com.elyndra.app.domain.usecase.game.ToggleFavoriteUseCase
import com.elyndra.app.domain.usecase.game.UpdateGameUseCase
import com.elyndra.app.domain.usecase.launch.LaunchGameUseCase
import com.elyndra.app.domain.usecase.scraper.ScrapeGameUseCase
import com.elyndra.app.R
import com.elyndra.app.ui.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    platformRepository: PlatformRepository,
    private val launchGameUseCase: LaunchGameUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    private val scrapeGameUseCase: ScrapeGameUseCase,
) : ViewModel() {

    private data class ControlState(
        val isLaunching: Boolean,
        val isScraping: Boolean,
        val launchError: UiMessage?,
        val scrapeMessage: UiMessage?,
    )

    private val gameId = MutableStateFlow<Long?>(null)
    private val isLaunching = MutableStateFlow(false)
    private val isScraping = MutableStateFlow(false)
    private val launchError = MutableStateFlow<UiMessage?>(null)
    private val scrapeMessage = MutableStateFlow<UiMessage?>(null)

    private val gameFlow = gameId.filterNotNull().flatMapLatest { gameRepository.observeGame(it) }
    private val controlState = combine(isLaunching, isScraping, launchError, scrapeMessage, ::ControlState)

    val uiState: StateFlow<GameDetailUiState> = combine(
        gameFlow,
        platformRepository.observePlatforms(),
        controlState,
    ) { game, platforms, control ->
        GameDetailUiState(
            game = game,
            platform = platforms.find { it.id == game?.platformId },
            allPlatforms = platforms,
            isLoading = game == null,
            isLaunching = control.isLaunching,
            isScraping = control.isScraping,
            launchError = control.launchError,
            scrapeMessage = control.scrapeMessage,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameDetailUiState())

    /** Called once from the screen with the id from the nav route. */
    fun load(id: Long) {
        if (gameId.value != id) gameId.value = id
    }

    fun onPlayClick() {
        val game = uiState.value.game ?: return
        viewModelScope.launch {
            isLaunching.value = true
            launchError.value = null
            launchError.value = when (val outcome = launchGameUseCase(game)) {
                LaunchOutcome.Launched -> null
                LaunchOutcome.EmulatorNotConfigured -> {
                    val platformName = uiState.value.platform?.displayName ?: game.platformId
                    UiMessage(R.string.launch_error_no_emulator, platformName)
                }
                is LaunchOutcome.EmulatorNotInstalled ->
                    UiMessage(R.string.launch_error_not_installed, outcome.packageName)
                is LaunchOutcome.Failed -> UiMessage(R.string.launch_error_failed, outcome.message)
            }
            isLaunching.value = false
        }
    }

    fun onToggleFavorite() {
        val game = uiState.value.game ?: return
        viewModelScope.launch { toggleFavoriteUseCase(game.id, !game.isFavorite) }
    }

    fun onDelete() {
        val game = uiState.value.game ?: return
        viewModelScope.launch { deleteGameUseCase(game.id) }
    }

    fun onRescrape() {
        val game = uiState.value.game ?: return
        viewModelScope.launch {
            isScraping.value = true
            scrapeMessage.value = when (val outcome = scrapeGameUseCase(game.id)) {
                is ScrapeOutcome.Success -> UiMessage(R.string.scrape_updated)
                ScrapeOutcome.NotFound -> UiMessage(R.string.scrape_not_found)
                ScrapeOutcome.MissingCredentials -> UiMessage(R.string.scrape_missing_credentials)
                is ScrapeOutcome.Failed -> UiMessage(R.string.scrape_failed, outcome.message)
            }
            isScraping.value = false
        }
    }

    fun onSaveEdits(
        title: String,
        platformId: String,
        isHidden: Boolean,
        coverImagePath: String?,
        backgroundImagePath: String?,
    ) {
        val game = uiState.value.game ?: return
        viewModelScope.launch {
            updateGameUseCase(
                game.copy(
                    title = title,
                    platformId = platformId,
                    isHidden = isHidden,
                    coverImagePath = coverImagePath,
                    backgroundImagePath = backgroundImagePath,
                ),
            )
        }
    }

    fun dismissLaunchError() {
        launchError.value = null
    }

    fun dismissScrapeMessage() {
        scrapeMessage.value = null
    }
}
