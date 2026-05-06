package com.bearzwayne.musicplayer.other

import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song

data class MusicControllerUiState(
    var loading: Boolean = false,
    var songs: List<Song>? = emptyList(),
    var playlists: List<Playlist> = emptyList(),
    var selectedPlaylist: Playlist? = null,
    val playerState: PlayerState? = null,
    val previousSong: Song? = null,
    val currentSong: Song? = null,
    val nextSong: Song? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val isRepeatOneEnabled: Boolean = false,
    val errorMessage: String? = null
)
