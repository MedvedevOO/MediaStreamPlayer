package com.example.musicplayer.ui.home.components

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.sharedresources.albumCoverImage
import com.example.musicplayer.ui.theme.extensions.generateDominantColorState

@Composable
fun MusicPlayerScreenAnimatedBackground(currentSong: Song?, playerState: PlayerState?) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply { eraseColor(0xFF454343.toInt()) }) }
    LaunchedEffect(currentSong) {
        bitmap = albumCoverImage(currentSong?.imageUrl?.toUri()?: DataProvider.getDefaultCover(), context)
    }

    bitmap?.let { image ->
        var swatch = try {
            image.generateDominantColorState()
        } catch (e : Exception) {
            null
        }
        if (swatch == null) {
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
                eraseColor(0xFF454343.toInt())
            }
            bitmap?.let { image ->
                swatch = image.generateDominantColorState()
            }
        }
        val dominantColor = animateColorAsState(
            targetValue = Color(swatch!!.rgb),
            animationSpec = tween(durationMillis = 1000),
            label = "animateColorAsState CurrentTrackScreen"
        )

        val alphaTarget = remember {
            mutableFloatStateOf(1f)
        }

        val currentAlpha = rememberUpdatedState(newValue = alphaTarget.floatValue)

        val alpha = animateFloatAsState(targetValue = currentAlpha.value, animationSpec = tween(durationMillis = 1000),
            label = "player_alpha"
        )
        val surfaceColor = MaterialTheme.colorScheme.surface
        val dominantGradient by rememberUpdatedState(newValue = listOf(dominantColor.value, surfaceColor))

        when(playerState) {
            PlayerState.PLAYING -> {
                alphaTarget.floatValue = 0.5f
            }
            PlayerState.PAUSED, PlayerState.STOPPED, null -> {
                alphaTarget.floatValue = 0.85f
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            BackgroundVideoPlayer(
                playerState = playerState,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                dominantGradient[0],
                                MaterialTheme.colorScheme.primary.copy(alpha = alpha.value)
                            )
                        )
                    )
            )
    }

//    val swatch = remember(currentSong?.imageUrl?.toUri()) { image.generateDominantColorState() }
////
////    val swatch = image.generateDominantColorState()
//    val dominantColor = animateColorAsState(
//        targetValue = Color(swatch.rgb),
//        animationSpec = tween(durationMillis = 1000),
//        label = "animateColorAsState CurrentTrackScreen"
//    )

        //TODO: придумать как реализовать затенение
//        if (MainActivity.currentScreen.value != NavType.HOME) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        Brush.verticalGradient(
//                            colors = listOf(
//                                MaterialTheme.colorScheme.background,
//                                Color.Transparent
//                            )
//                        )
//                    )
//            )
//        }

    }
}