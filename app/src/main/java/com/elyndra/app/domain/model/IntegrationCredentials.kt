package com.elyndra.app.domain.model

enum class MetadataSourceId { SCREENSCRAPER, IGDB, STEAMGRIDDB, RETROACHIEVEMENTS }

/** All third-party credentials the user enters in Settings > Integrations. */
data class IntegrationCredentials(
    // Identifies Elyndra itself to ScreenScraper's API; required for every call.
    val screenScraperDevId: String = "",
    val screenScraperDevPassword: String = "",
    val screenScraperSoftName: String = "Elyndra",
    // The user's own screenscraper.fr forum account. Optional, but raises their personal daily quota/threads.
    val screenScraperSsid: String = "",
    val screenScraperSsPassword: String = "",
    val igdbClientId: String = "",
    val igdbClientSecret: String = "",
    val steamGridDbApiKey: String = "",
    val retroAchievementsUsername: String = "",
    val retroAchievementsApiKey: String = "",
) {
    fun hasCredentials(source: MetadataSourceId): Boolean = when (source) {
        MetadataSourceId.SCREENSCRAPER -> screenScraperDevId.isNotBlank() && screenScraperDevPassword.isNotBlank()
        MetadataSourceId.IGDB -> igdbClientId.isNotBlank() && igdbClientSecret.isNotBlank()
        MetadataSourceId.STEAMGRIDDB -> steamGridDbApiKey.isNotBlank()
        MetadataSourceId.RETROACHIEVEMENTS -> retroAchievementsUsername.isNotBlank() && retroAchievementsApiKey.isNotBlank()
    }
}
