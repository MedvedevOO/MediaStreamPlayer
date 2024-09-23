package com.example.musicplayer.ui.home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

data class HomeUiState(
    val loading: Boolean? = false,
    val songs: List<Song>? = emptyList(),
    val playlists: List<Playlist>? = emptyList(),
    val selectedSong: Song? = null,
    var selectedPlaylist: Playlist? = Playlist(-1, DataProvider.getAllTracksName(), mutableStateListOf(), ""),

    val errorMessage: String? = null
)
