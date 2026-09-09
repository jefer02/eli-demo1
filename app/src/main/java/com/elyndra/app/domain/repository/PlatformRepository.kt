package com.elyndra.app.domain.repository

import com.elyndra.app.domain.model.Platform
import kotlinx.coroutines.flow.Flow

interface PlatformRepository {
    fun observePlatforms(): Flow<List<Platform>>
    suspend fun getPlatform(platformId: String): Platform?
    suspend fun getAllPlatforms(): List<Platform>

    /** Inserts the built-in platform catalog on first run; no-op afterwards. */
    suspend fun ensureSeeded()

    suspend fun setEmulatorConfig(
        platformId: String,
        packageName: String?,
        activityName: String?,
        action: String,
    )
}
