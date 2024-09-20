package com.example.musicplayer.ui.sharedresources

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.musicplayer.R
import com.example.musicplayer.other.PlayerState


@Composable

fun BackgroundVideoPlayer(
    playerState: PlayerState?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoUri = "android.resource://${context.packageName}/${R.raw.pulse_background}"
    val videoView = remember {
        VideoView(context).apply {
            scaleY = 1.2f
            scaleX = 1.2f
            setVideoURI(Uri.parse(videoUri))
            setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)

                if (playerState == PlayerState.PLAYING) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.start()
                    mediaPlayer.pause()
                }

            }
        }
    }

    DisposableEffect(videoView) {
        onDispose {
            videoView.stopPlayback()
        }
    }

    AndroidView(
        factory = { videoView },
        modifier = modifier
    )

    LaunchedEffect(playerState) {
        when(playerState) {
            PlayerState.PLAYING -> {
                videoView.start()
            }
            PlayerState.PAUSED, PlayerState.STOPPED, null -> {
                videoView.pause()



            }
        }
    }
}
