package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.playlist_maker_android_yulyakondratiuk.R
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.PlaylistsViewModel
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.ThemeManager
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    viewModel: PlaylistsViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onTrackClick: (Long) -> Unit,
    onEditClick: (Playlist) -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.isDarkThemeEnabled(context)
    val playlist by viewModel.getPlaylist(playlistId).collectAsState(initial = null)

    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTrackDeleteDialog by remember { mutableStateOf(false) }
    var trackToDelete by remember { mutableStateOf<Track?>(null) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

    val statsTextColor = if (isDarkTheme)
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    else colorResource(id = R.color.gray_900)

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = {
                Text(
                    text = stringResource(R.string.delete_playlist_confirm, playlist?.name ?: ""),
                    fontSize = (dimensionResource(id = R.dimen.text_size_small).value).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.viewModelScope.launch {
                            viewModel.deletePlaylistById(playlistId)
                            onNavigateBack()
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.yes),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
    }

    if (showTrackDeleteDialog && trackToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showTrackDeleteDialog = false
                trackToDelete = null
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_track_from_playlist_confirm, trackToDelete!!.trackName),
                    fontSize = (dimensionResource(id = R.dimen.text_size_small).value).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        trackToDelete?.let { track ->
                            viewModel.viewModelScope.launch {
                                viewModel.deleteTrackFromPlaylist(track)
                            }
                        }
                        showTrackDeleteDialog = false
                        trackToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.yes),
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTrackDeleteDialog = false
                    trackToDelete = null
                }) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
    }

    playlist?.let { currentPlaylist ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_large)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        if (currentPlaylist.coverBitmap != null) {
                            Image(
                                bitmap = currentPlaylist.coverBitmap!!.asImageBitmap(),
                                contentDescription = stringResource(R.string.playlist_cover),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.add_icon),
                                contentDescription = stringResource(R.string.add_cover),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.large_icon_size))
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large))) }

                item {
                    Text(
                        text = currentPlaylist.name,
                        fontSize = (dimensionResource(id = R.dimen.text_size_title_medium).value).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                    )
                }

                item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium))) }

                item {
                    Text(
                        text = currentYear,
                        fontSize = (dimensionResource(id = R.dimen.text_size_medium_large).value).sp,
                        fontWeight = FontWeight.Normal,
                        color = statsTextColor,
                        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_large)),
                        lineHeight = (dimensionResource(id = R.dimen.text_size_medium_large).value).sp
                    )
                }

                item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small))) }

                item {
                    val totalMinutes = try {
                        if (currentPlaylist.tracks.isNotEmpty()) {
                            currentPlaylist.tracks.sumOf { track ->
                                when (val time = track.trackTime) {
                                    is Long -> time
                                    is Int -> time.toLong()
                                    else -> 0L
                                }
                            } / 60000
                        } else 0L
                    } catch (e: Exception) {
                        0L
                    }

                    val totalTimeText = when {
                        totalMinutes >= 60 -> {
                            val hours = totalMinutes / 60
                            val minutes = totalMinutes % 60
                            if (minutes > 0) stringResource(R.string.playlist_time_hours_minutes, hours, minutes)
                            else stringResource(R.string.playlist_time_hours, hours)
                        }
                        else -> stringResource(R.string.playlist_time_minutes, totalMinutes)
                    }

                    Text(
                        text = "$totalTimeText · ${tracksCountText(currentPlaylist.tracks.size)}",
                        fontSize = (dimensionResource(id = R.dimen.text_size_medium_large).value).sp,
                        fontWeight = FontWeight.Normal,
                        color = statsTextColor,
                        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_large)),
                        lineHeight = (dimensionResource(id = R.dimen.text_size_medium_large).value).sp
                    )

                }

                item {
                    Box(
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.padding_large))
                            .size(dimensionResource(id = R.dimen.icon_size))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showBottomSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.menu),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.small_icon_size))
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large))) }

                if (currentPlaylist.tracks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.padding_huge)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.playlist_empty),
                                fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(
                        items = currentPlaylist.tracks,
                        key = { track -> track.trackId }
                    ) { track ->
                        TrackInPlaylistItem(
                            track = track,
                            onClick = { onTrackClick(track.trackId) },
                            onLongClick = {
                                trackToDelete = track
                                showTrackDeleteDialog = true
                            }
                        )
                        Divider(
                            color = colorResource(id = if (isDarkTheme) R.color.divider_dark else R.color.divider_light),
                            thickness = dimensionResource(id = R.dimen.divider_height),
                            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_huge))) }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = dimensionResource(id = R.dimen.card_corner_radius),
                    topEnd = dimensionResource(id = R.dimen.card_corner_radius)
                ),
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    TextButton(
                        onClick = {
                            showBottomSheet = false
                            sharePlaylist(currentPlaylist, context)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.share),
                            fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(
                        onClick = {
                            showBottomSheet = false
                            onEditClick(currentPlaylist)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.edit_info),
                            fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(
                        onClick = {
                            showBottomSheet = false
                            showDeleteDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.delete_playlist),
                            fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackInPlaylistItem(
    track: Track,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.isDarkThemeEnabled(context)

    val formattedTime = remember(track.trackTime) {
        try {
            val trackTime = when (val time = track.trackTime) {
                is Long -> time
                is Int -> time.toLong()
                else -> 0L
            }
            val minutes = trackTime / 60000
            val seconds = (trackTime % 60000) / 1000
            String.format("%d:%02d", minutes, seconds)
        } catch (e: Exception) {
            "0:00"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_large),
                vertical = dimensionResource(id = R.dimen.track_vertical_padding)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.track_icon_size))
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.field_corner_radius)))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (!track.artworkUrl100.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(track.artworkUrl100)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.track_icon_desc),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_music),
                    placeholder = painterResource(id = R.drawable.ic_music)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = stringResource(R.string.track_icon_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.track_item_spacing)))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                fontSize = (dimensionResource(id = R.dimen.track_name_text_size).value).sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.track_artist_time_format, track.artistName, formattedTime),
                fontSize = (dimensionResource(id = R.dimen.track_artist_text_size).value).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = Color(0xFFAEAFB4),
            modifier = Modifier.size(
                width = dimensionResource(id = R.dimen.arrow_forward_width),
                height = dimensionResource(id = R.dimen.arrow_forward_height)
            )
        )
    }
}

