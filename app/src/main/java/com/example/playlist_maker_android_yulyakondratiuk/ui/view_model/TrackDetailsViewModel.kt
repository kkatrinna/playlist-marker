package com.example.playlist_maker_android_yulyakondratiuk.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_yulyakondratiuk.creator.Creator
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.domain.PlaylistsRepository
import com.example.playlist_maker_android_yulyakondratiuk.domain.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TrackDetailsViewModel(
    private val playlistsViewModel: PlaylistsViewModel? = null
) : ViewModel() {

    private val tracksRepository: TracksRepository = Creator.getTracksRepository()
    private val playlistsRepository: PlaylistsRepository = Creator.getPlaylistsRepository()

    private val _track = MutableStateFlow<Track?>(null)
    val track: StateFlow<Track?> = _track.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistsRepository.getAllPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList
            }
        }
    }

    fun setTrack(track: Track) {
        _track.value = track
    }

    fun loadTrackById(trackId: Long) {
        viewModelScope.launch {
            val loadedTrack = tracksRepository.getTrackById(trackId)
            _track.value = loadedTrack
        }
    }

    fun addOrRemoveTrackFromPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistsRepository.getPlaylist(playlistId).firstOrNull()
            val isTrackInPlaylist = playlist?.tracks?.any { it.trackId == track.trackId } == true

            if (isTrackInPlaylist) {
                tracksRepository.deleteTrackFromPlaylist(track)
            } else {
                tracksRepository.insertTrackToPlaylist(track, playlistId)
            }

            loadPlaylists()
            playlistsViewModel?.refreshPlaylists()

            _track.value = track.copy(playlistId = if (isTrackInPlaylist) 0 else playlistId)
        }
    }

    fun toggleFavorite(track: Track, isFavorite: Boolean) {
        viewModelScope.launch {
            tracksRepository.saveTrack(track)
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
            _track.value = track.copy(favorite = isFavorite)
        }
    }
}