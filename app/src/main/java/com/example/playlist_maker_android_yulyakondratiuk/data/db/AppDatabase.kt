package com.example.playlist_maker_android_yulyakondratiuk.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.playlist_maker_android_yulyakondratiuk.data.db.dao.PlaylistDao
import com.example.playlist_maker_android_yulyakondratiuk.data.db.dao.TrackDao
import com.example.playlist_maker_android_yulyakondratiuk.data.db.entity.PlaylistEntity
import com.example.playlist_maker_android_yulyakondratiuk.data.db.entity.TrackEntity

@Database(
    entities = [PlaylistEntity::class, TrackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playlist_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}