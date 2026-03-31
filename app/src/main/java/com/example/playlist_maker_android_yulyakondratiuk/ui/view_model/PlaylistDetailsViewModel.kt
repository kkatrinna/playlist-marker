package com.example.playlist_maker_android_yulyakondratiuk.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_yulyakondratiuk.creator.Creator
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.domain.PlaylistsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    application: Application,
    private val playlistId: Long
) : AndroidViewModel(application) {

    private val playlistsRepository: PlaylistsRepository = Creator.getPlaylistsRepository()

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist.asStateFlow()

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            playlistsRepository.getPlaylist(playlistId).collect { playlist ->
                _playlist.value = playlist
            }
        }
    }
}