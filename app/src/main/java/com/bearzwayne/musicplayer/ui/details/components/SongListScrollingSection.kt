package com.bearzwayne.musicplayer.ui.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults.buttonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.details.DetailScreenUiState
import com.bearzwayne.musicplayer.ui.library.components.LibraryVerticalCardItem
import com.bearzwayne.musicplayer.ui.theme.typography

@Composable
fun SongListScrollingSection(
    uiState: DetailScreenUiState,
    contentName: String,
    songList: SnapshotStateList<Song>,
    albumsList: List<Album>,
    onLikeClick: (song: Song) -> Unit,
    onSongListItemClick: (song: Song) -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlist: Playlist) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {


    Column {

        if (albumsList.isNotEmpty()) {
            Text(
                text = stringResource(R.string.releases), maxLines = 3,
                style = typography.headlineSmall,
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp)


            )
            LazyRow {
                itemsIndexed(albumsList) { _, item ->
                    LibraryVerticalCardItem(
                        content = item,
                        onLibraryCardItemClick = { onAlbumCardClick((it as Album).id.toInt()) })
                }
            }


        }
        val isPlaylist = uiState.playlists?.filter { it.name == contentName } != null
        if (isPlaylist && songList.isEmpty()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(CircleShape),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.add_tracks),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = typography.headlineSmall.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .border(
                            border = BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.outline
                            ),
                            shape = CircleShape
                        )
                        .padding(vertical = 4.dp, horizontal = 24.dp)
                        .clickable {
                            uiState.playlists
                                ?.first { it.name == contentName }
                                ?.let { onAddTracksClick(it) }
                        }
                )
            }

        }
        SongList(
            uiState = uiState,
            playlist = songList,
            onLikeClick = onLikeClick,
            onSongListItemClick = onSongListItemClick,
            onSongListItemSettingsClick = onSongListItemSettingsClick
        )
        Spacer(modifier = Modifier.height(100.dp))
    }

}


@Composable
fun DescriptionRow(
    contentName: String,
    contentDescription: String,
    scrollState: ScrollState,
    onSettingsClick: () -> Unit,
    onShuffleClick: () -> Unit
) {
    val dynamicAlpha = 1f - ((scrollState.value + 200f) / 1000).coerceIn(0f, 1f)
    val colors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 16.dp, end = 56.dp)
            .graphicsLayer { alpha = dynamicAlpha },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.width(248.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .padding(top = 6.dp, bottom = 6.dp, end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contentName,
                    style = typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier
                        .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000)
                )
                Text(
                    text = contentDescription,
                    style = typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000)

                )
            }
            IconButton(
                onClick = onSettingsClick,
                colors = colors,

                ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        }

        IconButton(
            onClick = onShuffleClick,
            colors = colors,
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = stringResource(R.string.shuffle),
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)

            )
        }
    }
}

@Composable
fun PlayButton(
    contentName: String,
    selectedPlaylist: Playlist,
    playerState: PlayerState?,
    onPlayButtonClick: () -> Unit
) {
    val buttonIcon = remember { mutableStateOf(Icons.Default.PlayArrow) }
    if (selectedPlaylist.name == contentName && playerState == PlayerState.PLAYING) {
        buttonIcon.value = Icons.Default.Pause
    } else {
        buttonIcon.value = Icons.Default.PlayArrow
    }
    val buttonColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContentColor = Color.Gray,
        disabledContainerColor = Color.LightGray
    )

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    )

    {
        Button(
            elevation = buttonElevation(),
            shape = CircleShape,
            onClick = onPlayButtonClick,
            contentPadding = PaddingValues(0.dp),
            colors = buttonColors,
            modifier = Modifier.size(48.dp)
        )
        {
            Icon(imageVector = buttonIcon.value, contentDescription = "PLay")
        }
    }

}

