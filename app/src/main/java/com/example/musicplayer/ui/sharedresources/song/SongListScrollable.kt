package com.example.musicplayer.ui.sharedresources.song

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState

@Composable
fun SongListScrollable(
    allSongs: List<Song>,
    selectedSongList: List<Song>,
    currentSong: Song?,
    favoriteSongs: List<Song>,
    playlist: List<Song>,
    playerState: PlayerState?,
    modifier: Modifier = Modifier,
    onSongListItemClick: (song: Song) -> Unit,
    onSongListItemLikeClick: (song: Song) -> Unit,
    onSongListItemSettingsClick : (song: Song) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedSongList) {
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
                     allSongs = allSongs,
                     songInFavorites = favoriteSongs.contains(item),
                     currentSong = currentSong,
                     playerState = playerState,
                     onItemClick = onSongListItemClick,
                     onSettingsClick = onSongListItemSettingsClick,
                     onLikeClick = onSongListItemLikeClick
                 )

            }
        )
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }



}