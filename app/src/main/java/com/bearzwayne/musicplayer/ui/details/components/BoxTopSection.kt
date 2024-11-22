package com.bearzwayne.musicplayer.ui.details.components

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun BoxTopSection(
    contentArtworkUri: Uri,
    scrollState: ScrollState
) {

    val imagePainter: Painter = rememberAsyncImagePainter(model = contentArtworkUri)
    val shape: Shape = RoundedCornerShape(10.dp)
    val dynamicAlpha = 1f - ((scrollState.value + 0.00f) / 1000).coerceIn(0f, 1f)

    val dynamicValue =
        if (300.dp - Dp(scrollState.value / 50f) < 10.dp) 10.dp //prevent going 0 cause crash
        else 300.dp - Dp(scrollState.value / 20f)

    val animateImageSize = animateDpAsState(dynamicValue, label = "animateImageSize").value

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth()
        )

        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .size(animateImageSize)
                .padding(8.dp)
                .clip(shape),
            alpha = dynamicAlpha

        )
    }
}

@Composable
fun TopSectionOverlay(
    contentArtworkUri: Uri,
    scrollState: ScrollState,
    gradient: List<Color>
) {
    val dynamicAlpha = ((scrollState.value + 0.00f) / 1000).coerceIn(0f, 1f)
    val animatedAlpha = animateFloatAsState(dynamicAlpha, label = "animatedAlpha").value
    val gradientWithAlpha = gradient.map { it.copy(alpha = animatedAlpha) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientWithAlpha
                )
            )
    ) {
        BoxTopSection(contentArtworkUri = contentArtworkUri, scrollState = scrollState)
    }
}
