package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlist_maker_android_yulyakondratiuk.R
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.PlaylistsViewModel
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.ThemeManager
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Long) -> Unit,
    viewModel: PlaylistsViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.isDarkThemeEnabled(context)
    val favoriteTracks by viewModel.favoriteList.collectAsState(initial = emptyList())

    val settingsPadding = dimensionResource(id = R.dimen.padding_large)
    val settingsSmallPadding = dimensionResource(id = R.dimen.padding_medium)
    val settingsStartPadding = dimensionResource(id = R.dimen.title_start_padding)
    val iconButtonSize = dimensionResource(id = R.dimen.icon_button_size)
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val textSpacing = dimensionResource(id = R.dimen.icon_text_spacing)
    val textSizeButton = (dimensionResource(id = R.dimen.text_size_button).value).sp

    val errorImageSize = dimensionResource(id = R.dimen.error_image_size)
    val errorTextSize = dimensionResource(id = R.dimen.error_text_size).value.sp
    val imageTextSpacing = dimensionResource(id = R.dimen.image_text_spacing)

    val emptyImageResId = if (isDarkTheme) {
        R.drawable.ic_error
    } else {
        R.drawable.ic_error_light
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                text = stringResource(R.string.favorites_title),
                fontSize = textSizeButton,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (favoriteTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(imageTextSpacing)
                ) {
                    Image(
                        painter = painterResource(id = emptyImageResId),
                        contentDescription = stringResource(R.string.no_favorites),
                        modifier = Modifier.size(errorImageSize)
                    )
                    Text(
                        text = stringResource(R.string.no_favorites_message),
                        fontSize = errorTextSize,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.container_side_padding))
            ) {
                items(favoriteTracks) { track ->
                    FavoriteTrackItem(
                        track = track,
                        onClick = { onTrackClick(track.trackId) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
                        thickness = dimensionResource(id = R.dimen.divider_height),
                        color = colorResource(id = if (isDarkTheme) R.color.divider_dark else R.color.divider_light)
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteTrackItem(
    track: Track,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.isDarkThemeEnabled(context)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
            .padding(vertical = dimensionResource(id = R.dimen.track_vertical_padding)),
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                fontSize = (dimensionResource(id = R.dimen.track_name_text_size).value).sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(
                    R.string.track_artist_time_format,
                    track.artistName,
                    formatTrackTime(track.trackTime)
                ),
                fontSize = (dimensionResource(id = R.dimen.track_artist_text_size).value).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = colorResource(id = R.color.arrow_forward),
            modifier = Modifier.size(
                width = dimensionResource(id = R.dimen.arrow_forward_width),
                height = dimensionResource(id = R.dimen.arrow_forward_height)
            )
        )
    }
}