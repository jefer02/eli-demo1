package com.elyndra.app.ui.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elyndra.app.domain.model.ScanProgress
import com.elyndra.app.domain.model.ScanResult
import com.elyndra.app.domain.repository.RomFolderRepository
import com.elyndra.app.domain.repository.UserPreferencesRepository
import com.elyndra.app.domain.usecase.scan.AddRomFolderUseCase
import com.elyndra.app.domain.usecase.scan.ScanRomFoldersUseCase
import com.elyndra.app.domain.usecase.scraper.ScrapeLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    romFolderRepository: RomFolderRepository,
    private val addRomFolderUseCase: AddRomFolderUseCase,
    private val scanRomFoldersUseCase: ScanRomFoldersUseCase,
    private val scrapeLibraryUseCase: ScrapeLibraryUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private data class ProgressState(
        val isScanning: Boolean,
        val currentFolder: String?,
        val filesFoundSoFar: Int,
        val isAutoScraping: Boolean,
        val lastResult: ScanResult?,
    )

    private val isScanning = MutableStateFlow(false)
    private val currentFolder = MutableStateFlow<String?>(null)
    private val filesFoundSoFar = MutableStateFlow(0)
    private val isAutoScraping = MutableStateFlow(false)
    private val lastResult = MutableStateFlow<ScanResult?>(null)

    private val progressState = combine(
        isScanning,
        currentFolder,
        filesFoundSoFar,
        isAutoScraping,
        lastResult,
        ::ProgressState,
    )

    val uiState: StateFlow<ScannerUiState> = combine(
        romFolderRepository.observeFolders(),
        progressState,
    ) { folders, progress ->
        ScannerUiState(
            romFolders = folders,
            isScanning = progress.isScanning,
            currentFolder = progress.currentFolder,
            filesFoundSoFar = progress.filesFoundSoFar,
            isAutoScraping = progress.isAutoScraping,
            lastResult = progress.lastResult,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScannerUiState())

    fun onAddFolder(treeUri: String, displayPath: String) {
        viewModelScope.launch { addRomFolderUseCase(treeUri, displayPath) }
    }

    fun onStartScan() {
        if (isScanning.value) return
        viewModelScope.launch {
            isScanning.value = true
            lastResult.value = null
            var newlyAddedGameIds = emptyList<Long>()

            scanRomFoldersUseCase().collect { progress ->
                when (progress) {
                    is ScanProgress.Scanning -> {
                        currentFolder.value = progress.currentFolder
                        filesFoundSoFar.value = progress.filesFoundSoFar
                    }
                    is ScanProgress.Completed -> {
                        lastResult.value = progress.result
                        newlyAddedGameIds = progress.newlyAddedGameIds
                    }
                }
            }
            isScanning.value = false
            currentFolder.value = null

            if (newlyAddedGameIds.isNotEmpty() && userPreferencesRepository.preferences.first().autoScrapeOnScan) {
                isAutoScraping.value = true
                scrapeLibraryUseCase(newlyAddedGameIds).collect { /* progress folded into isAutoScraping only */ }
                isAutoScraping.value = false
            }
        }
    }
}
