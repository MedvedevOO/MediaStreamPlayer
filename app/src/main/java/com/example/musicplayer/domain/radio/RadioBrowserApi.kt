package com.example.musicplayer.domain.radio

import com.example.musicplayer.domain.model.RadioCountry
import com.example.musicplayer.domain.model.RadioLanguage
import com.example.musicplayer.domain.model.RadioStation
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadioBrowserApi {

    @GET("json/stations/topclick/{count}")
    suspend fun getTopStations(
        @Path("count") count: Int,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStation>

    @GET("json/stations/topvote/{count}")
    suspend fun getTopRatedStations(
        @Path("count") count: Int,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStation>

    @GET("json/stations/lastchange/{count}")
    suspend fun getRecentlyChangedStations(
        @Path("count") count: Int,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStation>

    @GET("json/countries")
    suspend fun getCountryList(
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioCountry>

    @GET("json/languages")
    suspend fun getLanguageList(
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioLanguage>

    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("country") country: String? = null,
        @Query("language") language: String? = null
    ): List<RadioStation>

    @GET("json/stations/byuuid/{uuid}")
    suspend fun getStationByUUID(
        @Path("uuid") uuid: String
    ): RadioStation
}