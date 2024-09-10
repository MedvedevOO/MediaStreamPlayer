package com.example.musicplayer.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState
import com.example.musicplayer.ui.search.components.SearchBar
import com.example.musicplayer.ui.search.components.SearchScreenToolBar
import com.example.musicplayer.ui.settings.AppSettingsSheet
import com.example.musicplayer.ui.sharedresources.TopPageBar
import com.example.musicplayer.ui.sharedresources.song.SongListScrollable
import com.example.musicplayer.ui.theme.modifiers.verticalGradientBackground


@Composable
fun SearchScreen(
    homeUiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    navController: NavController,
    musicControllerUiState: MusicControllerUiState
) {
    val allSongsList = homeUiState.songs
    val context = LocalContext.current
    val showAppSettings = remember { mutableStateOf(false) }
    val surfaceGradient = DataProvider.surfaceGradient(SettingsKeys.isSystemDark(context)).asReversed()
    var searchText by remember { mutableStateOf("") }
    val filteredSongs = filterSongs(allSongsList, searchText)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalGradientBackground(surfaceGradient)
    ) {

        Column {
            TopPageBar(pageName = R.string.nav_search, showAppSettings = showAppSettings)
            SearchBar(
                descriptionText = R.string.search_for_tracks,
                onValueChange = { value -> searchText = value}
            )
            Column {

                if(searchText.isNotBlank() && filteredSongs.isNotEmpty()) {
                    SongListScrollable(
                        homeUiState = homeUiState,
                        onEvent = onEvent,
                        navController = navController,
                        musicControllerUiState = musicControllerUiState,
                        playlist = filteredSongs,
                        playerState = musicControllerUiState.playerState,
                        onSongListItemClick = {
                            if (!homeUiState.selectedPlaylist!!.songList.contains(it)){
                                onEvent(HomeEvent.OnPlaylistChange(homeUiState.playlists!![0]))
                            }
                            onEvent(HomeEvent.OnSongSelected(it))
                            onEvent(HomeEvent.PlaySong)
                        }
                    )

                }
            }
        }
    }

    if (showAppSettings.value) {
        AppSettingsSheet(showAppSettings)
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



//@Preview
//@Composable
//fun PreviewSpotifySearch() {
//    SearchScreen({},{},{})
//}