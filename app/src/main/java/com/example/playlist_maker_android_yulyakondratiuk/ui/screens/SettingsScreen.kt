package com.example.playlist_maker_android_yulyakondratiuk.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.playlist_maker_android_yulyakondratiuk.R
import com.example.playlist_maker_android_yulyakondratiuk.ui.theme.ThemeManager

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var darkThemeEnabled by remember {
        mutableStateOf(ThemeManager.isDarkThemeEnabled(context))
    }

    val settingsPadding = dimensionResource(id = R.dimen.padding_large)
    val settingsSmallPadding = dimensionResource(id = R.dimen.padding_medium)
    val settingsStartPadding = dimensionResource(id = R.dimen.title_start_padding)
    val iconButtonSize = dimensionResource(id = R.dimen.icon_button_size)
    val iconSize = dimensionResource(id = R.dimen.icon_size)
    val settingsIconSize = dimensionResource(id = R.dimen.icon_size)
    val textSpacing = dimensionResource(id = R.dimen.icon_text_spacing)
    val verticalSpacing = dimensionResource(id = R.dimen.button_spacing)

    val textSizeTitle = getTextSizeFromResource(R.dimen.text_size_medium)
    val textSizeButton = getTextSizeFromResource(R.dimen.text_size_button)

    val switchThumbColor = Color(0xFF3772E7)
    val switchTrackColorLight = switchThumbColor.copy(alpha = 0.48f)
    val switchTrackColorDark = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier
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
                text = stringResource(R.string.settings_title),
                fontSize = textSizeButton,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(verticalSpacing))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = settingsPadding)
        ) {
            SettingsOption(
                onClick = {
                    darkThemeEnabled = !darkThemeEnabled

                    val theme = if (darkThemeEnabled) {
                        ThemeManager.THEME_DARK
                    } else {
                        ThemeManager.THEME_LIGHT
                    }
                    ThemeManager.setTheme(context, theme)

                    (context as? Activity)?.recreate()
                },
                text = stringResource(R.string.dark_theme),
                iconResId = null,
                showSwitch = true,
                switchChecked = darkThemeEnabled,
                iconSize = settingsIconSize,
                textSize = textSizeTitle,
                switchThumbColor = switchThumbColor,
                switchTrackColorChecked = switchTrackColorLight,
                switchTrackColorUnchecked = switchTrackColorDark
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            SettingsOption(
                onClick = { shareApp(context) },
                text = stringResource(R.string.share_app),
                iconResId = R.drawable.ic_share,
                showSwitch = false,
                iconSize = settingsIconSize,
                textSize = textSizeTitle
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            SettingsOption(
                onClick = { writeToSupport(context) },
                text = stringResource(R.string.write_to_developers),
                iconResId = R.drawable.support,
                showSwitch = false,
                iconSize = settingsIconSize,
                textSize = textSizeTitle
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            SettingsOption(
                onClick = { openUserAgreement(context) },
                text = stringResource(R.string.user_agreement),
                iconResId = R.drawable.ic_arrow,
                showSwitch = false,
                iconSize = settingsIconSize,
                textSize = textSizeTitle
            )
        }
    }
}

@Composable
private fun SettingsOption(
    onClick: () -> Unit,
    text: String,
    iconResId: Int?,
    showSwitch: Boolean,
    switchChecked: Boolean = false,
    iconSize: androidx.compose.ui.unit.Dp,
    textSize: TextUnit,
    switchThumbColor: Color = colorResource(id = R.color.primary_blue),
    switchTrackColorChecked: Color = colorResource(id = R.color.primary_blue).copy(alpha = 0.48f),
    switchTrackColorUnchecked: Color = MaterialTheme.colorScheme.surfaceVariant,
    switchUncheckedThumbColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = textSize,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (showSwitch) {
                Switch(
                    checked = switchChecked,
                    onCheckedChange = { onClick() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = switchThumbColor,
                        checkedTrackColor = switchTrackColorChecked,
                        checkedBorderColor = Color.Transparent,

                        uncheckedThumbColor = switchUncheckedThumbColor,
                        uncheckedTrackColor = switchTrackColorUnchecked,
                        uncheckedBorderColor = Color.Transparent,

                        disabledCheckedThumbColor = switchThumbColor.copy(alpha = 0.38f),
                        disabledCheckedTrackColor = switchTrackColorChecked.copy(alpha = 0.38f),
                        disabledUncheckedThumbColor = switchUncheckedThumbColor.copy(alpha = 0.38f),
                        disabledUncheckedTrackColor = switchTrackColorUnchecked.copy(alpha = 0.38f)
                    )
                )
            } else if (iconResId != null) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
private fun getTextSizeFromResource(dimenResId: Int): TextUnit {
    val dimension = dimensionResource(id = dimenResId)
    return dimension.value.sp
}

private fun shareApp(context: Context) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.share_app_chooser_title)
        )
    }

    val shareChooser = Intent.createChooser(
        shareIntent,
        context.getString(R.string.share_app)
    )

    context.startActivity(shareChooser)
}

private fun writeToSupport(context: Context) {
    val developerEmail = context.getString(R.string.developer_email)
    val emailSubject = context.getString(R.string.email_subject)
    val emailBody = context.getString(R.string.email_body)

    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$developerEmail")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(developerEmail))
        putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        putExtra(Intent.EXTRA_TEXT, emailBody)
    }

    val emailChooser = Intent.createChooser(
        emailIntent,
        context.getString(R.string.write_to_developers)
    )

    context.startActivity(emailChooser)
}
private fun openUserAgreement(context: Context) {
    val agreementUrl = context.getString(R.string.agreement_url)
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))

    val browserChooser = Intent.createChooser(
        browserIntent,
        context.getString(R.string.user_agreement)
    )

    context.startActivity(browserChooser)
}