package com.example.musicplayer.ui.details.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState

@Composable
fun BottomScrollableContent(
    homeUiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    navController: NavController,
    musicControllerUiState: MusicControllerUiState,
    contentName: String,
    songList: SnapshotStateList<Song>,
    albumsList: List<Album>,
    scrollState: ScrollState,
    showSettings: MutableState<Boolean>,
    onSongListItemClick: (song: Song) -> Unit,
    onSongListItemLikeClick: (song: Song) -> Unit,
    onPlayButtonClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onAlbumCardClick: (album: Album) -> Unit,
    onAddTracksClick: (playlist: Playlist) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(state = scrollState)) {
        Spacer(modifier = Modifier.height(430.dp))
        Column(modifier = Modifier.background(Color.Transparent)) {
            Column{

                if (scrollState.value < 1044 && songList.isNotEmpty()) {
                    Box {
//                        PlayerRow()
                        PlayButton(
                            contentName = contentName,
                            selectedPlaylist = homeUiState.selectedPlaylist!!,
                            playerState = musicControllerUiState.playerState!!,
                            onPlayButtonClick = onPlayButtonClick
                        )
                        DownloadedRow(
                            scrollState = scrollState,
                            onSettingsClick = {showSettings.value = true},
                            onShuffleClick = onShuffleClick)
                    }
                } else {
                    Spacer(modifier = Modifier.height(64.dp))
                }

                SongListScrollingSection(
                    homeUiState = homeUiState,
                    onEvent = onEvent,
                    navController = navController,
                    musicControllerUiState = musicControllerUiState,
                    contentName = contentName,
                    songList = songList,
                    albumsList = albumsList,
                    onSongListItemClick = onSongListItemClick,
                    onAlbumCardClick = onAlbumCardClick,
                    onAddTracksClick = onAddTracksClick

                )

            }



        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}