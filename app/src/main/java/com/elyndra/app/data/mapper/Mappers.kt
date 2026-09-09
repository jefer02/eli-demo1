package com.elyndra.app.data.mapper

import com.elyndra.app.data.local.entity.GameEntity
import com.elyndra.app.data.local.entity.PlatformEntity
import com.elyndra.app.data.local.entity.PlaySessionEntity
import com.elyndra.app.data.local.entity.RomFolderEntity
import com.elyndra.app.domain.model.Game
import com.elyndra.app.domain.model.Platform
import com.elyndra.app.domain.model.PlaySession
import com.elyndra.app.domain.model.RomFolder

fun GameEntity.toDomain() = Game(
    id = id,
    title = title,
    platformId = platformId,
    romUri = romUri,
    fileName = fileName,
    fileSizeBytes = fileSizeBytes,
    coverImagePath = coverImagePath,
    logoImagePath = logoImagePath,
    screenshotImagePath = screenshotImagePath,
    backgroundImagePath = backgroundImagePath,
    description = description,
    developer = developer,
    genre = genre,
    releaseDate = releaseDate,
    isFavorite = isFavorite,
    isHidden = isHidden,
    dateAdded = dateAdded,
    lastPlayedAt = lastPlayedAt,
    totalPlaytimeSeconds = totalPlaytimeSeconds,
    crc32 = crc32,
    isNativeApp = isNativeApp,
)

fun Game.toEntity() = GameEntity(
    id = id,
    title = title,
    platformId = platformId,
    romUri = romUri,
    fileName = fileName,
    fileSizeBytes = fileSizeBytes,
    coverImagePath = coverImagePath,
    logoImagePath = logoImagePath,
    screenshotImagePath = screenshotImagePath,
    backgroundImagePath = backgroundImagePath,
    description = description,
    developer = developer,
    genre = genre,
    releaseDate = releaseDate,
    isFavorite = isFavorite,
    isHidden = isHidden,
    dateAdded = dateAdded,
    lastPlayedAt = lastPlayedAt,
    totalPlaytimeSeconds = totalPlaytimeSeconds,
    crc32 = crc32,
    isNativeApp = isNativeApp,
)

fun PlatformEntity.toDomain() = Platform(
    id = id,
    displayName = displayName,
    shortName = shortName,
    extensions = extensions,
    folderAliases = folderAliases,
    emulatorPackageName = emulatorPackageName,
    emulatorActivityName = emulatorActivityName,
    emulatorAction = emulatorAction,
    sortOrder = sortOrder,
)

fun Platform.toEntity() = PlatformEntity(
    id = id,
    displayName = displayName,
    shortName = shortName,
    extensions = extensions,
    folderAliases = folderAliases,
    emulatorPackageName = emulatorPackageName,
    emulatorActivityName = emulatorActivityName,
    emulatorAction = emulatorAction,
    sortOrder = sortOrder,
)

fun PlaySessionEntity.toDomain() = PlaySession(
    id = id,
    gameId = gameId,
    startedAt = startedAt,
    endedAt = endedAt,
    durationSeconds = durationSeconds,
)

fun PlaySession.toEntity() = PlaySessionEntity(
    id = id,
    gameId = gameId,
    startedAt = startedAt,
    endedAt = endedAt,
    durationSeconds = durationSeconds,
)

fun RomFolderEntity.toDomain() = RomFolder(
    id = id,
    treeUri = treeUri,
    displayPath = displayPath,
    dateAdded = dateAdded,
    lastScannedAt = lastScannedAt,
)

fun RomFolder.toEntity() = RomFolderEntity(
    id = id,
    treeUri = treeUri,
    displayPath = displayPath,
    dateAdded = dateAdded,
    lastScannedAt = lastScannedAt,
)
