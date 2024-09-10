package com.example.musicplayer.ui.addsongstoplaylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.ui.search.components.SearchBar
import com.example.musicplayer.data.SettingsKeys
import com.example.musicplayer.ui.addsongstoplaylist.components.AddSongsToPlaylistTopBar
import com.example.musicplayer.ui.addsongstoplaylist.components.SongListChooseItem

@Composable
fun AddSongsToPlaylistScreen(allSongsList: List<Song>, playlist: Playlist, onAddClicked: (song: Song) -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val surfaceGradient = DataProvider.surfaceGradient(SettingsKeys.isSystemDark(context)).asReversed()
    var searchText by remember { mutableStateOf("") }

    val filteredSongs = filterEditSongs(allSongsList, searchText, playlist.songList.toList())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = surfaceGradient
                )
            )
    ) {
        Column {
            AddSongsToPlaylistTopBar(onBackClick = onBackClick)
            SearchBar(
                descriptionText = R.string.search_for_tracks,
                onValueChange = { value -> searchText = value}
            )
            Column {

                    LazyColumn(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .height((filteredSongs.size * 80).dp)
                    ) {
                        itemsIndexed(filteredSongs) { _, song ->

                            SongListChooseItem(
                                song = song,
                                onAddClicked = onAddClicked
                            )
                        }
                    }

            }
        }
    }
}

private fun filterEditSongs(allSongsList: List<Song>, query: String, songList: List<Song>): List<Song> {

    return allSongsList.filter { song ->
        (song.title.contains(query, ignoreCase = true) || song.artist.contains(query, ignoreCase = true)) &&
                !songList.contains(song)
    }
}



//
//@Preview
//@Composable
//fun PreviewSpotifySearch() {
//    EditPlaylistScreen()
//}