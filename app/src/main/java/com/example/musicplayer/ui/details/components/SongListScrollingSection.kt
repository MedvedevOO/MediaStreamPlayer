package com.example.musicplayer.ui.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.details.DetailScreenEvent
import com.example.musicplayer.ui.details.DetailScreenUiState
import com.example.musicplayer.ui.library.components.LibraryVerticalCardItem
import com.example.musicplayer.ui.theme.typography

@Composable
fun SongListScrollingSection(
    uiState: DetailScreenUiState,
    onEvent: (DetailScreenEvent) -> Unit,
    contentName: String,
    songList: SnapshotStateList<Song>,
    albumsList: List<Album>,
    onSongListItemClick: (song: Song) -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlistId: Int) -> Unit,
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
            LazyRow(modifier = Modifier.height(200.dp)) {
                itemsIndexed(albumsList) { _, item ->
                    LibraryVerticalCardItem(
                        content = item,
                        onLibraryCardItemClick = { onAlbumCardClick((it as Album).id.toInt()) })
                }
            }
            

        }
        val isPlaylist = uiState.playlists?.filter { it.name == contentName } != null
        if (isPlaylist && songList.isEmpty()) {

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(CircleShape),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
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
                            onAddTracksClick(uiState.playlists?.first{it.name == contentName}!!.id)
                        }
                )
            }

        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
        SongList(
            uiState = uiState,
            onEvent = onEvent,
            playlist = songList,
            onSongListItemClick = onSongListItemClick,
            onSongListItemSettingsClick = onSongListItemSettingsClick
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
    
    }


@Composable
fun DownloadedRow(
    scrollState: ScrollState,
    onSettingsClick: () -> Unit,
    onShuffleClick: () -> Unit
) {
    val dynamicAlpha = 1f -((scrollState.value + 200f) / 1000).coerceIn(0f, 1f)
    val buttonColors = ButtonColors(containerColor = Color.LightGray, contentColor = MaterialTheme.colorScheme.background, disabledContentColor = Color.Gray, disabledContainerColor = Color.LightGray)
    val colors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .graphicsLayer { alpha = dynamicAlpha },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        IconButton(
            onClick = onSettingsClick,
            colors= colors,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
//        Button(
//            shape = CircleShape,
//            onClick = {},
//            colors = buttonColors,
//            contentPadding = PaddingValues(0.dp),
//            modifier = Modifier
//                .size(48.dp)
//
//
//        ) {
//            Icon(imageVector = Icons.Default.Download, contentDescription = stringResource(R.string.download), modifier = Modifier.background(
//                Color.Transparent
//            ))
//        }

        Spacer(modifier = Modifier.width(110.dp))

        IconButton(
            onClick = onShuffleClick,
            colors = colors,
            modifier = Modifier.size(48.dp)
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
    val buttonColors = ButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onSurface, disabledContentColor = Color.Gray, disabledContainerColor = Color.LightGray)

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth())
    {
        Button(
            elevation = buttonElevation(),
            shape = CircleShape,
            onClick = onPlayButtonClick,
            contentPadding = PaddingValues(0.dp),
            colors = buttonColors,
            modifier = Modifier.size(64.dp)
        )
        {
            Icon(imageVector = buttonIcon.value, contentDescription = "PLay")
        }
    }

}

//@Composable
//fun PlayerRow() {
////    val buttonColor = Color(parseColor("#FFA500"))
//
//    val buttonIcon = remember { mutableStateOf(Icons.Default.PlayArrow) }
//
//    if (MusicPlayerData.currentPlaylist == MusicPlayerData.selectedPlaylist && MusicPlayerData.currentTrackAndState!!.state == TrackState.PLAYING) {
//        buttonIcon.value = Icons.Default.Pause
//    } else {
//        buttonIcon.value = Icons.Default.PlayArrow
//    }
//
//    val playButtonColors = ButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onSurface, disabledContentColor = Color.Gray, disabledContainerColor = Color.LightGray)
//    val shuffleButtonColors = ButtonColors(containerColor = Color.LightGray, contentColor = MaterialTheme.colorScheme.background, disabledContentColor = Color.Gray, disabledContainerColor = Color.LightGray)
//
//    Row(
//        horizontalArrangement = Arrangement.End,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth())
//    {
//        Button(
//            shape = CircleShape,
//            onClick = DetailScreenUC::shufflePlay,
//            colors = shuffleButtonColors,
//            contentPadding = PaddingValues(0.dp),
//            modifier = Modifier.size(48.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Shuffle,
//                contentDescription = stringResource(R.string.shuffle),
//                modifier = Modifier.background(Color.Transparent)
//            )
//        }
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        Button(
//            elevation = buttonElevation(),
//            shape = CircleShape,
//            onClick = DetailScreenUC::playMusic,
//            contentPadding = PaddingValues(0.dp),
//            colors = playButtonColors,
//            modifier = Modifier.size(64.dp)
//        )
//        {
//            Icon(imageVector = buttonIcon.value, contentDescription = "PLay",)
//        }
//
//        Spacer(modifier = Modifier.width(16.dp))
//    }
//
//}

