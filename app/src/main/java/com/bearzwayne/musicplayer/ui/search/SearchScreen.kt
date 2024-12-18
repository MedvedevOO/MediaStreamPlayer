package com.bearzwayne.musicplayer.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.search.components.SearchBar
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongListScrollable


@Composable
fun SearchScreen(
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
    val uiState = searchScreenViewModel.searchScreenUiState
    val onEvent = searchScreenViewModel::onEvent
    val allSongsList = uiState.allSongsPlaylist?.songList ?: emptyList()
    var searchText by remember { mutableStateOf("") }
    val filteredSongs = filterSongs(allSongsList, searchText)
    with(uiState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Column {
                SearchBar(
                    descriptionText = R.string.search_for_tracks,
                    onValueChange = { value -> searchText = value }
                )
                Column {

                    if (searchText.isNotBlank() && filteredSongs.isNotEmpty()) {
                        SongListScrollable(
                            allSongs = allSongsList,
                            selectedSongList = selectedPlaylist?.songList ?: emptyList(),
                            currentSong = uiState.selectedSong,
                            favoriteSongs = uiState.favoritesPlaylist?.songList ?: emptyList(),
                            playlist = filteredSongs,
                            playerState = uiState.playerState,
                            onSongListItemClick = {
                                onEvent(SearchScreenEvent.PlaySong(it))
                            },
                            onSongListItemLikeClick = { onEvent(SearchScreenEvent.OnSongLikeClick(it)) },
                            onSongListItemSettingsClick = onSongListItemSettingsClick
                        )
                    }
                }
            }
        }
    }

}

private fun filterSongs(allSongsList: List<Song>?, query: String): List<Song> {
    return if (allSongsList.isNullOrEmpty()) emptyList()
    else {
        allSongsList.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true)
        }
    }

}