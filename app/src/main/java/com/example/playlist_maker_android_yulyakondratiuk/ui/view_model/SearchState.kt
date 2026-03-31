package com.example.playlist_maker_android_yulyakondratiuk.ui.view_model

import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track

sealed class SearchState {
    object Initial : SearchState()
    object Searching : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()
    data class Fail(val error: String) : SearchState()
    object ConnectionError : SearchState()
}