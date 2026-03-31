package com.example.playlist_maker_android_yulyakondratiuk

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Search : Screen("search")
    object Playlists : Screen("playlists")
    object Settings : Screen("settings")
    object Favorites : Screen("favorites")
    object NewPlaylist : Screen("new_playlist")
    object EditPlaylist : Screen("edit_playlist/{playlistId}") {
        fun passId(playlistId: Long): String = "edit_playlist/$playlistId"
    }
    object PlaylistDetail : Screen("playlist_detail/{playlistId}") {
        fun passId(playlistId: Long): String = "playlist_detail/$playlistId"
    }
    object TrackDetail : Screen("track_detail/{trackId}") {
        fun passId(trackId: Long): String = "track_detail/$trackId"
    }
}