package com.example.playlist_maker_android_yulyakondratiuk.ui.view_model

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_yulyakondratiuk.creator.Creator
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.domain.PlaylistsRepository
import com.example.playlist_maker_android_yulyakondratiuk.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlaylistsViewModel() : ViewModel() {

    private val playlistsRepository: PlaylistsRepository = Creator.getPlaylistsRepository()
    private val tracksRepository: TracksRepository = Creator.getTracksRepository()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    val favoriteList = tracksRepository.getFavoriteTracks()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistsRepository.getAllPlaylists().collect { list ->
                _playlists.value = list
            }
        }
    }

    fun refreshPlaylists() {
        viewModelScope.launch {
            val newList = playlistsRepository.getAllPlaylists().firstOrNull() ?: emptyList()
            _playlists.value = newList
        }
    }

    fun getPlaylist(id: Long) = playlistsRepository.getPlaylist(id)

    fun createNewPlayList(namePlaylist: String, description: String, coverBitmap: Bitmap? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(namePlaylist, description, coverBitmap)
            refreshPlaylists()
        }
    }

    fun updatePlaylist(playlistId: Long, name: String, description: String, coverBitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.updatePlaylist(playlistId, name, description, coverBitmap)
            refreshPlaylists()
        }
    }

    suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksRepository.deleteTrackFromPlaylist(track)
        refreshPlaylists()
    }

    suspend fun deletePlaylistById(id: Long) {
        tracksRepository.deleteTracksByPlaylistId(id)
        playlistsRepository.deletePlaylistById(id)
        refreshPlaylists()
    }

    suspend fun mergePlaylists(sourcePlaylistId: Long, targetPlaylistId: Long): Boolean {
        return try {
            val sourcePlaylist = playlistsRepository.getPlaylist(sourcePlaylistId).firstOrNull()
            val targetPlaylist = playlistsRepository.getPlaylist(targetPlaylistId).firstOrNull()

            if (sourcePlaylist != null && targetPlaylist != null) {
                sourcePlaylist.tracks.forEach { track ->
                    tracksRepository.insertTrackToPlaylist(track, targetPlaylistId)
                }
                playlistsRepository.deletePlaylistById(sourcePlaylistId)
                refreshPlaylists()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun isExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track = track).firstOrNull()
    }
}