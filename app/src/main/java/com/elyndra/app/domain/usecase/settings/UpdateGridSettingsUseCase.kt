package com.elyndra.app.domain.usecase.settings

import com.elyndra.app.domain.model.ViewMode
import com.elyndra.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateGridSettingsUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend fun setColumns(columns: Int) = userPreferencesRepository.updateGridColumns(columns)
    suspend fun setViewMode(mode: ViewMode) = userPreferencesRepository.updateViewMode(mode)
}
