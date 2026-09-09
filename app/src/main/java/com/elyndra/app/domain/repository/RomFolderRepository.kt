package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.RomFolder
import com.elyndra.app.domain.model.ScannedRomFile
import kotlinx.coroutines.flow.Flow

interface RomFolderRepository {
    fun observeFolders(): Flow<List<RomFolder>>
    suspend fun getFolders(): List<RomFolder>
    suspend fun addFolder(treeUri: String, displayPath: String): Long
    suspend fun removeFolder(folderId: Long)
    suspend fun markScanned(folderId: Long, timestamp: Long)

    /** Recursively lists every recognized ROM file under [treeUri]. */
    suspend fun scanTree(treeUri: String): List<ScannedRomFile>
}
