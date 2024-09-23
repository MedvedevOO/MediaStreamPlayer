package com.example.musicplayer.ui.search

import androidx.compose.runtime.mutableStateListOf
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState

data class SearchScreenUiState(
    val loading: Boolean? = false,
    val selectedSong: Song? = null,
    val selectedPlaylist: Playlist? = Playlist(0, DataProvider.getAllTracksName(), mutableStateListOf(), ""),
    val allSongsPlaylist: Playlist? = null,
    val favoritesPlaylist: Playlist? = null,
    val playerState: PlayerState? = PlayerState.STOPPED,
    val errorMessage: String? = null
)
