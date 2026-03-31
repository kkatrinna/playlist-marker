package com.example.playlist_maker_android_yulyakondratiuk.data.network

import com.example.playlist_maker_android_yulyakondratiuk.creator.Storage
import com.example.playlist_maker_android_yulyakondratiuk.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_yulyakondratiuk.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_yulyakondratiuk.domain.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(
    private val storage: Storage,
    private val api: ITunesApi = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ITunesApi::class.java)
) : NetworkClient {

    override suspend fun doRequest(request: Any): TracksSearchResponse {
        return when (request) {
            is TracksSearchRequest -> {
                try {
                    val response = api.searchTracks(request.expression)
                    response.apply { resultCode = 200 }
                } catch (e: Exception) {
                    TracksSearchResponse(emptyList()).apply { resultCode = 400 }
                }
            }
            else -> TracksSearchResponse(emptyList()).apply { resultCode = 400 }
        }
    }
}