package com.example.playlist_maker_android_yulyakondratiuk.data.repository

import com.example.playlist_maker_android_yulyakondratiuk.data.db.AppDatabase
import com.example.playlist_maker_android_yulyakondratiuk.data.db.entity.TrackEntity
import com.example.playlist_maker_android_yulyakondratiuk.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_yulyakondratiuk.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.domain.NetworkClient
import com.example.playlist_maker_android_yulyakondratiuk.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val database: AppDatabase
) : TracksRepository {

    private val trackDao = database.trackDao()

    override suspend fun searchTracks(expression: String): List<Track> =
        withContext(Dispatchers.IO) {
            val response = networkClient.doRequest(TracksSearchRequest(expression))

            if (response.resultCode == 200) {
                try {
                    val tracksResponse = response as TracksSearchResponse
                    tracksResponse.results.mapNotNull { itDto ->
                        if (itDto.trackName.isNullOrBlank() || itDto.artistName.isNullOrBlank()) {
                            return@mapNotNull null
                        }

                        val existingTrack = trackDao.getTrackById(itDto.trackId ?: 0)

                        Track(
                            trackId = itDto.trackId ?: 0,
                            trackName = itDto.trackName ?: "",
                            artistName = itDto.artistName ?: "",
                            trackTime = (itDto.trackTimeMillis ?: 0).toLong(),
                            artworkUrl100 = itDto.artworkUrl100 ?: "",
                            favorite = existingTrack?.isFavorite ?: false,
                            playlistId = existingTrack?.playlistId ?: 0
                        )
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    override suspend fun getTrackById(trackId: Long): Track? = withContext(Dispatchers.IO) {
        val entity = trackDao.getTrackById(trackId)
        entity?.let {
            Track(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTime = it.trackTime,
                artworkUrl100 = it.artworkUrl100,
                favorite = it.isFavorite,
                playlistId = it.playlistId
            )
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flowOf(null)

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { entities ->
            entities.map {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = it.trackTime,
                    artworkUrl100 = it.artworkUrl100,
                    favorite = it.isFavorite,
                    playlistId = it.playlistId
                )
            }
        }
    }

    override suspend fun saveTrack(track: Track) {
        trackDao.insertTrack(
            TrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                isFavorite = track.favorite,
                playlistId = track.playlistId
            )
        )
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        trackDao.insertTrack(
            TrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                isFavorite = track.favorite,
                playlistId = playlistId
            )
        )
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        trackDao.updateTrackPlaylist(track.trackId, 0)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val existingTrack = trackDao.getTrackById(track.trackId)
        if (existingTrack == null) {
            saveTrack(track)
        }
        trackDao.updateFavoriteStatus(track.trackId, isFavorite)
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        trackDao.deleteTracksByPlaylistId(playlistId)
    }
}