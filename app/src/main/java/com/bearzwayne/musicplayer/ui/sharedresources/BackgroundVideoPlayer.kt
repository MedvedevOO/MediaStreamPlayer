package com.bearzwayne.musicplayer.ui.sharedresources

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.other.PlayerState


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
            setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
            setVideoURI(Uri.parse(videoUri))
            setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                mediaPlayer.start()
                Log.d("VideoView", "PlayerState is $playerState")
                if (playerState != PlayerState.PLAYING && playerState != null) {
                    mediaPlayer.pause()
                }
            }
            contentDescription = "Background Video Player"
        }
    }

    DisposableEffect(videoView) {
        onDispose {
            videoView.stopPlayback()
        }
    }

    AndroidView(
        factory = { videoView },
        modifier = modifier.semantics { contentDescription = "background video playback" }
    )
    Log.d("VideoView", "PlayerState is $playerState")
    if (playerState == PlayerState.PLAYING) videoView.resume()

    LaunchedEffect(playerState) {
        when (playerState) {
            PlayerState.PLAYING, null -> {
                videoView.start()
            }

            PlayerState.PAUSED, PlayerState.STOPPED -> {
                videoView.pause()
            }
        }
    }
}
