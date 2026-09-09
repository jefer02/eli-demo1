package com.elyndra.app.domain.model

/** A file found while walking a ROM folder, not yet matched to a platform. */
data class ScannedRomFile(
    val documentUri: String,
    val fileName: String,
    val extension: String,
    val sizeBytes: Long,
    val parentFolderName: String,
)
