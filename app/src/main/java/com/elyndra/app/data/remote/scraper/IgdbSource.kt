package com.elyndra.app.data.remote.scraper

import com.elyndra.app.data.remote.api.IgdbApi
import com.elyndra.app.data.remote.api.TwitchAuthApi
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.model.MetadataSourceId
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.ScrapeOutcome
import com.elyndra.app.util.TitleCleaner
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * General game database, not retro-specific. Matches by cleaned title search
 * (IGDB has no per-ROM hash matching), so it's tried after ScreenScraper and
 * is also what fills in art/summaries for native Android games.
 */
@Singleton
class IgdbSource @Inject constructor(
    private val authApi: TwitchAuthApi,
    private val igdbApi: IgdbApi,
) : MetadataSourceClient {

    override val id = MetadataSourceId.IGDB

    private val tokenMutex = Mutex()
    private var cachedToken: String? = null
    private var tokenExpiresAtEpochSeconds: Long = 0

    override fun isConfigured(credentials: IntegrationCredentials): Boolean =
        credentials.hasCredentials(MetadataSourceId.IGDB)

    override suspend fun scrape(game: Game, platform: Platform, credentials: IntegrationCredentials): ScrapeOutcome {
        if (!isConfigured(credentials)) return ScrapeOutcome.MissingCredentials

        return try {
            val token = accessToken(credentials.igdbClientId, credentials.igdbClientSecret)
                ?: return ScrapeOutcome.Failed("Could not authenticate with Twitch/IGDB")

            val cleanTitle = TitleCleaner.clean(game.fileName).replace("\"", "")
            val query = """
                fields name,summary,cover.image_id,screenshots.image_id,genres.name,involved_companies.company.name,involved_companies.developer,first_release_date;
                search "$cleanTitle";
                limit 1;
            """.trimIndent()

            val results = igdbApi.searchGames(
                headers = mapOf(
                    "Client-ID" to credentials.igdbClientId,
                    "Authorization" to "Bearer $token",
                ),
                query = query.toRequestBody("text/plain".toMediaType()),
            )
            val match = results.firstOrNull() ?: return ScrapeOutcome.NotFound

            val developer = match.involvedCompanies
                ?.firstOrNull { it.developer == true }
                ?.company?.name

            val updated = game.copy(
                title = match.name ?: game.title,
                description = match.summary ?: game.description,
                developer = developer ?: game.developer,
                genre = match.genres?.firstOrNull()?.name ?: game.genre,
                releaseDate = match.firstReleaseDate?.let(::formatEpochSeconds) ?: game.releaseDate,
                coverImagePath = match.cover?.imageId?.let { imageUrl(it, "cover_big") } ?: game.coverImagePath,
                screenshotImagePath = match.screenshots?.firstOrNull()?.imageId?.let { imageUrl(it, "screenshot_huge") }
                    ?: game.screenshotImagePath,
                backgroundImagePath = match.screenshots?.firstOrNull()?.imageId?.let { imageUrl(it, "1080p") }
                    ?: game.backgroundImagePath,
            )
            ScrapeOutcome.Success(updated)
        } catch (e: IOException) {
            ScrapeOutcome.Failed(e.message ?: "Network error while contacting IGDB")
        } catch (e: HttpException) {
            ScrapeOutcome.Failed("IGDB returned HTTP ${e.code()}")
        }
    }

    /** Twitch app tokens last ~60 days; cache in memory instead of re-authenticating every scrape. */
    private suspend fun accessToken(clientId: String, clientSecret: String): String? = tokenMutex.withLock {
        val now = Instant.now().epochSecond
        val token = cachedToken
        if (token != null && now < tokenExpiresAtEpochSeconds) return@withLock token

        val response = authApi.getAppAccessToken(clientId, clientSecret)
        cachedToken = response.accessToken
        tokenExpiresAtEpochSeconds = now + response.expiresIn - TOKEN_EXPIRY_SAFETY_MARGIN_SECONDS
        response.accessToken
    }

    private fun imageUrl(imageId: String, size: String): String =
        "https://images.igdb.com/igdb/image/upload/t_$size/$imageId.jpg"

    private fun formatEpochSeconds(epochSeconds: Long): String =
        DateTimeFormatter.ISO_LOCAL_DATE.format(Instant.ofEpochSecond(epochSeconds).atZone(ZoneOffset.UTC))

    private companion object {
        const val TOKEN_EXPIRY_SAFETY_MARGIN_SECONDS = 300L
    }
}
