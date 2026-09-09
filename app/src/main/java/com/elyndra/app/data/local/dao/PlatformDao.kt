package com.elyndra.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elyndra.app.data.local.entity.PlatformEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatformDao {

    @Query("SELECT * FROM platforms ORDER BY sortOrder ASC, displayName ASC")
    fun observePlatforms(): Flow<List<PlatformEntity>>

    @Query("SELECT * FROM platforms ORDER BY sortOrder ASC, displayName ASC")
    suspend fun getAllPlatforms(): List<PlatformEntity>

    @Query("SELECT * FROM platforms WHERE id = :platformId")
    suspend fun getPlatform(platformId: String): PlatformEntity?

    @Query("SELECT COUNT(*) FROM platforms")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(platforms: List<PlatformEntity>)

    @Query(
        "UPDATE platforms SET emulatorPackageName = :packageName, " +
            "emulatorActivityName = :activityName, emulatorAction = :action WHERE id = :platformId",
    )
    suspend fun setEmulatorConfig(
        platformId: String,
        packageName: String?,
        activityName: String?,
        action: String,
    )
}
