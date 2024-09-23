package com.example.musicplayer.ui.details

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState

data class DetailScreenUiState(
    val loading: Boolean? = false,
    val songs: List<Song>? = emptyList(),
    val playlists: List<Playlist>? = emptyList(),
    val playerState: PlayerState? = PlayerState.STOPPED,
    val selectedSong: Song? = null,
    var selectedPlaylist: Playlist? = Playlist(-1, DataProvider.getAllTracksName(), mutableStateListOf(), ""),

    val errorMessage: String? = null
)
