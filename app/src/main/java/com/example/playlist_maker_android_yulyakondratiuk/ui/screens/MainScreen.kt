package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.playlist_maker_android_yulyakondratiuk.R

@Composable
fun MainScreen(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val containerHeight = dimensionResource(id = R.dimen.container_height)
    val containerCornerRadius = dimensionResource(id = R.dimen.container_corner_radius)
    val paddingLarge = dimensionResource(id = R.dimen.padding_large)
    val paddingHuge = dimensionResource(id = R.dimen.padding_huge)
    val titleStartPadding = dimensionResource(id = R.dimen.title_start_padding)
    val textSizeTitle = dimensionResource(id = R.dimen.text_size_title).value.sp
    val containerSidePadding = dimensionResource(id = R.dimen.container_side_padding)
    val buttonSpacing = dimensionResource(id = R.dimen.button_spacing)
    val iconTextSpacing = dimensionResource(id = R.dimen.icon_text_spacing)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.app_title),
            fontSize = textSizeTitle,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = titleStartPadding, top = paddingHuge)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = containerCornerRadius,
                        topEnd = containerCornerRadius
                    )
                )
                .background(colorScheme.surface)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = containerSidePadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(paddingLarge))

                NavigationItem(
                    icon = Icons.Default.Search,
                    text = stringResource(R.string.search_title),
                    onClick = onSearchClick,
                    iconTextSpacing = iconTextSpacing
                )

                Spacer(modifier = Modifier.height(buttonSpacing))

                NavigationItem(
                    icon = Icons.Default.LibraryMusic,
                    text = stringResource(R.string.playlists_title),
                    onClick = onPlaylistsClick,
                    iconTextSpacing = iconTextSpacing
                )

                Spacer(modifier = Modifier.height(buttonSpacing))

                NavigationItem(
                    icon = Icons.Default.FavoriteBorder,
                    text = stringResource(R.string.favorites_title),
                    onClick = onFavoritesClick,
                    iconTextSpacing = iconTextSpacing
                )

                Spacer(modifier = Modifier.height(buttonSpacing))

                NavigationItem(
                    icon = Icons.Default.Settings,
                    text = stringResource(R.string.settings_title),
                    onClick = onSettingsClick,
                    iconTextSpacing = iconTextSpacing
                )
            }
        }
    }
}

@Composable
fun NavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    iconTextSpacing: androidx.compose.ui.unit.Dp
) {
    val colorScheme = MaterialTheme.colorScheme

    val buttonHeight = dimensionResource(id = R.dimen.button_height)
    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
    val paddingSmall = dimensionResource(id = R.dimen.padding_small)
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val textSizeButton = dimensionResource(id = R.dimen.text_size_button).value.sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingMedium,
                    end = paddingSmall,
                    bottom = paddingMedium,
                    start = paddingMedium
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(iconSize),
                    tint = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(iconTextSpacing))

                Text(
                    text = text,
                    fontSize = textSizeButton,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
            }

            ArrowIcon()
        }
    }
}

@Composable
fun ArrowIcon() {
    val colorScheme = MaterialTheme.colorScheme

    val smallIconSize = dimensionResource(id = R.dimen.small_icon_size)

    Icon(
        imageVector = Icons.Outlined.ArrowForwardIos,
        contentDescription = stringResource(R.string.back),
        modifier = Modifier.size(smallIconSize),
        tint = colorScheme.onSurfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(
                onSearchClick = {},
                onSettingsClick = {},
                onPlaylistsClick = {},
                onFavoritesClick = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenDarkPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(
                onSearchClick = {},
                onSettingsClick = {},
                onPlaylistsClick = {},
                onFavoritesClick = {}
            )
        }
    }
}