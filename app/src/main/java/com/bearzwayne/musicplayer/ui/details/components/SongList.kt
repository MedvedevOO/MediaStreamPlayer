package com.bearzwayne.musicplayer.ui.details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.details.DetailScreenUiState
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongListItem

@Composable
fun SongList(
    uiState: DetailScreenUiState,
    playlist: List<Song>,
    onLikeClick: (song:Song) -> Unit,
    onSongListItemClick: (song: Song) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.selectedPlaylist!!.songList) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        playlist.forEach { item ->
            SongListItem(
                song = item,
                allSongs = uiState.songs!!,
                songInFavorites = uiState.playlists?.get(2)?.songList?.contains(item) ?: false,
                currentSong = uiState.selectedSong,
                playerState = uiState.playerState,
                onItemClick = { onSongListItemClick(it) },
                onSettingsClick = onSongListItemSettingsClick,
                onLikeClick = onLikeClick
            )
        }
    }
}