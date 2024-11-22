package com.bearzwayne.musicplayer.ui.home

import androidx.compose.runtime.mutableStateListOf
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song

data class HomeUiState(
    val loading: Boolean? = false,
    val songs: List<Song>? = emptyList(),
    val playlists: List<Playlist>? = emptyList(),
    val selectedSong: Song? = null,
    var selectedPlaylist: Playlist? = Playlist(-1, DataProvider.getString(R.string.all_tracks), mutableStateListOf(), ""),

    val errorMessage: String? = null
)
