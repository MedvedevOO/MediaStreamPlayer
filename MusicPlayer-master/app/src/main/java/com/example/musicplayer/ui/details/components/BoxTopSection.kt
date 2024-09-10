package com.example.musicplayer.ui.details.components

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.musicplayer.ui.theme.typography


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxTopSection(
    contentName: String,
    contentArtworkUri: Uri,
    description: String,
    scrollState: ScrollState
) {

    val imagePainter: Painter = rememberAsyncImagePainter(model = contentArtworkUri)
    val shape: Shape = RoundedCornerShape(10.dp)
    val dynamicAlpha = 1f - ((scrollState.value + 0.00f) / 1000).coerceIn(0f, 1f)

    val dynamicValue =
        if (250.dp - Dp(scrollState.value / 50f) < 10.dp) 10.dp //prevent going 0 cause crash
        else 250.dp - Dp(scrollState.value / 20f)

    val animateImageSize = animateDpAsState(dynamicValue, label = "animateImageSize").value

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(
            modifier = Modifier
                .height(64.dp)
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
        Text(
            text = contentName,
            maxLines = 1,
            style = typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier
                .padding(8.dp)
                .alpha(dynamicAlpha)
                .basicMarquee(delayMillis = 2000, initialDelayMillis = 2000),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = description,
            style = typography.titleLarge,
            modifier = Modifier
                .padding(4.dp)
                .alpha(dynamicAlpha)
        )
    }
}

@Composable
fun TopSectionOverlay(
    contentName: String,
    contentArtworkUri: Uri,
    description: String,
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
        BoxTopSection(contentName = contentName, contentArtworkUri = contentArtworkUri, description = description, scrollState = scrollState)
    }
}
