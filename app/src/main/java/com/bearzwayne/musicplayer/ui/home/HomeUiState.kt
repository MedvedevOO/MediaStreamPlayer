package com.bearzwayne.musicplayer.ui.home

import androidx.compose.runtime.mutableStateListOf
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState

data class HomeUiState(
    val loading: Boolean? = false,
    val songs: List<Song>? = emptyList(),
    val playlists: List<Playlist>? = emptyList(),
    val selectedSong: Song? = null,
    var selectedPlaylist: Playlist? = Playlist(-1, DataProvider.getAllTracksName(), mutableStateListOf(), ""),
    val playerState: PlayerState = PlayerState.STOPPED,

    val errorMessage: String? = null
)
