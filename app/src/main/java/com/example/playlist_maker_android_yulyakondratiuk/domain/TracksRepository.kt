package com.example.playlist_maker_android_yulyakondratiuk.domain

import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
    suspend fun getTrackById(trackId: Long): Track?
    fun getTrackByNameAndArtist(track: Track): Flow<Track?>
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun deleteTrackFromPlaylist(track: Track)
    suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean)
    suspend fun deleteTracksByPlaylistId(playlistId: Long)
    suspend fun saveTrack(track: Track)
}