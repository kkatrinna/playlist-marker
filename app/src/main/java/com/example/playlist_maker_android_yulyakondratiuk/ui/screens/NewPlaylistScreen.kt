package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlist_maker_android_yulyakondratiuk.R
import com.example.playlist_maker_android_yulyakondratiuk.domain.Playlist
import com.example.playlist_maker_android_yulyakondratiuk.ui.view_model.PlaylistsViewModel
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.ThemeManager

@Composable
fun NewPlaylistScreen(
    onNavigateBack: () -> Unit,
    viewModel: PlaylistsViewModel = viewModel(),
    playlistToEdit: Playlist? = null
) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.isDarkThemeEnabled(context)

    var playlistName by remember { mutableStateOf(playlistToEdit?.name ?: "") }
    var playlistDescription by remember { mutableStateOf(playlistToEdit?.description ?: "") }
    var selectedImageBitmap by remember { mutableStateOf(playlistToEdit?.coverBitmap) }

    var isNameFocused by remember { mutableStateOf(false) }
    var isDescriptionFocused by remember { mutableStateOf(false) }

    val settingsPadding = dimensionResource(id = R.dimen.padding_large)
    val settingsSmallPadding = dimensionResource(id = R.dimen.padding_medium)
    val settingsStartPadding = dimensionResource(id = R.dimen.title_start_padding)
    val iconButtonSize = dimensionResource(id = R.dimen.icon_button_size)
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val textSpacing = dimensionResource(id = R.dimen.icon_text_spacing)
    val textSizeButton = (dimensionResource(id = R.dimen.text_size_button).value).sp

    val defaultBorderColor = colorResource(id = R.color.gray_400)
    val focusedBorderColor = colorResource(id = R.color.primary_blue)
    val defaultButtonColor = colorResource(id = R.color.gray_400)
    val activeButtonColor = colorResource(id = R.color.primary_blue)
    val buttonTextColor = colorResource(id = R.color.white)

    val placeholderColor = if (isDarkTheme)
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    else colorResource(id = R.color.gray_900)

    val labelColor = focusedBorderColor

    val buttonColor = if (playlistName.isNotBlank()) activeButtonColor else defaultButtonColor

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                selectedImageBitmap = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
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
                .verticalScroll(rememberScrollState())
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
                    onClick = onNavigateBack,
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
                    text = if (playlistToEdit != null)
                        stringResource(R.string.edit_info)
                    else
                        stringResource(R.string.new_playlist),
                    fontSize = textSizeButton,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.container_side_padding)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.playlist_image_top_spacing)))

                Box(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.playlist_image_size))
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.button_corner_radius)))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageBitmap != null) {
                        Image(
                            bitmap = selectedImageBitmap!!.asImageBitmap(),
                            contentDescription = stringResource(R.string.playlist_cover),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.button_corner_radius)))
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.add_icon),
                            contentDescription = stringResource(R.string.add_cover),
                            tint = defaultBorderColor,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.playlist_image_size))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.playlist_form_top_spacing)))

                CustomTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = stringResource(R.string.playlist_name_label),
                    isRequired = true,
                    placeholderColor = placeholderColor,
                    labelColor = labelColor,
                    defaultBorderColor = defaultBorderColor,
                    focusedBorderColor = focusedBorderColor,
                    isFocused = isNameFocused,
                    onFocusChange = { isNameFocused = it },
                    modifier = Modifier.width(dimensionResource(id = R.dimen.playlist_text_field_width)),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    textColor = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

                CustomTextField(
                    value = playlistDescription,
                    onValueChange = { playlistDescription = it },
                    label = stringResource(R.string.playlist_description_label),
                    isRequired = false,
                    placeholderColor = placeholderColor,
                    labelColor = labelColor,
                    defaultBorderColor = defaultBorderColor,
                    focusedBorderColor = focusedBorderColor,
                    isFocused = isDescriptionFocused,
                    onFocusChange = { isDescriptionFocused = it },
                    modifier = Modifier.width(dimensionResource(id = R.dimen.playlist_text_field_width)),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    textColor = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.playlist_button_top_spacing)))
            }
        }

        Button(
            onClick = {
                if (playlistName.isNotBlank()) {
                    if (playlistToEdit != null) {
                        viewModel.updatePlaylist(
                            playlistId = playlistToEdit.id,
                            name = playlistName,
                            description = playlistDescription,
                            coverBitmap = selectedImageBitmap
                        )
                    } else {
                        viewModel.createNewPlayList(playlistName, playlistDescription, selectedImageBitmap)
                    }
                    onNavigateBack()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(id = R.dimen.padding_huge))
                .width(dimensionResource(id = R.dimen.playlist_text_field_width))
                .height(dimensionResource(id = R.dimen.playlist_button_height)),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.button_corner_radius)),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = buttonTextColor,
                disabledContainerColor = buttonColor.copy(alpha = 0.5f),
                disabledContentColor = buttonTextColor.copy(alpha = 0.5f)
            ),
            enabled = playlistName.isNotBlank()
        ) {
            Text(
                text = if (playlistToEdit != null)
                    stringResource(R.string.save)
                else
                    stringResource(R.string.create),
                fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isRequired: Boolean,
    placeholderColor: Color,
    labelColor: Color,
    defaultBorderColor: Color,
    focusedBorderColor: Color,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color
) {
    val borderColor = if (isFocused || value.isNotEmpty()) focusedBorderColor else defaultBorderColor
    val labelText = if (isRequired) "$label*" else label
    val showLabel = isFocused || value.isNotEmpty()
    val cornerRadius = dimensionResource(id = R.dimen.button_corner_radius)

    Box(
        modifier = modifier.height(dimensionResource(id = R.dimen.playlist_text_field_height))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .border(
                    width = dimensionResource(id = R.dimen.divider_height),
                    color = borderColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxSize()
                    .onFocusChanged { focusState ->
                        onFocusChange(focusState.isFocused)
                    },
                textStyle = TextStyle(
                    fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp,
                    color = textColor
                ),
                cursorBrush = SolidColor(focusedBorderColor),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty() && !isFocused) {
                            Text(
                                text = labelText,
                                color = placeholderColor,
                                fontSize = (dimensionResource(id = R.dimen.text_size_medium).value).sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        if (showLabel) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x = dimensionResource(id = R.dimen.padding_medium),
                        y = dimensionResource(id = R.dimen.padding_small).times(-2)
                    )
                    .background(backgroundColor)
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            ) {
                Text(
                    text = labelText,
                    color = labelColor,
                    fontSize = (dimensionResource(id = R.dimen.text_size_caption).value).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}