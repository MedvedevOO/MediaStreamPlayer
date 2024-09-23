package com.example.musicplayer.ui.sharedresources.song

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.musicplayer.R
import com.example.musicplayer.data.SettingsKeys.isSystemDark
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.ui.sharedresources.AddNewPlaylistDialog
import com.example.musicplayer.ui.theme.graySurface
import com.example.musicplayer.ui.theme.typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongSettingsSheet(
    song: Song,
    currentSong: Song?,
    selectedPlaylist: List<Song>,
    onDismiss: () -> Unit,
    background: Color,
    onDetailMenuItemClick: (menuItem: String, song: Song) -> Unit
) {

    val menuItems = mutableMapOf(

//        Pair(stringResource(R.string.download), Icons.Default.Download),
        Pair(stringResource(R.string.add_to_playlist_variant), Icons.AutoMirrored.Filled.PlaylistAdd),
        Pair(stringResource(R.string.play_next), Icons.AutoMirrored.Filled.QueueMusic),
        Pair(stringResource(R.string.add_to_queue), Icons.AutoMirrored.Filled.PlaylistAddCheck),
        Pair(stringResource(R.string.go_to_artist), Icons.Default.Person),
        Pair(stringResource(R.string.go_to_album), Icons.Default.Album)
    )

    if (selectedPlaylist.contains(song)){
        menuItems.remove(stringResource(R.string.add_to_queue))
    }

    if (song.songUrl == currentSong?.songUrl) {
        menuItems.remove(stringResource(R.string.play_next))
    }


    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismiss,
        containerColor = background,
        modifier = Modifier
            .height((150 + 60 * menuItems.size).dp)
            .fillMaxWidth()
    ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            ) {
                item { SongContentCardItem(song) }

                menuItems.forEach { menuItem ->
                    item { SongMenuItem(
                        item = menuItem.toPair(),
                        onItemClick = {name ->
                            onDetailMenuItemClick(name, song)
                        }
                    )
                    }
                    item { HorizontalDivider(thickness = 4.dp) }
                }
            }
        Spacer(modifier = Modifier.height(64.dp))


    }

}

@Composable
fun SongSettingsBottomSheet(
    playlists: List<Playlist>,
    selectedPlaylist: List<Song>,
    currentSong: Song?,
    showSongSettings: MutableState<Boolean>,
    showAddToPlaylistDialog: MutableState<Boolean>,
    songSettingsItem: Song,
    onOkAddPlaylistClick: (newPlaylist: Playlist) -> Unit,
    onPlaylistToAddSongChosen: (updatedPlaylist: Playlist) -> Unit,
    onDetailMenuItemClick: (menuItem: String, song: Song) -> Unit
) {
    val showCreatePlaylistDialog = remember { mutableStateOf(false) }
        SongSettingsSheet(
            song = songSettingsItem,
            currentSong = currentSong,
            selectedPlaylist = selectedPlaylist,
            onDismiss = {showSongSettings.value = false},
            background = MaterialTheme.colorScheme.background,
            onDetailMenuItemClick = onDetailMenuItemClick
        )

    if(showAddToPlaylistDialog.value) {
        AddSongToPlaylistSheet(
            playlists = playlists,
            showAddToPlaylistDialog = showAddToPlaylistDialog,
            songSettingsItem = songSettingsItem,
            onCreatePlaylistClick = {showCreatePlaylistDialog.value = true},
            onPlaylistToAddSongChosen = onPlaylistToAddSongChosen
        )
    }
    if (showCreatePlaylistDialog.value) {
        AddNewPlaylistDialog(
            onOkClicked = {
                onOkAddPlaylistClick(it)
                showCreatePlaylistDialog.value = false
                          },
            showDialog = showCreatePlaylistDialog,
            allPlaylistNames = playlists.map { it.name }
        )
    }


}
@SuppressLint("SuspiciousIndentation")
@Composable
fun SongContentCardItem(song: Song) {
    val imagePainter = rememberAsyncImagePainter(song.imageUrl.toUri())
    val topCardText = song.title
    val bottomCardText = stringResource(R.string.from_album_by, song.album, song.artist)
    val shape: Shape = RoundedCornerShape(10.dp)
    val context = LocalContext.current
    val cardColor = if (isSystemDark(context)) graySurface else MaterialTheme.colorScheme.background
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .background(cardColor.copy(alpha = 0.0f))
            .clip(shape)


    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(shape),
            contentScale = ContentScale.Crop
        )

        Column {
            Text(
                text = topCardText,
                style = typography.headlineMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = bottomCardText,
                style = typography.titleLarge.copy(fontSize = 14.sp),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun SongMenuItem(
    item: Pair<String,ImageVector>,
    onItemClick: (name: String) -> Unit
) {

    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                onItemClick(item.first)
            }
    ) {
        Icon(imageVector = item.second, contentDescription = item.first, modifier = Modifier
            .padding(end = 8.dp)
            .size(24.dp))
        Text(
            text = item.first,
            style = typography.headlineMedium.copy(fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

    }
}
