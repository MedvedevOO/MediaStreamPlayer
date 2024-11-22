package com.bearzwayne.musicplayer.ui.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.ui.theme.typography

@SuppressLint("SuspiciousIndentation")
@Composable
fun QuickAccessItem(
    playlist: Playlist,
    onQuickAccessItemClick: (playlist: Playlist) -> Unit
) {
    var painter1 = rememberAsyncImagePainter(DataProvider.getDefaultCover())
    var painter2 = rememberAsyncImagePainter(DataProvider.getDefaultCover())
    if (playlist.songList.isNotEmpty()) {
        painter1 = rememberAsyncImagePainter(playlist.songList[0].imageUrl.toUri())
    }
    if (playlist.songList.size >= 2) {
        painter2 = rememberAsyncImagePainter(playlist.songList[1].imageUrl.toUri())
    }
    val recentlyAddedArtists = mutableListOf<String>()
    playlist.songList.map {
        recentlyAddedArtists.add(it.artist)
    }

    Row(
        modifier = Modifier
            .height(64.dp)
            .width(230.dp)
            .padding(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
            .clickable {
                onQuickAccessItemClick(playlist)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .width(79.dp)
        ) {
            Image(
                painter = painter1,
                contentDescription = "First image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painter2,
                contentDescription = "Second image",
                modifier = Modifier
                    .size(50.dp)
                    .offset(25.dp, 0.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Column {
            Text(
                playlist.name,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000),
                style = typography.titleSmall.copy(fontSize = 12.sp)
            )

            Text(
                recentlyAddedArtists.toString().trim('[', ']'),
                modifier = Modifier
                    .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000),
                style = typography.titleSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}
