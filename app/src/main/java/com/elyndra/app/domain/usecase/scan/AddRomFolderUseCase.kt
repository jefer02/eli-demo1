package com.elyndra.app.domain.usecase.scan

import com.elyndra.app.domain.repository.RomFolderRepository
import javax.inject.Inject

class AddRomFolderUseCase @Inject constructor(
    private val romFolderRepository: RomFolderRepository,
) {
    suspend operator fun invoke(treeUri: String, displayPath: String): Long =
        romFolderRepository.addFolder(treeUri, displayPath)
}
