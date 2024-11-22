package com.bearzwayne.musicplayer.ui.home.components

import android.net.Uri

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.sharedresources.MusicPlayerScreenAnimatedBackground
import com.bearzwayne.musicplayer.ui.theme.MusicPlayerTheme


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