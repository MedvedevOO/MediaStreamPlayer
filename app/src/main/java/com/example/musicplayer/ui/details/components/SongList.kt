package com.example.musicplayer.ui.details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.ui.details.DetailScreenEvent
import com.example.musicplayer.ui.details.DetailScreenUiState
import com.example.musicplayer.ui.sharedresources.song.SongListItem

@Composable
fun SongList(
    uiState: DetailScreenUiState,
    onEvent: (DetailScreenEvent) -> Unit,
    playlist: List<Song>,
    onSongListItemClick: (song: Song) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.selectedPlaylist!!.songList) {
        listState.scrollToItem(0)
    }

    Column {
        playlist.forEach { item ->
            SongListItem(
                song = item,
                allSongs = uiState.songs!!,
                songInFavorites = uiState.playlists?.get(2)?.songList?.contains(item) ?: false,
                currentSong = uiState.selectedSong,
                playerState = uiState.playerState,
                onItemClick = { onSongListItemClick(it) },
                onSettingsClick = onSongListItemSettingsClick,
                onLikeClick = {
                    onEvent(DetailScreenEvent.OnSongLikeClick(it))
                }
            )
        }
    }
}