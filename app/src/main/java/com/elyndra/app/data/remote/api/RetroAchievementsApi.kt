package com.elyndra.app.data.remote.api

import com.elyndra.app.data.remote.dto.RetroAchievementsGameDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RetroAchievementsApi {
    @GET("API_GetGame.php")
    suspend fun getGame(
        @Query("i") gameId: Int,
        @Query("y") apiKey: String,
    ): RetroAchievementsGameDto
}
