package com.bearzwayne.musicplayer.ui.library

import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist

data class LibraryScreenUiState(
    val loading: Boolean? = false,
    val playlists: List<Playlist>? = emptyList(),
    val artists: List<Artist>? = emptyList(),
    val albums: List<Album>? = emptyList(),
    val errorMessage: String? = null
)
