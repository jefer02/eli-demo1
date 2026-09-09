package com.elyndra.app.data.remote.scraper

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.model.MetadataSourceId
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.ScrapeOutcome

/**
 * One metadata provider. [ScraperRepositoryImpl] tries these in priority
 * order and merges results, since no single source covers everything: art
 * sources like SteamGridDB only ever fill image fields, general databases
 * like IGDB only fill what a title search can find, and ScreenScraper's
 * hash-based match is the most precise but retro-only.
 */
internal interface MetadataSourceClient {
    val id: MetadataSourceId
    fun isConfigured(credentials: IntegrationCredentials): Boolean
    suspend fun scrape(game: Game, platform: Platform, credentials: IntegrationCredentials): ScrapeOutcome
}
