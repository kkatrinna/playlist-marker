package com.example.playlist_maker_android_yulyakondratiuk.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String?,
    val isFavorite: Boolean = false,
    val playlistId: Long = 0
)