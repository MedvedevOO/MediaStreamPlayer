package com.example.musicplayer.ui.home.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.data.PermissionHandler
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.sharedresources.MusicPlayerScreenAnimatedBackground
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoTracksBox() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                maxLines = 2,
                text = stringResource(id = R.string.no_tracks_found),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    shadow = Shadow(
                        MaterialTheme.colorScheme.background, blurRadius = 1f
                    )
                ),
                modifier = Modifier
                    .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun NoTrackBoxPreview(){
    val context = LocalContext.current
    val defaultCoverUri =
        Uri.parse("android.resource://${context.packageName}/${R.drawable.allsongsplaylist}").toString()

    val testSong = Song(
        mediaId = "0",
        title = "Title",
        artist = "Artist",
        album = "Album",
        genre = "Genre",
        year = "2024",
        songUrl = "",
        imageUrl = defaultCoverUri,
    )
    MusicPlayerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MusicPlayerScreenAnimatedBackground(currentSong = testSong, playerState = PlayerState.STOPPED)
        }
        NoTracksBox()
    }

}