package com.example.playlist_maker_android_yulyakondratiuk

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.playlist_maker_android_yulyakondratiuk.ui.screens.*
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.PlaylistsViewModel
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.TrackDetailsViewModel
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track

@Composable
fun PlaylistHost(navController: NavHostController) {
    PlaylistHostContent(
        navController = navController
    )
}

@Composable
fun PlaylistHostContent(
    navController: NavHostController
) {
    val playlistsViewModel: PlaylistsViewModel = viewModel()

    Scaffold(
        modifier = Modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Main.route) {
                MainScreen(
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onPlaylistsClick = {
                        navController.navigate(Screen.Playlists.route)
                    },
                    onFavoritesClick = {
                        navController.navigate(Screen.Favorites.route)
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onTrackClick = { track: Track ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("track", track)
                        navController.navigate(Screen.TrackDetail.passId(track.trackId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Playlists.route) {
                PlaylistsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCreatePlaylistClick = {
                        navController.navigate(Screen.NewPlaylist.route)
                    },
                    onPlaylistClick = { playlistId: Long ->
                        navController.navigate(Screen.PlaylistDetail.passId(playlistId))
                    },
                    viewModel = playlistsViewModel
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onTrackClick = { trackId: Long ->
                        navController.navigate(Screen.TrackDetail.passId(trackId))
                    },
                    viewModel = playlistsViewModel
                )
            }

            composable(Screen.NewPlaylist.route) {
                NewPlaylistScreen(
                    viewModel = playlistsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.EditPlaylist.route,
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                val playlist by playlistsViewModel.getPlaylist(playlistId).collectAsState(initial = null)

                playlist?.let {
                    NewPlaylistScreen(
                        viewModel = playlistsViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        playlistToEdit = it
                    )
                }
            }

            composable(
                route = Screen.PlaylistDetail.route,
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                PlaylistDetailScreen(
                    playlistId = playlistId,
                    viewModel = playlistsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onTrackClick = { trackId: Long ->
                        navController.navigate(Screen.TrackDetail.passId(trackId))
                    },
                    onEditClick = { playlist ->
                        navController.navigate(Screen.EditPlaylist.passId(playlist.id))
                    }
                )
            }

            composable(
                route = Screen.TrackDetail.route,
                arguments = listOf(navArgument("trackId") { type = NavType.LongType })
            ) { backStackEntry ->
                val trackId = backStackEntry.arguments?.getLong("trackId") ?: 0L
                val track = navController.previousBackStackEntry?.savedStateHandle?.get<Track>("track")

                val trackDetailsViewModel: TrackDetailsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return TrackDetailsViewModel(playlistsViewModel) as T
                        }
                    }
                )

                TrackDetailsScreen(
                    trackId = trackId,
                    track = track,
                    viewModel = trackDetailsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}