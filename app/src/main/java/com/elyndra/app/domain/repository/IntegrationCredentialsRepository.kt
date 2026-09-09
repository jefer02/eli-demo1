package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.IntegrationCredentials
import kotlinx.coroutines.flow.Flow

interface IntegrationCredentialsRepository {
    val credentials: Flow<IntegrationCredentials>

    suspend fun updateScreenScraper(devId: String, devPassword: String, softName: String, ssid: String, ssPassword: String)
    suspend fun updateIgdb(clientId: String, clientSecret: String)
    suspend fun updateSteamGridDb(apiKey: String)
    suspend fun updateRetroAchievements(username: String, apiKey: String)
}
