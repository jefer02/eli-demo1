package com.elyndra.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SteamGridDbSearchResponse(
    val success: Boolean = false,
    val data: List<SteamGridDbGameDto> = emptyList(),
)

@Serializable
data class SteamGridDbGameDto(
    val id: Int,
    val name: String? = null,
)

@Serializable
data class SteamGridDbImagesResponse(
    val success: Boolean = false,
    val data: List<SteamGridDbImageDto> = emptyList(),
)

@Serializable
data class SteamGridDbImageDto(
    val id: Int? = null,
    val url: String? = null,
)
