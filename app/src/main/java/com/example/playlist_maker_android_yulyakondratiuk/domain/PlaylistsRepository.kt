package com.example.playlist_maker_android_yulyakondratiuk.domain

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylist(playlistId: Long): Flow<Playlist?>
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addNewPlaylist(name: String, description: String, coverBitmap: Bitmap? = null)
    suspend fun updatePlaylist(playlistId: Long, name: String, description: String, coverBitmap: Bitmap?)
    suspend fun deletePlaylistById(id: Long)
}