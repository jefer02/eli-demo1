package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.ScrapeOutcome

interface ScraperRepository {
    /** Tries every source the user has entered credentials for; see ScraperRepositoryImpl for the merge order. */
    suspend fun scrapeGame(game: Game, platform: Platform): ScrapeOutcome
}
