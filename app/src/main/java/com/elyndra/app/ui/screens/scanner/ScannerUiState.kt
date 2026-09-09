package com.elyndra.app.ui.screens.scanner

import com.elyndra.app.domain.model.RomFolder
import com.elyndra.app.domain.model.ScanResult

data class ScannerUiState(
    val romFolders: List<RomFolder> = emptyList(),
    val isScanning: Boolean = false,
    val currentFolder: String? = null,
    val filesFoundSoFar: Int = 0,
    val isAutoScraping: Boolean = false,
    val lastResult: ScanResult? = null,
)
