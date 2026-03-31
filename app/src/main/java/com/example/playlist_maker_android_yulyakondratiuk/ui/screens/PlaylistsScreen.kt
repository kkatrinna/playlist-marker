package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlist_maker_android_yulyakondratiuk.R
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.PlaylistsViewModel
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.ThemeManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onBackClick: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    viewModel: PlaylistsViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.isDarkThemeEnabled(context)
    val playlists by viewModel.playlists.collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMergeBottomSheet by remember { mutableStateOf(false) }
    var showPlaylistOptionsBottomSheet by remember { mutableStateOf(false) }
    var selectedPlaylistForMerge by remember { mutableStateOf<Playlist?>(null) }
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    val settingsPadding = dimensionResource(id = R.dimen.padding_large)
    val settingsSmallPadding = dimensionResource(id = R.dimen.padding_medium)
    val settingsStartPadding = dimensionResource(id = R.dimen.title_start_padding)
    val iconButtonSize = dimensionResource(id = R.dimen.icon_button_size)
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val textSpacing = dimensionResource(id = R.dimen.icon_text_spacing)
    val textSizeButton = (dimensionResource(id = R.dimen.text_size_button).value).sp

    if (showDeleteDialog && selectedPlaylist != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedPlaylist = null
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_playlist_confirm, selectedPlaylist!!.name),
                    fontSize = (dimensionResource(id = R.dimen.text_size_small).value).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPlaylist?.let { playlist ->
                            viewModel.viewModelScope.launch {
                                viewModel.deletePlaylistById(playlist.id)
                            }
                        }
                        showDeleteDialog = false
                        selectedPlaylist = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.yes),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedPlaylist = null
                    }
                ) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
    }

    if (showPlaylistOptionsBottomSheet && selectedPlaylist != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showPlaylistOptionsBottomSheet = false
                selectedPlaylist = null
            },
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
                Text(
                    text = stringResource(R.string.playlist_options_title),
                    fontSize = (dimensionResource(id = R.dimen.text_size_title).value).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(id = R.dimen.padding_large)),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                TextButton(
                    onClick = {
                        showPlaylistOptionsBottomSheet = false
                        selectedPlaylistForMerge = selectedPlaylist
                        showMergeBottomSheet = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.merge_playlist),
                        fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                TextButton(
                    onClick = {
                        showPlaylistOptionsBottomSheet = false
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

    if (showMergeBottomSheet && selectedPlaylistForMerge != null) {
        val otherPlaylists = playlists.filter { it.id != selectedPlaylistForMerge!!.id }

        ModalBottomSheet(
            onDismissRequest = {
                showMergeBottomSheet = false
                selectedPlaylistForMerge = null
            },
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
                Text(
                    text = stringResource(R.string.merge_playlist_title),
                    fontSize = (dimensionResource(id = R.dimen.text_size_title).value).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(id = R.dimen.padding_large)),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                if (otherPlaylists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.playlist_empty_height)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_other_playlists),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn {
                        items(otherPlaylists) { playlist ->
                            PlaylistMergeItem(
                                playlist = playlist,
                                onClick = {
                                    val sourcePlaylist = selectedPlaylistForMerge
                                    val targetPlaylist = playlist

                                    showMergeBottomSheet = false

                                    if (sourcePlaylist != null) {
                                        viewModel.viewModelScope.launch {
                                            try {
                                                val success = viewModel.mergePlaylists(
                                                    sourcePlaylistId = sourcePlaylist.id,
                                                    targetPlaylistId = targetPlaylist.id
                                                )
                                                if (success) {
                                                    val message = context.resources.getString(
                                                        R.string.playlist_merged_message,
                                                        sourcePlaylist.name,
                                                        targetPlaylist.name
                                                    )
                                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        context.resources.getString(R.string.merge_error),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Toast.makeText(
                                                    context,
                                                    context.resources.getString(R.string.merge_error),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } finally {
                                                selectedPlaylistForMerge = null
                                                viewModel.refreshPlaylists()
                                            }
                                        }
                                    }
                                }
                            )
                            HorizontalDivider(
                                thickness = dimensionResource(id = R.dimen.divider_height),
                                color = colorResource(id = if (isDarkTheme) R.color.divider_dark else R.color.divider_light)
                            )
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = settingsStartPadding,
                        top = settingsPadding,
                        end = settingsPadding,
                        bottom = settingsSmallPadding
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(iconButtonSize)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(iconSize)
                    )
                }

                Spacer(modifier = Modifier.width(textSpacing))

                Text(
                    text = stringResource(R.string.playlists_title),
                    fontSize = textSizeButton,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_icon),
                        contentDescription = stringResource(R.string.no_playlists),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.large_icon_size))
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensionResource(id = R.dimen.container_side_padding))
                ) {
                    items(playlists) { playlist ->
                        PlaylistItem(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist.id) },
                            onLongClick = {
                                selectedPlaylist = playlist
                                showPlaylistOptionsBottomSheet = true
                            }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = dimensionResource(id = R.dimen.padding_extra_large),
                    end = dimensionResource(id = R.dimen.container_side_padding)
                )
                .size(dimensionResource(id = R.dimen.fab_size))
                .clip(CircleShape)
                .background(
                    if (isDarkTheme)
                        Color.White.copy(alpha = 0.1f)
                    else
                        Color(0x401A1B22)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onCreatePlaylistClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_playlist_button),
                tint = Color.White,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = dimensionResource(id = R.dimen.track_vertical_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.track_icon_size))
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.field_corner_radius)))
        ) {
            if (playlist.coverBitmap != null) {
                Image(
                    bitmap = playlist.coverBitmap!!.asImageBitmap(),
                    contentDescription = stringResource(R.string.playlist_cover_desc, playlist.name),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.field_corner_radius)))
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = stringResource(R.string.playlist_cover_missing),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.track_item_spacing)))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                fontSize = (dimensionResource(id = R.dimen.track_name_text_size).value).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = tracksCountText(playlist.tracks.size),
                fontSize = (dimensionResource(id = R.dimen.track_artist_text_size).value).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlaylistMergeItem(
    playlist: Playlist,
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
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.track_icon_size))
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.field_corner_radius)))
        ) {
            if (playlist.coverBitmap != null) {
                Image(
                    bitmap = playlist.coverBitmap!!.asImageBitmap(),
                    contentDescription = stringResource(R.string.playlist_cover_desc, playlist.name),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.field_corner_radius)))
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = stringResource(R.string.playlist_cover_missing),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.track_item_spacing)))

        Column(
            modifier = Modifier.weight(1f)
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