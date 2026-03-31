package com.example.playlist_maker_android_yulyakondratiuk.domain

import com.example.playlist_maker_android_yulyakondratiuk.data.dto.BaseResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): BaseResponse
}