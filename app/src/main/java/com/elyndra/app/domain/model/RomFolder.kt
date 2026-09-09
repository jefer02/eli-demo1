package com.elyndra.app.domain.model

/** A user-picked SAF tree (`content://...`) that gets scanned recursively for ROMs. */
data class RomFolder(
    val id: Long = 0,
    val treeUri: String,
    val displayPath: String,
    val dateAdded: Long = System.currentTimeMillis(),
    val lastScannedAt: Long? = null,
)
