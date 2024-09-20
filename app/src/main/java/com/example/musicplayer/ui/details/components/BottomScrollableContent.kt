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
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.ui.details.DetailScreenEvent
import com.example.musicplayer.ui.details.DetailScreenUiState

@Composable
fun BottomScrollableContent(
    uiState: DetailScreenUiState,
    onEvent: (DetailScreenEvent) -> Unit,
    contentName: String,
    songList: SnapshotStateList<Song>,
    albumsList: List<Album>,
    scrollState: ScrollState,
    showSettings: MutableState<Boolean>,
    onSongListItemClick: (song: Song) -> Unit,
    onSongListItemLikeClick: (song: Song) -> Unit,
    onPlayButtonClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlistId: Int) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
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
                            selectedPlaylist = uiState.selectedPlaylist!!,
                            playerState = uiState.playerState,
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
                    uiState = uiState,
                    onEvent = onEvent,
                    contentName = contentName,
                    songList = songList,
                    albumsList = albumsList,
                    onSongListItemClick = onSongListItemClick,
                    onAlbumCardClick = onAlbumCardClick,
                    onAddTracksClick = onAddTracksClick,
                    onSongListItemSettingsClick = onSongListItemSettingsClick

                )

            }



        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}