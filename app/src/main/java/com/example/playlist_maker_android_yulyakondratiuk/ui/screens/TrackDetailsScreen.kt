package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.playlist_maker_android_yulyakondratiuk.R
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.TrackDetailsViewModel
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.ThemeManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    trackId: Long,
    track: Track? = null,
    viewModel: TrackDetailsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModelTrack by viewModel.track.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val isDarkTheme = ThemeManager.isDarkThemeEnabled(context)

    val currentTrack = track ?: viewModelTrack

    val paddingLarge = dimensionResource(id = R.dimen.padding_large)
    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
    val paddingExtraLarge = dimensionResource(id = R.dimen.padding_extra_large)
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val largeIconSize = dimensionResource(id = R.dimen.large_icon_size)
    val fabSize = dimensionResource(id = R.dimen.fab_size)
    val cardCornerRadius = dimensionResource(id = R.dimen.card_corner_radius)
    val textSizeTitle = dimensionResource(id = R.dimen.text_size_title).value.sp
    val textSizeSmall = dimensionResource(id = R.dimen.text_size_small).value.sp

    LaunchedEffect(trackId, track) {
        if (track == null && viewModelTrack == null) {
            viewModel.loadTrackById(trackId)
        } else if (track != null && viewModelTrack == null) {
            viewModel.setTrack(track)
        }
    }

    currentTrack?.let { trackData ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(iconSize)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = paddingLarge)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(cardCornerRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    if (!trackData.artworkUrl100.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(trackData.artworkUrl100)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.track_icon_desc),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.ic_music),
                            placeholder = painterResource(id = R.drawable.ic_music)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_music),
                                contentDescription = stringResource(R.string.track_icon_desc),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(largeIconSize)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(paddingExtraLarge))

                Text(
                    text = trackData.trackName,
                    fontSize = textSizeTitle,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(paddingMedium))

                Text(
                    text = trackData.artistName,
                    fontSize = textSizeSmall,
                    color = if (isDarkTheme)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    else colorResource(id = R.color.gray_900),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(paddingExtraLarge))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(fabSize)
                            .clip(CircleShape)
                            .background(
                                if (isDarkTheme)
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                else
                                    Color(0x401A1B22)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showBottomSheet = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                            contentDescription = stringResource(R.string.add_to_playlist),
                            tint = Color.White,
                            modifier = Modifier.size(iconSize)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(fabSize)
                            .clip(CircleShape)
                            .background(
                                if (isDarkTheme)
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                else
                                    Color(0x401A1B22)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                viewModel.toggleFavorite(trackData, !trackData.favorite)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (trackData.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.add_to_favorites),
                            tint = if (trackData.favorite) colorResource(id = R.color.accent_red) else Color.White,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(paddingExtraLarge))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.duration),
                        fontSize = textSizeSmall,
                        color = if (isDarkTheme)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else colorResource(id = R.color.gray_400),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatTrackTime(trackData.trackTime),
                        fontSize = textSizeSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        if (showBottomSheet) {
            SimplePlaylistSelectionSheet(
                playlists = playlists,
                currentTrack = trackData,
                onPlaylistSelected = { playlist ->
                    viewModel.addOrRemoveTrackFromPlaylist(trackData, playlist.id)
                    showBottomSheet = false
                },
                onDismiss = { showBottomSheet = false }
            )
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.primary_blue)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePlaylistSelectionSheet(
    playlists: List<Playlist>,
    currentTrack: Track,
    onPlaylistSelected: (Playlist) -> Unit,
    onDismiss: () -> Unit
) {
    val paddingLarge = dimensionResource(id = R.dimen.padding_large)
    val cardCornerRadius = dimensionResource(id = R.dimen.card_corner_radius)
    val dividerHeight = dimensionResource(id = R.dimen.divider_height)
    val playlistEmptyHeight = dimensionResource(id = R.dimen.playlist_empty_height)
    val textSizeTitle = dimensionResource(id = R.dimen.text_size_title).value.sp
    val isDarkTheme = ThemeManager.isDarkThemeEnabled(LocalContext.current)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = cardCornerRadius,
            topEnd = cardCornerRadius
        ),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge)
        ) {
            Text(
                text = stringResource(R.string.add_to_playlist_title),
                fontSize = textSizeTitle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingLarge),
                textAlign = TextAlign.Center
            )

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(playlistEmptyHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_playlists_available),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn {
                    items(playlists) { playlist ->
                        val isTrackInPlaylist = playlist.tracks.any { it.trackId == currentTrack.trackId }
                        PlaylistSheetItem(
                            playlist = playlist,
                            isSelected = isTrackInPlaylist,
                            onClick = { onPlaylistSelected(playlist) }
                        )
                        HorizontalDivider(
                            thickness = dividerHeight,
                            color = colorResource(id = if (isDarkTheme) R.color.divider_dark else R.color.divider_light)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistSheetItem(
    playlist: Playlist,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val textSizeMedium = dimensionResource(id = R.dimen.text_size_medium).value.sp
    val textSizeSmall = dimensionResource(id = R.dimen.text_size_small).value.sp
    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
    val trackVerticalPadding = dimensionResource(id = R.dimen.track_vertical_padding)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
            .padding(vertical = trackVerticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
            contentDescription = null,
            tint = if (isSelected) colorResource(id = R.color.accent_red) else colorResource(id = R.color.primary_blue),
            modifier = Modifier.size(iconSize)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = paddingMedium)
        ) {
            Text(
                text = playlist.name,
                fontSize = textSizeMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = tracksCountText(playlist.tracks.size),
                fontSize = textSizeSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatTrackTime(trackTimeMillis: Long): String {
    val minutes = trackTimeMillis / 60000
    val seconds = (trackTimeMillis % 60000) / 1000
    return String.format("%d:%02d", minutes, seconds)
}