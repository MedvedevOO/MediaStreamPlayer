package com.bearzwayne.musicplayer.ui.theme.util

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.SettingsKeys
import com.bearzwayne.musicplayer.data.utils.SettingsKeys.dataStore
import kotlinx.coroutines.flow.map

@Composable
fun isSystemDark(context: Context): Boolean {
    val settingsKey = context.dataStore.data
        .map { preferences ->
            preferences[SettingsKeys.THEME_KEY] ?: "Match system"
        }.collectAsState(initial = "Match system").value
    val  isDarkTheme = when(settingsKey) {

        stringResource(R.string.match_system) -> isSystemInDarkTheme()
        stringResource(R.string.dark) -> true
        stringResource(R.string.light) -> false
        else -> isSystemInDarkTheme()
    }
    return isDarkTheme
}