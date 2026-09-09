package com.elyndra.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elyndra.app.domain.model.IntegrationCredentials
import com.elyndra.app.domain.repository.IntegrationCredentialsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntegrationCredentialsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : IntegrationCredentialsRepository {

    private object Keys {
        val SS_DEV_ID = stringPreferencesKey("integration_screenscraper_devid")
        val SS_DEV_PASSWORD = stringPreferencesKey("integration_screenscraper_devpassword")
        val SS_SOFTNAME = stringPreferencesKey("integration_screenscraper_softname")
        val SS_SSID = stringPreferencesKey("integration_screenscraper_ssid")
        val SS_SSPASSWORD = stringPreferencesKey("integration_screenscraper_sspassword")
        val IGDB_CLIENT_ID = stringPreferencesKey("integration_igdb_client_id")
        val IGDB_CLIENT_SECRET = stringPreferencesKey("integration_igdb_client_secret")
        val STEAMGRIDDB_API_KEY = stringPreferencesKey("integration_steamgriddb_api_key")
        val RA_USERNAME = stringPreferencesKey("integration_retroachievements_username")
        val RA_API_KEY = stringPreferencesKey("integration_retroachievements_api_key")
    }

    override val credentials: Flow<IntegrationCredentials> = dataStore.data.map { prefs ->
        IntegrationCredentials(
            screenScraperDevId = prefs[Keys.SS_DEV_ID] ?: "",
            screenScraperDevPassword = prefs[Keys.SS_DEV_PASSWORD] ?: "",
            screenScraperSoftName = prefs[Keys.SS_SOFTNAME]?.ifBlank { null } ?: "Elyndra",
            screenScraperSsid = prefs[Keys.SS_SSID] ?: "",
            screenScraperSsPassword = prefs[Keys.SS_SSPASSWORD] ?: "",
            igdbClientId = prefs[Keys.IGDB_CLIENT_ID] ?: "",
            igdbClientSecret = prefs[Keys.IGDB_CLIENT_SECRET] ?: "",
            steamGridDbApiKey = prefs[Keys.STEAMGRIDDB_API_KEY] ?: "",
            retroAchievementsUsername = prefs[Keys.RA_USERNAME] ?: "",
            retroAchievementsApiKey = prefs[Keys.RA_API_KEY] ?: "",
        )
    }

    override suspend fun updateScreenScraper(devId: String, devPassword: String, softName: String, ssid: String, ssPassword: String) {
        dataStore.edit {
            it[Keys.SS_DEV_ID] = devId.trim()
            it[Keys.SS_DEV_PASSWORD] = devPassword.trim()
            it[Keys.SS_SOFTNAME] = softName.trim()
            it[Keys.SS_SSID] = ssid.trim()
            it[Keys.SS_SSPASSWORD] = ssPassword.trim()
        }
    }

    override suspend fun updateIgdb(clientId: String, clientSecret: String) {
        dataStore.edit {
            it[Keys.IGDB_CLIENT_ID] = clientId.trim()
            it[Keys.IGDB_CLIENT_SECRET] = clientSecret.trim()
        }
    }

    override suspend fun updateSteamGridDb(apiKey: String) {
        dataStore.edit { it[Keys.STEAMGRIDDB_API_KEY] = apiKey.trim() }
    }

    override suspend fun updateRetroAchievements(username: String, apiKey: String) {
        dataStore.edit {
            it[Keys.RA_USERNAME] = username.trim()
            it[Keys.RA_API_KEY] = apiKey.trim()
        }
    }
}
