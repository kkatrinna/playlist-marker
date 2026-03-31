package com.example.playlist_maker_android_yulyakondratiuk.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.playlist_maker_android_yulyakondratiuk.PlaylistHost
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.PlaylistMarketTheme
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentTheme = ThemeManager.getCurrentTheme(this)
        ThemeManager.applyTheme(currentTheme)

        setContent {
            PlaylistMarketTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    PlaylistHost(navController = navController)
                }
            }
        }
    }
}