package com.bearzwayne.musicplayer.ui.search

import androidx.compose.runtime.mutableStateListOf
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState

data class SearchScreenUiState(
    val loading: Boolean? = false,
    val selectedSong: Song? = null,
    val selectedPlaylist: Playlist? = Playlist(0, DataProvider.getString(R.string.all_tracks), mutableStateListOf(), ""),
    val allSongsPlaylist: Playlist? = null,
    val favoritesPlaylist: Playlist? = null,
    val playerState: PlayerState? = PlayerState.STOPPED,
    val errorMessage: String? = null
)
