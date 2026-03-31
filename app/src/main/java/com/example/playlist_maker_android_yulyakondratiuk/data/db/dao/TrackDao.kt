package com.example.playlist_maker_android_yulyakondratiuk.data.db.dao

import androidx.room.*
import com.example.playlist_maker_android_yulyakondratiuk.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY trackName")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE playlistId = :playlistId")
    fun getTracksByPlaylistId(playlistId: Long): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Long): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("UPDATE tracks SET playlistId = :playlistId WHERE trackId = :trackId")
    suspend fun updateTrackPlaylist(trackId: Long, playlistId: Long)

    @Query("UPDATE tracks SET isFavorite = :isFavorite WHERE trackId = :trackId")
    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean)

    @Query("DELETE FROM tracks WHERE playlistId = :playlistId")
    suspend fun deleteTracksByPlaylistId(playlistId: Long)
}