package com.example.playlist_maker_android_yulyakondratiuk.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREF_NAME = "app_settings"
    private const val THEME_KEY = "theme_mode"

    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getCurrentTheme(context: Context): String {
        return getPreferences(context).getString(THEME_KEY, THEME_LIGHT) ?: THEME_LIGHT
    }

    fun setTheme(context: Context, theme: String) {
        getPreferences(context).edit().putString(THEME_KEY, theme).apply()
        applyTheme(theme)
    }

    fun applyTheme(theme: String) {
        when (theme) {
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun isDarkThemeEnabled(context: Context): Boolean {
        return getCurrentTheme(context) == THEME_DARK
    }
}