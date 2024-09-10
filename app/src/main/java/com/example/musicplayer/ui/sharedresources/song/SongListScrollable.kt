package com.example.musicplayer.ui.sharedresources.song

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState

@Composable
fun SongListScrollable(
    homeUiState: HomeUiState,
    musicControllerUiState: MusicControllerUiState,
    navController: NavController,
    onEvent: (HomeEvent) -> Unit,
    playlist: List<Song>,
    playerState: PlayerState?,
    modifier: Modifier = Modifier,
    onSongListItemClick: (song: Song) -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val showSongSettings = remember { mutableStateOf(false) }
    val showAddToPlaylistDialog = remember { mutableStateOf(false) }
    val songSettingsItem = remember { mutableStateOf(
        //Todo: иправить костыль
        Song(
        mediaId = "",
        title = "Test Song",
        artist = "Test Artist",
        album = " Test Album",
        imageUrl = DataProvider.getDefaultCover().toString(),
        genre = "Pop",
        year = "2022",
            songUrl = ""
    ))
    }
    val onSettingsClickedLambda = remember<(Song) -> Unit> {
        {
            showSongSettings.value = true
            songSettingsItem.value = it
        }
    }
    LaunchedEffect(homeUiState.selectedPlaylist!!.songList) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(
            count = playlist.size,
            key = {
                playlist[it].mediaId
            },
            itemContent = { index ->
                val item = playlist[index]
                 SongListItem(
                     song = item,
                     homeUiState = homeUiState,
                     currentSong = musicControllerUiState.currentSong,
                     playerState = playerState,
                     onItemClick = { onSongListItemClick(it) },
                     onSettingsClick = onSettingsClickedLambda,
                     onLikeClick = {
                             onEvent(HomeEvent.OnSongLikeClick(it))
                     }
                 )

            }
        )
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    SongSettingsItem(
        homeUiState = homeUiState,
        currentSong = musicControllerUiState.currentSong,
        showSongSettings = showSongSettings,
        showAddToPlaylistDialog = showAddToPlaylistDialog,
        songSettingsItem = songSettingsItem.value,
        surfaceGradient = listOf(Color.Black, Color.Transparent),
        onOkAddPlaylistClick = {newPlaylist ->

            val newSongList = newPlaylist.songList.toMutableList().apply { add(songSettingsItem.value) }
            val resultList = newPlaylist.copy(
                songList = newSongList,
                artWork = songSettingsItem.value.imageUrl.toUri()
            )
            onEvent(HomeEvent.AddNewPlaylist(resultList))
            val toastText = context.getString(R.string.track_added_to_playlist, resultList.name)
            Toast.makeText(context,toastText, Toast.LENGTH_SHORT).show()
            showAddToPlaylistDialog.value = false

        },
        onPlaylistToAddSongChosen = {
            onEvent(HomeEvent.AddNewPlaylist(it))
        },
        onDetailMenuItemClick = {menuItem, song ->
            showSongSettings.value = false

            when(menuItem) {
                context.getString(R.string.download) -> {}
                context.getString(R.string.add_to_playlist_variant)  -> showAddToPlaylistDialog.value = true
                context.getString(R.string.add_to_queue)  -> onEvent(HomeEvent.AddSongListToQueue(listOf(song)))
                context.getString(R.string.play_next)  -> onEvent(HomeEvent.AddSongNextToCurrentSong(song))
                context.getString(R.string.go_to_artist)  -> {
                    val author = homeUiState.artists!!.find { it.name == song.artist }
                    navController.navigate("detail/artist/${author!!.id}")

                }
                context.getString(R.string.go_to_album)  -> {
                    val album = homeUiState.albums!!.find { it.name == song.album }
                    navController.navigate("detail/album/${album!!.id}")



                }

            }
        }
    )


}