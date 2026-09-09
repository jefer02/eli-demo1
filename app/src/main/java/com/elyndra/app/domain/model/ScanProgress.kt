package com.elyndra.app.domain.model

sealed interface ScanProgress {
    data class Scanning(val currentFolder: String, val filesFoundSoFar: Int) : ScanProgress
    data class Completed(val result: ScanResult, val newlyAddedGameIds: List<Long>) : ScanProgress
}
