package com.example.playlist_maker_android_yulyakondratiuk.data.network

import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DatabaseMock {
    private val historyList = mutableListOf<String>()
    private val playlists = mutableListOf<Playlist>()
    private val tracks = mutableListOf<Track>()

    fun getHistory(): List<String> {
        return historyList.toList()
    }

    fun addToHistory(word: String) {
        historyList.add(word)
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        delay(100)
        val filteredPlaylists = mutableListOf<Playlist>()
        playlists.forEach { playlist ->
            val playlistTracks = tracks.filter { track ->
                track.playlistId == playlist.id
            }.toList()
            filteredPlaylists.add(playlist.copy(tracks = playlistTracks))
            println("DatabaseMock: Playlist ${playlist.name} has ${playlistTracks.size} tracks")
        }
        println("DatabaseMock: Returning ${filteredPlaylists.size} playlists")
        emit(filteredPlaylists.toList())
    }

    fun getPlaylist(id: Long): Flow<Playlist?> = flow {
        val playlist = playlists.find { it.id == id }
        if (playlist != null) {
            val playlistTracks = tracks.filter { track ->
                track.playlistId == playlist.id
            }.toList()
            val updatedPlaylist = playlist.copy(tracks = playlistTracks)
            println("DatabaseMock: Getting playlist ${playlist.name} with ${playlistTracks.size} tracks")
            emit(updatedPlaylist)
        } else {
            emit(null)
        }
    }

    fun addNewPlaylist(name: String, description: String) {
        val newPlaylist = Playlist(
            id = playlists.size.toLong() + 1,
            name = name,
            description = description,
            tracks = emptyList()
        )
        playlists.add(newPlaylist)
        println("DatabaseMock: Added playlist '$name', total playlists: ${playlists.size}")
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        tracks.removeIf { it.id == trackId }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        emit(tracks.find { it.trackName == track.trackName && it.artistName == track.artistName })
    }

    fun insertTrack(track: Track) {
        tracks.removeIf { it.id == track.id }
        tracks.add(track)
    }

    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        delay(300)
        val favorites = tracks.filter { it.favorite }
        emit(favorites)
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracks.removeIf { it.playlistId == playlistId }
    }

    fun searchTracks(expression: String): List<Track> {
        return tracks.filter { it.trackName.contains(expression, true) }
    }
}