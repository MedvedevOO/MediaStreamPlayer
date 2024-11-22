package com.bearzwayne.musicplayer.ui.details.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@Composable
fun AnimatedGradientBackgroundBox(dominantGradient: List<Color>) {
    val isInfiniteTransition = rememberInfiniteTransition(label = "background Detail screen")
    val isAnimated = isInfiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing, delayMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ), label = "AnimateGradient"
    )
    
    val animatedColors = dominantGradient.zip(dominantGradient.reversed()) { startColor, endColor ->
        val animatedColor = lerp(startColor, endColor, isAnimated.value)
        animatedColor
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = animatedColors
                )
            )
    ) {
    }
}
