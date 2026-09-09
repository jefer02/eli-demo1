package com.elyndra.app.di

import com.elyndra.app.BuildConfig
import com.elyndra.app.data.remote.api.IgdbApi
import com.elyndra.app.data.remote.api.RetroAchievementsApi
import com.elyndra.app.data.remote.api.ScreenScraperApi
import com.elyndra.app.data.remote.api.SteamGridDbApi
import com.elyndra.app.data.remote.api.TwitchAuthApi
import com.elyndra.app.util.Constants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private fun retrofitFor(baseUrl: String, okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideScreenScraperApi(okHttpClient: OkHttpClient, json: Json): ScreenScraperApi =
        retrofitFor(Constants.SCREENSCRAPER_BASE_URL, okHttpClient, json).create(ScreenScraperApi::class.java)

    @Provides
    @Singleton
    fun provideTwitchAuthApi(okHttpClient: OkHttpClient, json: Json): TwitchAuthApi =
        retrofitFor(Constants.TWITCH_OAUTH_BASE_URL, okHttpClient, json).create(TwitchAuthApi::class.java)

    @Provides
    @Singleton
    fun provideIgdbApi(okHttpClient: OkHttpClient, json: Json): IgdbApi =
        retrofitFor(Constants.IGDB_BASE_URL, okHttpClient, json).create(IgdbApi::class.java)

    @Provides
    @Singleton
    fun provideSteamGridDbApi(okHttpClient: OkHttpClient, json: Json): SteamGridDbApi =
        retrofitFor(Constants.STEAMGRIDDB_BASE_URL, okHttpClient, json).create(SteamGridDbApi::class.java)

    @Provides
    @Singleton
    fun provideRetroAchievementsApi(okHttpClient: OkHttpClient, json: Json): RetroAchievementsApi =
        retrofitFor(Constants.RETROACHIEVEMENTS_BASE_URL, okHttpClient, json).create(RetroAchievementsApi::class.java)
}
