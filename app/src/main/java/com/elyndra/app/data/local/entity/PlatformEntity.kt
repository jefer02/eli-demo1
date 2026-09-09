package com.elyndra.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platforms")
data class PlatformEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val shortName: String,
    val extensions: List<String>,
    val folderAliases: List<String>,
    val emulatorPackageName: String?,
    val emulatorActivityName: String?,
    val emulatorAction: String,
    val sortOrder: Int,
)
