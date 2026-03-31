package com.example.playlist_maker_android_yulyakondratiuk.domain

import android.graphics.Bitmap
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val tracks: List<Track> = emptyList(),
    val coverBitmap: Bitmap? = null,
    val coverPath: String? = null
) {
    val tracksCount: Int
        get() = tracks.size

    val totalDuration: Long
        get() = tracks.sumOf { it.trackTime }
}