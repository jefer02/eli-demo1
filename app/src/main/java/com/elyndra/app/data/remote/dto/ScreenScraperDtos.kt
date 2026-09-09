package com.elyndra.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScreenScraperEnvelope(
    val response: ScreenScraperResponseBody? = null,
)

@Serializable
data class ScreenScraperResponseBody(
    val jeu: ScreenScraperGameDto? = null,
)

@Serializable
data class ScreenScraperGameDto(
    val id: String? = null,
    val noms: List<ScreenScraperLocalizedTextDto> = emptyList(),
    val synopsis: List<ScreenScraperLocalizedTextDto> = emptyList(),
    val systeme: ScreenScraperNamedRefDto? = null,
    val developpeur: ScreenScraperNamedRefDto? = null,
    val editeur: ScreenScraperNamedRefDto? = null,
    val genres: List<ScreenScraperGenreDto> = emptyList(),
    val dates: List<ScreenScraperLocalizedTextDto> = emptyList(),
    val medias: List<ScreenScraperMediaDto> = emptyList(),
)

@Serializable
data class ScreenScraperLocalizedTextDto(
    val region: String? = null,
    val langue: String? = null,
    val text: String = "",
)

@Serializable
data class ScreenScraperNamedRefDto(
    val id: String? = null,
    val text: String? = null,
)

@Serializable
data class ScreenScraperGenreDto(
    val id: String? = null,
    val noms: List<ScreenScraperLocalizedTextDto> = emptyList(),
)

@Serializable
data class ScreenScraperMediaDto(
    val type: String? = null,
    val url: String? = null,
    val region: String? = null,
)
