package com.example.playlist_maker_android_yulyakondratiuk.data.network

import com.example.playlist_maker_android_yulyakondratiuk.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): TracksSearchResponse
}