package com.elyndra.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwitchTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
)

@Serializable
data class IgdbGameDto(
    val id: Int? = null,
    val name: String? = null,
    val summary: String? = null,
    val cover: IgdbImageDto? = null,
    val screenshots: List<IgdbImageDto>? = null,
    val genres: List<IgdbGenreDto>? = null,
    @SerialName("involved_companies") val involvedCompanies: List<IgdbInvolvedCompanyDto>? = null,
    @SerialName("first_release_date") val firstReleaseDate: Long? = null,
)

@Serializable
data class IgdbImageDto(@SerialName("image_id") val imageId: String? = null)

@Serializable
data class IgdbGenreDto(val name: String? = null)

@Serializable
data class IgdbInvolvedCompanyDto(
    val company: IgdbCompanyRefDto? = null,
    val developer: Boolean? = null,
)

@Serializable
data class IgdbCompanyRefDto(val name: String? = null)
