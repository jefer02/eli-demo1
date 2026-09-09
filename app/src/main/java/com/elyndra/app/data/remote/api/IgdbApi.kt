package com.elyndra.app.data.remote.api

import com.elyndra.app.data.remote.dto.IgdbGameDto
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 * IGDB v4 speaks "Apicalypse" - a plain-text query language in the POST body,
 * not JSON - so the body is a raw [RequestBody] built by the caller rather
 * than a serialized DTO. The response is ordinary JSON.
 */
interface IgdbApi {
    @POST("games")
    suspend fun searchGames(
        @HeaderMap headers: Map<String, String>,
        @Body query: RequestBody,
    ): List<IgdbGameDto>
}
