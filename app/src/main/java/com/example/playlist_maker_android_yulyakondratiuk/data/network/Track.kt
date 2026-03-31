package com.example.playlist_maker_android_yulyakondratiuk.data.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String? = null,
    var favorite: Boolean = false,
    var playlistId: Long = 0
) : Parcelable {
    val id: Long
        get() = trackId
}