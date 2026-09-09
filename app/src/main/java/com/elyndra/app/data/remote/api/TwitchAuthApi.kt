package com.elyndra.app.data.remote.api

import com.elyndra.app.data.remote.dto.TwitchTokenResponse
import retrofit2.http.POST
import retrofit2.http.Query

/** Twitch's OAuth2 app-token endpoint - IGDB authenticates through Twitch, not its own login. */
interface TwitchAuthApi {
    @POST("oauth2/token")
    suspend fun getAppAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "client_credentials",
    ): TwitchTokenResponse
}
