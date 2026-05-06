package com.bearzwayne.musicplayer.ui.util

import androidx.compose.ui.graphics.Color
import com.bearzwayne.musicplayer.ui.graySurface

fun surfaceGradient(isDark: Boolean) =
    if (isDark) listOf(graySurface.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.7f)) else listOf(
        Color.White.copy(alpha = 0.7f), Color.LightGray.copy(alpha = 0.7f))
