package com.elyndra.app.domain.usecase.scan

import com.elyndra.app.domain.repository.RomFolderRepository
import javax.inject.Inject

class RemoveRomFolderUseCase @Inject constructor(
    private val romFolderRepository: RomFolderRepository,
) {
    suspend operator fun invoke(folderId: Long) {
        romFolderRepository.removeFolder(folderId)
    }
}
