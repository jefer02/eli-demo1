package com.elyndra.app.domain.usecase.scan

import com.elyndra.app.di.IoDispatcher
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.ScanProgress
import com.elyndra.app.domain.model.ScanResult
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.RomFolderRepository
import com.elyndra.app.util.TitleCleaner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Walks every configured [com.elyndra.app.domain.model.RomFolder], matches
 * each file to a platform, and upserts it into the library. Emits progress as
 * it goes so the Scanner screen can show a live count instead of a spinner.
 */
class ScanRomFoldersUseCase @Inject constructor(
    private val romFolderRepository: RomFolderRepository,
    private val gameRepository: GameRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(): Flow<ScanProgress> = flow {
        val folders = romFolderRepository.getFolders()
        var filesScanned = 0
        var gamesAdded = 0
        var gamesUpdated = 0
        var unrecognized = 0
        val newlyAddedGameIds = mutableListOf<Long>()

        for (folder in folders) {
            emit(ScanProgress.Scanning(folder.displayPath, filesScanned))
            val scannedFiles = romFolderRepository.scanTree(folder.treeUri)

            for (file in scannedFiles) {
                filesScanned++
                val platformId = PlatformDetector.detect(file.extension, file.parentFolderName)
                if (platformId == null) {
                    unrecognized++
                    continue
                }

                val existing = gameRepository.findByRomUri(file.documentUri)
                if (existing == null) {
                    val id = gameRepository.upsertGame(
                        Game(
                            title = TitleCleaner.clean(file.fileName),
                            platformId = platformId,
                            romUri = file.documentUri,
                            fileName = file.fileName,
                            fileSizeBytes = file.sizeBytes,
                        ),
                    )
                    newlyAddedGameIds += id
                    gamesAdded++
                } else if (existing.fileSizeBytes != file.sizeBytes) {
                    gameRepository.upsertGame(existing.copy(fileSizeBytes = file.sizeBytes))
                    gamesUpdated++
                }

                if (filesScanned % 10 == 0) emit(ScanProgress.Scanning(folder.displayPath, filesScanned))
            }

            romFolderRepository.markScanned(folder.id, System.currentTimeMillis())
        }

        emit(
            ScanProgress.Completed(
                result = ScanResult(filesScanned, gamesAdded, gamesUpdated, unrecognized),
                newlyAddedGameIds = newlyAddedGameIds,
            ),
        )
    }.flowOn(ioDispatcher)
}
