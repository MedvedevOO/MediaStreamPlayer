package com.bearzwayne.musicplayer.ui.details

import androidx.compose.runtime.mutableStateListOf
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState

data class DetailScreenUiState(
    val loading: Boolean? = false,
    val songs: List<Song>? = emptyList(),
    val playlists: List<Playlist>? = emptyList(),
    val playerState: PlayerState? = PlayerState.STOPPED,
    val selectedSong: Song? = null,
    var selectedPlaylist: Playlist? = Playlist(-1, DataProvider.getString(R.string.all_tracks), mutableStateListOf(), ""),

    val errorMessage: String? = null
)
