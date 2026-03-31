package com.example.playlist_maker_android_yulyakondratiuk.creator

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.playlist_maker_android_yulyakondratiuk.data.db.AppDatabase
import com.example.playlist_maker_android_yulyakondratiuk.data.network.RetrofitNetworkClient
import com.example.playlist_maker_android_yulyakondratiuk.data.repository.TracksRepositoryImpl
import com.example.playlist_maker_android_yulyakondratiuk.data.db.preferences.SearchHistoryPreferences
import com.example.playlist_maker_android_yulyakondratiuk.data.repository.PlaylistsRepositoryImpl
import com.example.playlist_maker_android_yulyakondratiuk.domain.PlaylistsRepository
import com.example.playlist_maker_android_yulyakondratiuk.domain.TracksRepository

object Creator {
    private lateinit var appContext: Context
    private lateinit var application: Application
    private lateinit var database: AppDatabase
    private lateinit var searchHistoryPreferences: SearchHistoryPreferences
    private val storage = Storage()

    fun init(context: Context) {
        appContext = context.applicationContext
        application = context.applicationContext as Application
        database = AppDatabase.getInstance(appContext)
        searchHistoryPreferences = SearchHistoryPreferences(appContext)
    }

    fun getApplication(): Application {
        return application
    }

    fun getAppContext(): Context {
        return appContext
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(database, appContext)
    }

    fun getTracksRepository(): TracksRepository {
        val networkClient = RetrofitNetworkClient(storage)
        return TracksRepositoryImpl(networkClient, database)
    }

    fun getSearchHistoryPreferences(): SearchHistoryPreferences {
        return searchHistoryPreferences
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}