package com.example.musicplayer.data

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.musicplayer.ui.theme.ColorPallet
import kotlinx.coroutines.flow.map

object SettingsKeys {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val THEME_KEY = stringPreferencesKey("theme_key")
    val SELECTED_COLOR_PALETTE = stringPreferencesKey("selected_color_palette")
    val SELECTED_RADIO_COUNTRY = stringPreferencesKey("selected_radio_country")
    val SELECTED_RADIO_TAG = stringPreferencesKey("selected_radio_tag")
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

    suspend fun saveRadioCountry(context: Context, country: String) {
        context.dataStore.edit { settings ->
            settings[SELECTED_RADIO_COUNTRY] = country
        }
    }

    suspend fun saveRadioTag(context: Context, tag: String) {
        context.dataStore.edit { settings ->
            settings[SELECTED_RADIO_TAG] = tag
        }
    }

    @Composable
    fun isSystemDark(context: Context): Boolean {
        val settingsKey = context.dataStore.data
            .map { preferences ->
                preferences[THEME_KEY] ?: "Match system"
            }.collectAsState(initial = "Match system").value
        val  isDarkTheme = when(settingsKey) {

            "Match system" -> isSystemInDarkTheme()
            "Dark" -> true
            "Light" -> false
            else -> isSystemInDarkTheme()
        }
        return isDarkTheme
    }


}