package com.example.playlist_maker_android_yulyakondratiuk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.playlist_maker_android_yulyakondratiuk.R

@Composable
private fun LightColorScheme() = lightColorScheme(
    primary = colorResource(id = R.color.primary_blue),
    onPrimary = colorResource(id = R.color.on_primary),
    primaryContainer = colorResource(id = R.color.primary_container),
    onPrimaryContainer = colorResource(id = R.color.on_primary_container),
    secondary = colorResource(id = R.color.secondary_blue),
    onSecondary = colorResource(id = R.color.on_secondary_light),
    background = colorResource(id = R.color.background_light),
    onBackground = colorResource(id = R.color.on_background_light),
    surface = colorResource(id = R.color.surface_light),
    onSurface = colorResource(id = R.color.on_surface_light),
    surfaceVariant = colorResource(id = R.color.surface_variant_light),
    onSurfaceVariant = colorResource(id = R.color.on_surface_variant_light),
    error = colorResource(id = R.color.error_light),
    onError = colorResource(id = R.color.on_error_light),
)

@Composable
private fun DarkColorScheme() = darkColorScheme(
    primary = colorResource(id = R.color.primary_blue),
    onPrimary = colorResource(id = R.color.on_primary),
    primaryContainer = colorResource(id = R.color.primary_container_dark),
    onPrimaryContainer = colorResource(id = R.color.on_primary_container),
    secondary = colorResource(id = R.color.secondary_light_blue),
    onSecondary = colorResource(id = R.color.on_secondary_dark),
    background = colorResource(id = R.color.background_dark),
    onBackground = colorResource(id = R.color.on_background_dark),
    surface = colorResource(id = R.color.surface_dark),
    onSurface = colorResource(id = R.color.on_surface_dark),
    surfaceVariant = colorResource(id = R.color.surface_variant_dark),
    onSurfaceVariant = colorResource(id = R.color.on_surface_variant_dark),
    error = colorResource(id = R.color.error_dark),
    onError = colorResource(id = R.color.on_error_dark),
)

@Composable
fun PlaylistMarketTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val currentTheme = ThemeManager.getCurrentTheme(context)
    val darkTheme = currentTheme == ThemeManager.THEME_DARK

    val colors = if (darkTheme) {
        DarkColorScheme()
    } else {
        LightColorScheme()
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}