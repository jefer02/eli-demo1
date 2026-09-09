package com.elyndra.app.data.repository

import android.net.Uri
import com.elyndra.app.data.local.dao.RomFolderDao
import com.elyndra.app.data.local.entity.RomFolderEntity
import com.elyndra.app.data.mapper.toDomain
import com.elyndra.app.data.scanner.RomScanner
import com.elyndra.app.domain.model.RomFolder
import com.elyndra.app.domain.model.ScannedRomFile
import com.elyndra.app.domain.repository.RomFolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RomFolderRepositoryImpl @Inject constructor(
    private val romFolderDao: RomFolderDao,
    private val romScanner: RomScanner,
) : RomFolderRepository {

    override fun observeFolders(): Flow<List<RomFolder>> =
        romFolderDao.observeFolders().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getFolders(): List<RomFolder> =
        romFolderDao.getFolders().map { it.toDomain() }

    override suspend fun addFolder(treeUri: String, displayPath: String): Long =
        romFolderDao.insert(
            RomFolderEntity(
                treeUri = treeUri,
                displayPath = displayPath,
                dateAdded = System.currentTimeMillis(),
                lastScannedAt = null,
            ),
        )

    override suspend fun removeFolder(folderId: Long) = romFolderDao.deleteById(folderId)

    override suspend fun markScanned(folderId: Long, timestamp: Long) =
        romFolderDao.markScanned(folderId, timestamp)

    override suspend fun scanTree(treeUri: String): List<ScannedRomFile> =
        romScanner.scanTree(Uri.parse(treeUri)).map {
            ScannedRomFile(
                documentUri = it.documentUri.toString(),
                fileName = it.fileName,
                extension = it.extension,
                sizeBytes = it.sizeBytes,
                parentFolderName = it.parentFolderName,
            )
        }
}
