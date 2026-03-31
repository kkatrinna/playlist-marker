package com.example.playlist_maker_android_yulyakondratiuk

import android.app.Application
import com.example.playlist_maker_android_yulyakondratiuk.creator.Creator

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
    }
}