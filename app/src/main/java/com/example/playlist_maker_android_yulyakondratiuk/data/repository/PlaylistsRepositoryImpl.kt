package com.example.playlist_maker_android_yulyakondratiuk.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.playlist_maker_android_yulyakondratiuk.data.db.AppDatabase
import com.example.playlist_maker_android_yulyakondratiuk.data.db.entity.PlaylistEntity
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val database: AppDatabase,
    private val context: Context
) : PlaylistsRepository {

    private val playlistDao = database.playlistDao()
    private val trackDao = database.trackDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId).map { entity ->
            entity?.let { mapToPlaylist(it) }
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { mapToPlaylist(it) }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverBitmap: Bitmap?) {
        val coverPath = coverBitmap?.let { saveBitmapToFile(it) }
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverPath = coverPath
            )
        )
    }

    override suspend fun updatePlaylist(playlistId: Long, name: String, description: String, coverBitmap: Bitmap?) {
        val existingPlaylist = playlistDao.getPlaylistById(playlistId).first()
        existingPlaylist?.let {
            val coverPath = if (coverBitmap != null) {
                saveBitmapToFile(coverBitmap)
            } else {
                it.coverPath
            }

            val updatedPlaylist = it.copy(
                name = name,
                description = description,
                coverPath = coverPath
            )
            playlistDao.updatePlaylist(updatedPlaylist)
        }
    }

    override suspend fun deletePlaylistById(id: Long) {
        val playlist = playlistDao.getPlaylistById(id).first()
        playlist?.coverPath?.let { path ->
            File(path).delete()
        }
        trackDao.deleteTracksByPlaylistId(id)
        playlistDao.deletePlaylistById(id)
    }

    private fun mapToPlaylist(entity: PlaylistEntity): Playlist {
        val trackEntities = runBlocking {
            trackDao.getTracksByPlaylistId(entity.id).first()
        }

        val tracks = trackEntities.map { trackEntity ->
            Track(
                trackId = trackEntity.trackId,
                trackName = trackEntity.trackName,
                artistName = trackEntity.artistName,
                trackTime = trackEntity.trackTime,
                artworkUrl100 = trackEntity.artworkUrl100,
                favorite = trackEntity.isFavorite,
                playlistId = trackEntity.playlistId
            )
        }

        val coverBitmap = entity.coverPath?.let { path ->
            loadBitmapFromFile(path)
        }

        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            tracks = tracks,
            coverBitmap = coverBitmap,
            coverPath = entity.coverPath
        )
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String {
        val file = File(context.filesDir, "playlist_covers")
        if (!file.exists()) {
            file.mkdirs()
        }
        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val imageFile = File(file, fileName)
        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        return imageFile.absolutePath
    }

    private fun loadBitmapFromFile(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            null
        }
    }
}