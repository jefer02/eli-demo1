package com.elyndra.app.data.remote.scraper

import android.net.Uri
import com.elyndra.app.data.remote.ScreenScraperSystemIds
import com.elyndra.app.data.remote.api.ScreenScraperApi
import com.elyndra.app.data.remote.dto.ScreenScraperLocalizedTextDto
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.model.MetadataSourceId
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.ScrapeOutcome
import com.elyndra.app.util.Crc32Calculator
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/** Hash-matched retro metadata - the most precise source Elyndra has for ROMs. */
class ScreenScraperSource @Inject constructor(
    private val api: ScreenScraperApi,
    private val crc32Calculator: Crc32Calculator,
) : MetadataSourceClient {

    override val id = MetadataSourceId.SCREENSCRAPER

    override fun isConfigured(credentials: IntegrationCredentials): Boolean =
        credentials.hasCredentials(MetadataSourceId.SCREENSCRAPER)

    override suspend fun scrape(game: Game, platform: Platform, credentials: IntegrationCredentials): ScrapeOutcome {
        if (!isConfigured(credentials)) return ScrapeOutcome.MissingCredentials

        return try {
            val crc = crc32Calculator.compute(Uri.parse(game.romUri))
            val envelope = api.getGameInfo(
                devId = credentials.screenScraperDevId,
                devPassword = credentials.screenScraperDevPassword,
                softName = credentials.screenScraperSoftName,
                ssid = credentials.screenScraperSsid.ifBlank { null },
                ssPassword = credentials.screenScraperSsPassword.ifBlank { null },
                crc32 = crc,
                romFileName = game.fileName,
                systemId = ScreenScraperSystemIds.forPlatform(platform.id),
            )
            val dto = envelope.response?.jeu ?: return ScrapeOutcome.NotFound

            val updated = game.copy(
                title = pickText(dto.noms, PREFERRED_REGIONS) ?: game.title,
                description = pickText(dto.synopsis, PREFERRED_LANGUAGES) ?: game.description,
                developer = dto.developpeur?.text ?: game.developer,
                genre = dto.genres.firstOrNull()?.let { pickText(it.noms, PREFERRED_LANGUAGES) } ?: game.genre,
                releaseDate = pickText(dto.dates, PREFERRED_REGIONS) ?: game.releaseDate,
                coverImagePath = dto.medias.firstOrNull { it.type in COVER_TYPES }?.url ?: game.coverImagePath,
                logoImagePath = dto.medias.firstOrNull { it.type in LOGO_TYPES }?.url ?: game.logoImagePath,
                screenshotImagePath = dto.medias.firstOrNull { it.type in SCREENSHOT_TYPES }?.url
                    ?: game.screenshotImagePath,
                backgroundImagePath = dto.medias.firstOrNull { it.type in BACKGROUND_TYPES }?.url
                    ?: game.backgroundImagePath,
                crc32 = crc ?: game.crc32,
            )
            ScrapeOutcome.Success(updated)
        } catch (e: IOException) {
            ScrapeOutcome.Failed(e.message ?: "Network error while scraping")
        } catch (e: HttpException) {
            ScrapeOutcome.Failed("ScreenScraper returned HTTP ${e.code()}")
        }
    }

    private fun pickText(items: List<ScreenScraperLocalizedTextDto>, preferred: List<String>): String? {
        if (items.isEmpty()) return null
        for (key in preferred) {
            items.firstOrNull { (it.region ?: it.langue) == key }?.let { return it.text }
        }
        return items.firstOrNull()?.text
    }

    private companion object {
        val PREFERRED_REGIONS = listOf("wor", "us", "eu", "ss")
        val PREFERRED_LANGUAGES = listOf("en", "es")
        val COVER_TYPES = setOf("box-2D", "box-3D")
        val LOGO_TYPES = setOf("wheel", "wheel-hd")
        val SCREENSHOT_TYPES = setOf("ss", "screenmarquee", "screenmarqueesmall")
        val BACKGROUND_TYPES = setOf("fanart", "screenmarqueewide")
    }
}
