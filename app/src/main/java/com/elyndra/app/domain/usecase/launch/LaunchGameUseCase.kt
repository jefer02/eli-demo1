package com.elyndra.app.domain.usecase.launch

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.LaunchOutcome
import com.elyndra.app.domain.repository.GameLauncherRepository
import com.elyndra.app.domain.repository.PlatformRepository
import javax.inject.Inject

class LaunchGameUseCase @Inject constructor(
    private val platformRepository: PlatformRepository,
    private val gameLauncherRepository: GameLauncherRepository,
) {
    suspend operator fun invoke(game: Game): LaunchOutcome {
        val platform = platformRepository.getPlatform(game.platformId)
            ?: return LaunchOutcome.Failed("Unknown platform: ${game.platformId}")
        return gameLauncherRepository.launch(game, platform)
    }
}
