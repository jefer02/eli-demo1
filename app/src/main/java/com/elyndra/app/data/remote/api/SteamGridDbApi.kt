package com.elyndra.app.data.remote.api

import com.elyndra.app.data.remote.dto.SteamGridDbImagesResponse
import com.elyndra.app.data.remote.dto.SteamGridDbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SteamGridDbApi {
    @GET("search/autocomplete/{term}")
    suspend fun searchGames(
        @Header("Authorization") authorization: String,
        @Path("term") term: String,
    ): SteamGridDbSearchResponse

    /** Portrait cover art - what Elyndra uses for [com.elyndra.app.domain.model.Game.coverImagePath]. */
    @GET("grids/game/{gameId}")
    suspend fun getGrids(
        @Header("Authorization") authorization: String,
        @Path("gameId") gameId: Int,
        @Query("dimensions") dimensions: String = "600x900",
    ): SteamGridDbImagesResponse

    /** Wide banner art - what Elyndra uses for [com.elyndra.app.domain.model.Game.backgroundImagePath]. */
    @GET("heroes/game/{gameId}")
    suspend fun getHeroes(
        @Header("Authorization") authorization: String,
        @Path("gameId") gameId: Int,
    ): SteamGridDbImagesResponse
}
