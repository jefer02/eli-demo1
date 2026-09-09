package com.elyndra.app.domain.usecase.scraper

import com.elyndra.app.domain.model.ScrapeOutcome
import com.elyndra.app.domain.repository.GameRepository
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.domain.repository.ScraperRepository
import javax.inject.Inject

class ScrapeGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val platformRepository: PlatformRepository,
    private val scraperRepository: ScraperRepository,
) {
    suspend operator fun invoke(gameId: Long): ScrapeOutcome {
        val game = gameRepository.getGame(gameId)
            ?: return ScrapeOutcome.Failed("Game not found")
        val platform = platformRepository.getPlatform(game.platformId)
            ?: return ScrapeOutcome.Failed("Unknown platform for this game")

        val outcome = scraperRepository.scrapeGame(game, platform)
        if (outcome is ScrapeOutcome.Success) {
            gameRepository.upsertGame(outcome.game)
        }
        return outcome
    }
}
