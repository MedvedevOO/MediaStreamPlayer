package com.example.musicplayer.ui.details.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.ui.details.DetailScreenUiState

@Composable
fun BottomScrollableContent(
    uiState: DetailScreenUiState,
    contentName: String,
    description: String,
    songList: SnapshotStateList<Song>,
    albumsList: List<Album>,
    scrollState: ScrollState,
    showSettings: MutableState<Boolean>,
    onSongListItemClick: (song: Song) -> Unit,
    onSongListItemLikeClick: (song: Song) -> Unit,
    onPlayButtonClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlist: Playlist) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(state = scrollState)) {
        Spacer(modifier = Modifier.height(390.dp))
        Column(modifier = Modifier.background(Color.Transparent)) {
            Column{

                if (scrollState.value < 1054 && songList.isNotEmpty()) {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        PlayButton(
                            contentName = contentName,
                            selectedPlaylist = uiState.selectedPlaylist!!,
                            playerState = uiState.playerState,
                            onPlayButtonClick = onPlayButtonClick
                        )
                        DescriptionRow(
                            contentName = contentName,
                            contentDescription = description,
                            scrollState = scrollState,
                            onSettingsClick = {showSettings.value = true},
                            onShuffleClick = onShuffleClick)
                    }
                } else {
                    Spacer(modifier = Modifier.fillMaxWidth()
                        .height(52.dp)
                        .padding(end = 16.dp))
                }

                SongListScrollingSection(
                    uiState = uiState,
                    contentName = contentName,
                    songList = songList,
                    albumsList = albumsList,
                    onLikeClick = onSongListItemLikeClick,
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