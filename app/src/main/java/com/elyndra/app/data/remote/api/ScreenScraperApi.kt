package com.elyndra.app.data.remote.api

import com.elyndra.app.data.remote.dto.ScreenScraperEnvelope
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * https://api.screenscraper.fr/api2/ - community-run, mildly-documented API.
 * `jeuInfos.php` matches a rom by crc/md5/romnom (+ optional systemeid to
 * disambiguate) and returns its metadata and media URLs.
 */
interface ScreenScraperApi {
    @GET("jeuInfos.php")
    suspend fun getGameInfo(
        @Query("devid") devId: String,
        @Query("devpassword") devPassword: String,
        @Query("softname") softName: String,
        @Query("ssid") ssid: String? = null,
        @Query("sspassword") ssPassword: String? = null,
        @Query("output") output: String = "json",
        @Query("crc") crc32: String? = null,
        @Query("romnom") romFileName: String? = null,
        @Query("systemeid") systemId: Int? = null,
    ): ScreenScraperEnvelope
}
