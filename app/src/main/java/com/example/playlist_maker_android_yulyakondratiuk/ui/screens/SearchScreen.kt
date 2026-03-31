package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.playlist_maker_android_yulyakondratiuk.R
import com.example.playlist_maker_android_yulyakondratiuk.data.network.Track
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.SearchState
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.getViewModelFactory())
) {
    val context = LocalContext.current
    val screenState by viewModel.searchScreenState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val colorScheme = MaterialTheme.colorScheme

    val isDarkTheme = colorScheme.background.luminance < 0.5f

    val errorImageResId = if (isDarkTheme) {
        R.drawable.ic_error
    } else {
        R.drawable.ic_error_light
    }

    val networkImageResId = if (isDarkTheme) {
        R.drawable.ic_network
    } else {
        R.drawable.ic_network_light
    }

    val paddingLarge = dimensionResource(id = R.dimen.padding_large)
    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
    val titleStartPadding = dimensionResource(id = R.dimen.title_start_padding)
    val iconButtonSize = dimensionResource(id = R.dimen.icon_button_size)
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val textSpacing = dimensionResource(id = R.dimen.icon_text_spacing)
    val verticalSpacing = dimensionResource(id = R.dimen.button_spacing)
    val textSizeButton = dimensionResource(id = R.dimen.text_size_button).value.sp

    val searchFieldHeight = dimensionResource(id = R.dimen.search_field_height)
    val searchFieldInnerPadding = dimensionResource(id = R.dimen.search_field_inner_padding)
    val searchIconSize = dimensionResource(id = R.dimen.search_icon_size)
    val searchIconSpacing = dimensionResource(id = R.dimen.search_icon_spacing)
    val searchTextSize = dimensionResource(id = R.dimen.search_text_size).value.sp
    val clearButtonSize = dimensionResource(id = R.dimen.clear_button_size)
    val clearIconSize = dimensionResource(id = R.dimen.clear_icon_size)
    val fieldCornerRadius = dimensionResource(id = R.dimen.field_corner_radius)
    val fieldShadowElevation = dimensionResource(id = R.dimen.field_shadow_elevation)

    val errorImageSize = dimensionResource(id = R.dimen.error_image_size)
    val errorTextSize = dimensionResource(id = R.dimen.error_text_size).value.sp
    val imageTextSpacing = dimensionResource(id = R.dimen.image_text_spacing)

    val trackIconSize = dimensionResource(id = R.dimen.track_icon_size)
    val trackNameTextSize = dimensionResource(id = R.dimen.track_name_text_size).value.sp
    val trackArtistTextSize = dimensionResource(id = R.dimen.track_artist_text_size).value.sp
    val trackTimeTextSize = dimensionResource(id = R.dimen.track_time_text_size).value.sp
    val trackItemSpacing = dimensionResource(id = R.dimen.track_item_spacing)
    val trackTimeStartPadding = dimensionResource(id = R.dimen.track_time_start_padding)
    val trackVerticalPadding = dimensionResource(id = R.dimen.track_vertical_padding)

    LaunchedEffect(searchText) {
        debounceJob?.cancel()

        if (searchText.isNotBlank()) {
            debounceJob = coroutineScope.launch {
                viewModel.search(searchText)
            }
        } else if (searchText.isEmpty()) {
            viewModel.clearSearch()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = titleStartPadding,
                    top = paddingLarge,
                    end = paddingLarge,
                    bottom = paddingMedium
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(iconButtonSize)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(iconSize)
                )
            }

            Spacer(modifier = Modifier.width(textSpacing))

            Text(
                text = stringResource(R.string.search_title),
                fontSize = textSizeButton,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingMedium)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(searchFieldHeight),
                shape = RoundedCornerShape(fieldCornerRadius),
                color = colorScheme.surfaceVariant,
                shadowElevation = fieldShadowElevation
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = searchFieldInnerPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_icon_desc),
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(searchIconSize)
                    )

                    Spacer(modifier = Modifier.width(searchIconSpacing))

                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = colorScheme.onSurfaceVariant,
                                fontSize = searchTextSize
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            focusedPlaceholderColor = colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = colorScheme.onSurfaceVariant
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                if (searchText.isNotBlank()) {
                                    viewModel.search(searchText)
                                }
                            }
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = searchTextSize,
                            color = colorScheme.onSurface
                        )
                    )

                    if (searchText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                searchText = ""
                                viewModel.clearSearch()
                            },
                            modifier = Modifier.size(clearButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search),
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(clearIconSize)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(verticalSpacing))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(horizontal = paddingLarge)
        ) {
            when (screenState) {
                SearchState.Initial -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

                SearchState.Searching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorScheme.primary
                        )
                    }
                }

                is SearchState.Success -> {
                    val tracks = (screenState as SearchState.Success).tracks
                    if (tracks.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = screenHeight * 0.15f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(imageTextSpacing)
                        ) {
                            Image(
                                painter = painterResource(id = errorImageResId),
                                contentDescription = stringResource(R.string.search_empty_results),
                                modifier = Modifier.size(errorImageSize),
                                contentScale = ContentScale.Fit
                            )

                            Text(
                                text = stringResource(R.string.search_empty_results),
                                color = colorScheme.onSurface,
                                fontSize = errorTextSize,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                        ) {
                            items(tracks) { track ->
                                TrackListItem(
                                    track = track,
                                    isDarkTheme = isDarkTheme,
                                    trackIconSize = trackIconSize,
                                    trackNameTextSize = trackNameTextSize,
                                    trackArtistTextSize = trackArtistTextSize,
                                    trackTimeTextSize = trackTimeTextSize,
                                    trackItemSpacing = trackItemSpacing,
                                    trackTimeStartPadding = trackTimeStartPadding,
                                    trackVerticalPadding = trackVerticalPadding,
                                    onTrackClick = onTrackClick
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)),
                                    thickness = dimensionResource(id = R.dimen.divider_height),
                                    color = colorResource(id = if (isDarkTheme) R.color.divider_dark else R.color.divider_light)
                                )
                            }
                        }
                    }
                }

                is SearchState.Fail -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = screenHeight * 0.15f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(imageTextSpacing)
                    ) {
                        Image(
                            painter = painterResource(id = errorImageResId),
                            contentDescription = stringResource(R.string.search_error),
                            modifier = Modifier.size(errorImageSize),
                            contentScale = ContentScale.Fit
                        )

                        Text(
                            text = (screenState as SearchState.Fail).error,
                            color = colorScheme.onSurface,
                            fontSize = errorTextSize,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                SearchState.ConnectionError -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = screenHeight * 0.15f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(imageTextSpacing)
                    ) {
                        Image(
                            painter = painterResource(id = networkImageResId),
                            contentDescription = stringResource(R.string.search_connection_error),
                            modifier = Modifier.size(errorImageSize),
                            contentScale = ContentScale.Fit
                        )

                        Text(
                            text = stringResource(R.string.search_connection_error),
                            color = colorScheme.onSurface,
                            fontSize = errorTextSize,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = stringResource(R.string.search_connection_error_details),
                            color = colorScheme.onSurface,
                            fontSize = errorTextSize,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun TrackListItem(
    track: Track,
    isDarkTheme: Boolean,
    trackIconSize: androidx.compose.ui.unit.Dp,
    trackNameTextSize: androidx.compose.ui.unit.TextUnit,
    trackArtistTextSize: androidx.compose.ui.unit.TextUnit,
    trackTimeTextSize: androidx.compose.ui.unit.TextUnit,
    trackItemSpacing: androidx.compose.ui.unit.Dp,
    trackTimeStartPadding: androidx.compose.ui.unit.Dp,
    trackVerticalPadding: androidx.compose.ui.unit.Dp,
    onTrackClick: (Track) -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick(track) }
            .padding(vertical = trackVerticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(trackIconSize)
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.field_corner_radius)))
                .background(colorScheme.surfaceVariant)
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
                    tint = if (isDarkTheme) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }

        Spacer(modifier = Modifier.width(trackItemSpacing))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                fontSize = trackNameTextSize,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artistName} · ${formatTrackTime(track.trackTime)}",
                fontSize = trackArtistTextSize,
                color = colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = Color(0xFFAEAFB4),
            modifier = Modifier.size(width = 8.dp, height = 14.dp)
        )
    }
}

val Color.luminance: Float
    get() {
        val red = red
        val green = green
        val blue = blue
        return 0.2126f * red + 0.7152f * green + 0.0722f * blue
    }