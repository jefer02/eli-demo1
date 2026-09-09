package com.elyndra.app.domain.usecase.nativeapp

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.InstalledAndroidApp
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.util.Constants
import javax.inject.Inject

/** Adds an installed Android app to the library as a launchable entry under the synthetic Android platform. */
class AddNativeAppUseCase @Inject constructor(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(app: InstalledAndroidApp): Long {
        gameRepository.findByRomUri(app.packageName)?.let { return it.id }
        return gameRepository.upsertGame(
            Game(
                title = app.label,
                platformId = Constants.ANDROID_PLATFORM_ID,
                romUri = app.packageName,
                fileName = app.packageName,
                fileSizeBytes = 0,
                isNativeApp = true,
            ),
        )
    }
}
