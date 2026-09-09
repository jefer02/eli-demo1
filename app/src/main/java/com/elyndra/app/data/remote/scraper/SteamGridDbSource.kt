package com.elyndra.app.data.remote.scraper

import com.elyndra.app.data.remote.api.SteamGridDbApi
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.model.MetadataSourceId
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.ScrapeOutcome
import com.elyndra.app.util.TitleCleaner
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/** Art only - no text metadata - so it only ever fills [Game.coverImagePath] and [Game.backgroundImagePath]. */
class SteamGridDbSource @Inject constructor(
    private val api: SteamGridDbApi,
) : MetadataSourceClient {

    override val id = MetadataSourceId.STEAMGRIDDB

    override fun isConfigured(credentials: IntegrationCredentials): Boolean =
        credentials.hasCredentials(MetadataSourceId.STEAMGRIDDB)

    override suspend fun scrape(game: Game, platform: Platform, credentials: IntegrationCredentials): ScrapeOutcome {
        if (!isConfigured(credentials)) return ScrapeOutcome.MissingCredentials
        if (!game.coverImagePath.isNullOrBlank() && !game.backgroundImagePath.isNullOrBlank()) {
            // Nothing for this source to contribute.
            return ScrapeOutcome.Success(game)
        }

        val authorization = "Bearer ${credentials.steamGridDbApiKey}"

        return try {
            val cleanTitle = TitleCleaner.clean(game.fileName)
            val searchResult = api.searchGames(authorization, cleanTitle)
            val gridDbId = searchResult.data.firstOrNull()?.id ?: return ScrapeOutcome.NotFound

            var cover = game.coverImagePath
            if (cover.isNullOrBlank()) {
                cover = api.getGrids(authorization, gridDbId).data.firstOrNull()?.url
            }

            var background = game.backgroundImagePath
            if (background.isNullOrBlank()) {
                background = api.getHeroes(authorization, gridDbId).data.firstOrNull()?.url
            }

            ScrapeOutcome.Success(game.copy(coverImagePath = cover, backgroundImagePath = background))
        } catch (e: IOException) {
            ScrapeOutcome.Failed(e.message ?: "Network error while contacting SteamGridDB")
        } catch (e: HttpException) {
            ScrapeOutcome.Failed("SteamGridDB returned HTTP ${e.code()}")
        }
    }
}
