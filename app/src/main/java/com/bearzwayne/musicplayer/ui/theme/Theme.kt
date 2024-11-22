package com.bearzwayne.musicplayer.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bearzwayne.musicplayer.data.utils.SettingsKeys
import com.bearzwayne.musicplayer.data.utils.SettingsKeys.dataStore
import com.bearzwayne.musicplayer.ui.theme.ColorPallet.BLUE
import com.bearzwayne.musicplayer.ui.theme.ColorPallet.GREEN
import com.bearzwayne.musicplayer.ui.theme.ColorPallet.ORANGE
import com.bearzwayne.musicplayer.ui.theme.ColorPallet.PURPLE
import com.bearzwayne.musicplayer.ui.theme.util.isSystemDark
import kotlinx.coroutines.flow.map

// dark palettes
private val DarkGreenColorPalette = darkColorScheme(
    primary = green200,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = seriousBlack,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

private val DarkPurpleColorPalette = darkColorScheme(
    primary = purple200,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = seriousBlack,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

private val DarkBlueColorPalette = darkColorScheme(
    primary = blue200,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = seriousBlack,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

private val DarkOrangeColorPalette = darkColorScheme(
    primary = darkerBestOrange,
    primaryContainer = darkerBestOrange,
    secondaryContainer = darkerBestOrange,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = seriousBlack,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

// Light pallets
private val LightGreenColorPalette = lightColorScheme(
    primary = green500,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = seriousWhite,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightPurpleColorPalette = lightColorScheme(
    primary = purple,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = seriousWhite,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightBlueColorPalette = lightColorScheme(
    primary = blue500,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = seriousWhite,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightOrangeColorPalette = lightColorScheme(
    primary = darkerBestOrange,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = seriousWhite,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

enum class ColorPallet {
    PURPLE, GREEN, ORANGE, BLUE
}

@Composable
fun MusicPlayerTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = isSystemDark(context)
    val palette = context.dataStore.data
        .map { preferences ->
            preferences[SettingsKeys.SELECTED_COLOR_PALETTE] ?: "ORANGE"
        }.collectAsState(initial = "ORANGE").value
    val colorScheme = when (palette) {
        GREEN.name -> if (isDarkTheme) DarkGreenColorPalette else LightGreenColorPalette
        PURPLE.name -> if (isDarkTheme) DarkPurpleColorPalette else LightPurpleColorPalette
        ORANGE.name -> if (isDarkTheme) DarkOrangeColorPalette else LightOrangeColorPalette
        BLUE.name -> if (isDarkTheme) DarkBlueColorPalette else LightBlueColorPalette
        else -> if (isDarkTheme) DarkOrangeColorPalette else LightOrangeColorPalette
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

