package com.elyndra.app.data.repository

import com.elyndra.app.data.local.dao.PlatformDao
import com.elyndra.app.data.mapper.toDomain
import com.elyndra.app.data.mapper.toEntity
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.repository.PlatformRepository
import com.elyndra.app.util.PlatformCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlatformRepositoryImpl @Inject constructor(
    private val platformDao: PlatformDao,
) : PlatformRepository {

    override fun observePlatforms(): Flow<List<Platform>> =
        platformDao.observePlatforms().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getPlatform(platformId: String): Platform? =
        platformDao.getPlatform(platformId)?.toDomain()

    override suspend fun getAllPlatforms(): List<Platform> =
        platformDao.getAllPlatforms().map { it.toDomain() }

    override suspend fun ensureSeeded() {
        // IGNORE-on-conflict: safe to call on every app start, so new catalog
        // entries (e.g. a future platform addition) reach existing installs
        // without disturbing already-configured platforms.
        platformDao.insertAll(PlatformCatalog.all.map { it.toEntity() })
    }

    override suspend fun setEmulatorConfig(
        platformId: String,
        packageName: String?,
        activityName: String?,
        action: String,
    ) = platformDao.setEmulatorConfig(platformId, packageName, activityName, action)
}
