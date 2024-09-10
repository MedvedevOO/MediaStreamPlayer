package com.example.musicplayer.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.musicplayer.data.SettingsKeys
import com.example.musicplayer.data.SettingsKeys.dataStore
import com.example.musicplayer.ui.theme.ColorPallet.BLUE
import com.example.musicplayer.ui.theme.ColorPallet.GREEN
import com.example.musicplayer.ui.theme.ColorPallet.ORANGE
import com.example.musicplayer.ui.theme.ColorPallet.PURPLE
import kotlinx.coroutines.flow.map

// dark palettes
private val DarkGreenColorPalette = darkColorScheme(
    primary = green200,
//    primaryVariant = green700,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

private val DarkPurpleColorPalette = darkColorScheme(
    primary = purple200,
//    primaryVariant = purple700,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

private val DarkBlueColorPalette = darkColorScheme(
    primary = blue200,
//    primaryVariant = blue700,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color.DarkGray,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

private val DarkOrangeColorPalette = darkColorScheme(
    primary = darkerBestOrange,
//    primaryVariant = orange700,
    primaryContainer = darkerBestOrange,
    secondaryContainer = darkerBestOrange,
    secondary = teal200,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

// Light pallets
private val LightGreenColorPalette = lightColorScheme(
    primary = green500,
//    primaryVariant = green700,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightPurpleColorPalette = lightColorScheme(
    primary = purple,
//    primaryVariant = purple700,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightBlueColorPalette = lightColorScheme(
    primary = blue500,
//    primaryVariant = blue700,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightOrangeColorPalette = lightColorScheme(
    primary = darkerBestOrange,
//    primaryVariant = orange700,
    secondary = teal200,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

enum class ColorPallet {
    PURPLE, GREEN, ORANGE, BLUE, WALLPAPER
}



@Composable
fun MusicPlayerTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = SettingsKeys.isSystemDark(context)
    val palette = context.dataStore.data
        .map { preferences ->
            preferences[SettingsKeys.SELECTED_COLOR_PALETTE] ?: "ORANGE"
        }.collectAsState(initial = "ORANGE").value
    val colorScheme = when (palette) {
        GREEN.name -> if (isDarkTheme) DarkGreenColorPalette else LightGreenColorPalette
        PURPLE.name -> if (isDarkTheme) DarkPurpleColorPalette else LightPurpleColorPalette
        ORANGE.name -> if (isDarkTheme) DarkOrangeColorPalette else LightOrangeColorPalette
        BLUE.name -> if (isDarkTheme) DarkBlueColorPalette else LightBlueColorPalette
        else ->  if (isDarkTheme) DarkOrangeColorPalette else LightOrangeColorPalette
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}