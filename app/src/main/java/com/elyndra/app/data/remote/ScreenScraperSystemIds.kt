package com.elyndra.app.data.remote

/**
 * Elyndra platform id -> ScreenScraper `systemeid`. Passing the right id
 * narrows their match considerably; passing none still works (they fall back
 * to hashing across all systems) so unmapped/uncertain platforms are simply
 * left out rather than risk sending a wrong id. Cross-check/extend this via
 * https://api.screenscraper.fr/api2/systemesListe.php if a platform is missing.
 */
object ScreenScraperSystemIds {
    private val map = mapOf(
        "nes" to 3,
        "snes" to 4,
        "n64" to 14,
        "gb" to 9,
        "gbc" to 10,
        "gba" to 12,
        "nds" to 15,
        "gamecube" to 13,
        "wii" to 16,
        "psx" to 57,
        "ps2" to 58,
        "psp" to 61,
        "genesis" to 1,
        "mastersystem" to 2,
        "gamegear" to 21,
        "saturn" to 22,
        "dreamcast" to 23,
    )

    fun forPlatform(platformId: String): Int? = map[platformId]
}
