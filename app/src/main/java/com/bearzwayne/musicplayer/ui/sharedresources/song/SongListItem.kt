package com.bearzwayne.musicplayer.ui.sharedresources.song

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.theme.typography

@Composable
fun SongListItem(
    song: Song,
    allSongs: List<Song>,
    songInFavorites: Boolean,
    currentSong: Song?,
    playerState: PlayerState?,
    onItemClick: (song: Song) -> Unit,
    onSettingsClick: (song: Song) -> Unit,
    onLikeClick: (song: Song) -> Unit
) {
    val defaultColor = MaterialTheme.colorScheme.onSurface
    val likeIcon = remember { mutableStateOf(Icons.Default.FavoriteBorder) }
    val likeIconColor = remember { mutableStateOf(defaultColor) }
    Row(
        modifier = Modifier
            .height(70.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            .clickable(onClick = { onItemClick(song) }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Box(modifier = Modifier.size(48.dp)) {

            Image(
                painter = rememberAsyncImagePainter(song.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(55.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(5.dp))
            )

            if (song.songUrl == currentSong?.songUrl && playerState == PlayerState.PLAYING) {
                GifImage(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(shape = CircleShape)
                        .zIndex(1f)
                )
            }
        }
        Column(
            modifier = Modifier
                .height(70.dp)
                .weight(1f)
        ) {
            Text(
                text = song.title,
                maxLines = 1,
                style = typography.headlineMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.basicMarquee(
                    initialDelayMillis = 3000,
                    repeatDelayMillis = 3000
                )
            )
            Text(
                text = song.artist,
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(
                    initialDelayMillis = 3000,
                    repeatDelayMillis = 3000
                )
            )
        }

        if (songInFavorites) {
            if (likeIcon.value != Icons.Default.Favorite) {
                likeIcon.value = Icons.Default.Favorite
                likeIconColor.value = Color.Red
            }
        } else {
            if (likeIcon.value != Icons.Default.FavoriteBorder) {
                likeIcon.value = Icons.Default.FavoriteBorder
                likeIconColor.value = MaterialTheme.colorScheme.onSurface
            }
        }
        if (allSongs.contains(song)) {

            IconButton(
                onClick = {
                    onLikeClick(song)
                },
                modifier = Modifier
                    .size(32.dp)
                    .semantics { this.contentDescription = "Like" }
            ) {
                Icon(
                    imageVector = likeIcon.value,
                    contentDescription = null,
                    tint = likeIconColor.value,
                )
            }

            IconButton(
                onClick = {
                    onSettingsClick(song)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = defaultColor
                )
            }
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

    }
}