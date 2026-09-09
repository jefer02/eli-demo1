package com.elyndra.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    foreignKeys = [
        ForeignKey(
            entity = PlatformEntity::class,
            parentColumns = ["id"],
            childColumns = ["platformId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["romUri"], unique = true),
        Index(value = ["platformId"]),
        Index(value = ["isFavorite"]),
        Index(value = ["isHidden"]),
    ],
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val platformId: String,
    val romUri: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val coverImagePath: String?,
    val logoImagePath: String?,
    val screenshotImagePath: String?,
    val backgroundImagePath: String? = null,
    val description: String?,
    val developer: String?,
    val genre: String?,
    val releaseDate: String?,
    val isFavorite: Boolean,
    val isHidden: Boolean,
    val dateAdded: Long,
    val lastPlayedAt: Long?,
    val totalPlaytimeSeconds: Long,
    val crc32: String?,
    val isNativeApp: Boolean = false,
)
