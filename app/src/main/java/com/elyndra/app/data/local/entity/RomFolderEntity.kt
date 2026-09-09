package com.elyndra.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rom_folders", indices = [Index(value = ["treeUri"], unique = true)])
data class RomFolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val treeUri: String,
    val displayPath: String,
    val dateAdded: Long,
    val lastScannedAt: Long?,
)
