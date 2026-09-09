package com.elyndra.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.elyndra.app.data.local.dao.GameDao
import com.elyndra.app.data.local.dao.PlatformDao
import com.elyndra.app.data.local.dao.PlaySessionDao
import com.elyndra.app.data.local.dao.RomFolderDao
import com.elyndra.app.data.local.entity.GameEntity
import com.elyndra.app.data.local.entity.PlatformEntity
import com.elyndra.app.data.local.entity.PlaySessionEntity
import com.elyndra.app.data.local.entity.RomFolderEntity

@Database(
    entities = [
        GameEntity::class,
        PlatformEntity::class,
        PlaySessionEntity::class,
        RomFolderEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ElyndraDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun platformDao(): PlatformDao
    abstract fun playSessionDao(): PlaySessionDao
    abstract fun romFolderDao(): RomFolderDao

    companion object {
        const val DATABASE_NAME = "elyndra.db"
    }
}
