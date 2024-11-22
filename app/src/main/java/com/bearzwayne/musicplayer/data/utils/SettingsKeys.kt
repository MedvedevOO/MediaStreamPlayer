package com.bearzwayne.musicplayer.data.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bearzwayne.musicplayer.ui.theme.ColorPallet

object SettingsKeys {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val THEME_KEY = stringPreferencesKey("theme_key")
    val SELECTED_COLOR_PALETTE = stringPreferencesKey("selected_color_palette")

    suspend fun saveSelectedColorPalette(context: Context,colorPalette: ColorPallet) {
            context.dataStore.edit { preferences ->
                preferences[SELECTED_COLOR_PALETTE] = colorPalette.name
            }
    }

    suspend fun saveThemeSetting(context: Context, theme: String) {
        context.dataStore.edit { settings ->
            settings[THEME_KEY] = theme
        }
    }
}