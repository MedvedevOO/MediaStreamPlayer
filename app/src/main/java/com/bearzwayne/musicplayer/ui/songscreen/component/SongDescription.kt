package com.bearzwayne.musicplayer.ui.songscreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.theme.typography

@Composable
fun SongDescription(
    allSongs: List<Song>,
    song: Song,
    onSettingsClicked: (song: Song) -> Unit,
    onGotoArtistClick: () -> Unit,
    onGotoAlbumClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .padding(start = 40.dp, top = 8.dp, end = 32.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.height(96.dp)) {
            Text(
                text = song.title,
                style = typography.headlineMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier
                    .height(36.dp)
                    .clickable(onClick = onGotoAlbumClick)
                    .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000)
            )
            Text(
                text = song.artist,
                style = typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .height(36.dp)
                    .clickable(onClick = onGotoArtistClick)
                    .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000)

            )
        }

        if (allSongs.contains(song)) {
            Button(
                elevation = ButtonDefaults.buttonElevation(),
                shape = CircleShape,
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color.Transparent
                ),
                onClick = { onSettingsClicked(song) },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}