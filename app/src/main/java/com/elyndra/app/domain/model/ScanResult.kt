package com.elyndra.app.domain.model

data class ScanResult(
    val filesScanned: Int,
    val gamesAdded: Int,
    val gamesUpdated: Int,
    val unrecognizedFiles: Int,
)
