package com.elyndra.app.domain.model

data class Game(
    val id: Long = 0,
    val title: String,
    val platformId: String,
    /** A SAF content:// document URI for ROMs, or a bare Android package name when [isNativeApp]. */
    val romUri: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val coverImagePath: String? = null,
    val logoImagePath: String? = null,
    val screenshotImagePath: String? = null,
    /** Wide/hero art for platform and game detail banners - distinct from [coverImagePath]. */
    val backgroundImagePath: String? = null,
    val description: String? = null,
    val developer: String? = null,
    val genre: String? = null,
    val releaseDate: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long? = null,
    val totalPlaytimeSeconds: Long = 0,
    val crc32: String? = null,
    /** True when [romUri] holds a package name to launch directly, not a ROM to hand to an emulator. */
    val isNativeApp: Boolean = false,
)
