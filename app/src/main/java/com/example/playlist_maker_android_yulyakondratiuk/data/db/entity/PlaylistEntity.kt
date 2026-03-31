package com.example.playlist_maker_android_yulyakondratiuk.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)