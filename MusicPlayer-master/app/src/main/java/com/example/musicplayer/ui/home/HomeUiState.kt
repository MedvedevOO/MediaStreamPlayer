package com.example.musicplayer.ui.home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

data class HomeUiState(
    val loading: Boolean? = false,
    val songs: List<Song>? = emptyList(),
    val playlists: List<Playlist>? = emptyList(),
    val artists: List<Artist>? = emptyList(),
    val albums: List<Album>? = emptyList(),
    val selectedSong: Song? = null,
    var selectedPlaylist: Playlist? = Playlist(0, DataProvider.getAllTracksName(), mutableStateListOf(), Uri.EMPTY),

    val errorMessage: String? = null
)