private fun sharePlaylist(playlist: Playlist, context: android.content.Context) {
    val shareText = buildString {
        appendLine(playlist.name)
        appendLine()

        val totalMinutes = playlist.tracks.sumOf { it.trackTime } / 60000
        val totalTimeText = when {
            totalMinutes >= 60 -> {
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                if (minutes > 0) {
                    val hoursText = context.getString(R.string.playlist_time_hours, hours)
                    val minutesText = context.getString(R.string.playlist_time_minutes, minutes)
                    "$hoursText $minutesText"
                } else {
                    context.getString(R.string.playlist_time_hours, hours)
                }
            }
            else -> context.getString(R.string.playlist_time_minutes, totalMinutes)
        }

        appendLine(context.getString(R.string.playlist_stats, totalTimeText, playlist.tracks.size))
        appendLine()

        if (playlist.tracks.isNotEmpty()) {
            playlist.tracks.forEachIndexed { index, track ->
                val minutes = track.trackTime / 60000
                val seconds = (track.trackTime % 60000) / 1000
                val formattedTime = String.format("%d:%02d", minutes, seconds)
                appendLine(context.getString(
                    R.string.track_number_format,
                    index + 1,
                    track.trackName,
                    track.artistName,
                    formattedTime
                ))
            }
        } else {
            appendLine(context.getString(R.string.no_favorites_message))
        }

        appendLine()
        append(context.getString(R.string.created_in_app))
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val shareChooser = Intent.createChooser(
        shareIntent,
        context.getString(R.string.share_playlist)
    )

    context.startActivity(shareChooser)
}

fun tracksCountText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "$count трек"
        count % 10 in 2..4 && (count % 100 < 10 || count % 100 > 20) -> "$count трека"
        else -> "$count треков"
    }
}