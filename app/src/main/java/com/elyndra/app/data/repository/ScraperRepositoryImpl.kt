package com.elyndra.app.data.repository

import com.elyndra.app.data.remote.scraper.IgdbSource
import com.elyndra.app.data.remote.scraper.ScreenScraperSource
import com.elyndra.app.data.remote.scraper.SteamGridDbSource
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.ScrapeOutcome
import com.elyndra.app.domain.repository.IntegrationCredentialsRepository
import com.elyndra.app.domain.repository.ScraperRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tries each configured metadata source in turn, keeping whatever the best
 * result so far has and letting later sources fill in only what's still
 * missing - a hash-matched ScreenScraper hit for a ROM should never get its
 * title overwritten by a fuzzier IGDB title search, but a missing screenshot
 * is fair game for the next source to provide.
 */
@Singleton
class ScraperRepositoryImpl @Inject constructor(
    private val credentialsRepository: IntegrationCredentialsRepository,
    private val screenScraperSource: ScreenScraperSource,
    private val igdbSource: IgdbSource,
    private val steamGridDbSource: SteamGridDbSource,
) : ScraperRepository {

    override suspend fun scrapeGame(game: Game, platform: Platform): ScrapeOutcome {
        val credentials = credentialsRepository.credentials.first()
        val sources = listOf(screenScraperSource, igdbSource, steamGridDbSource)
        val configuredSources = sources.filter { it.isConfigured(credentials) }
        if (configuredSources.isEmpty()) return ScrapeOutcome.MissingCredentials

        var working = game
        var foundAnything = false
        var lastFailure: ScrapeOutcome.Failed? = null

        for (source in configuredSources) {
            if (isComplete(working)) break
            when (val outcome = source.scrape(working, platform, credentials)) {
                is ScrapeOutcome.Success -> {
                    working = outcome.game
                    foundAnything = true
                }
                is ScrapeOutcome.Failed -> lastFailure = outcome
                ScrapeOutcome.NotFound, ScrapeOutcome.MissingCredentials -> Unit
            }
        }

        return when {
            foundAnything -> ScrapeOutcome.Success(working)
            lastFailure != null -> lastFailure
            else -> ScrapeOutcome.NotFound
        }
    }

    /** Once these are filled there's nothing left worth querying more sources for. */
    private fun isComplete(game: Game): Boolean =
        !game.coverImagePath.isNullOrBlank() &&
            !game.backgroundImagePath.isNullOrBlank() &&
            !game.description.isNullOrBlank()
}
