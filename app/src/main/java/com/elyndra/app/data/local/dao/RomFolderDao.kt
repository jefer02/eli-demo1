package com.elyndra.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.elyndra.app.data.local.entity.RomFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RomFolderDao {

    @Query("SELECT * FROM rom_folders ORDER BY dateAdded ASC")
    fun observeFolders(): Flow<List<RomFolderEntity>>

    @Query("SELECT * FROM rom_folders ORDER BY dateAdded ASC")
    suspend fun getFolders(): List<RomFolderEntity>

    @Insert
    suspend fun insert(folder: RomFolderEntity): Long

    @Query("DELETE FROM rom_folders WHERE id = :folderId")
    suspend fun deleteById(folderId: Long)

    @Query("UPDATE rom_folders SET lastScannedAt = :timestamp WHERE id = :folderId")
    suspend fun markScanned(folderId: Long, timestamp: Long)
}
