package com.elyndra.app.domain.usecase.settings

import com.elyndra.app.domain.model.ThemeMode
import com.elyndra.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateThemeModeUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(mode: ThemeMode) = userPreferencesRepository.updateThemeMode(mode)
}
