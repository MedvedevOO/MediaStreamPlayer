package com.example.musicplayer.ui.home

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

sealed class HomeEvent {
    data object PlaySong : HomeEvent()
    data object PauseSong : HomeEvent()
    data object ResumeSong : HomeEvent()
    data object FetchData: HomeEvent()
    data class OnPlaylistChange(val newPlaylist: Playlist): HomeEvent()
    data class OnSongSelected(val selectedSong: Song) : HomeEvent()
    data class OnSongLikeClick(val song: Song) : HomeEvent()
}