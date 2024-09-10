package com.example.musicplayer.ui.home

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

sealed class HomeEvent {
    data object PlaySong : HomeEvent()
    data object PauseSong : HomeEvent()
    data object ResumeSong : HomeEvent()
    data object FetchSong : HomeEvent()
    data object SkipToNextSong : HomeEvent()
    data object SkipToPreviousSong : HomeEvent()
    data object SeekToStartOfSong: HomeEvent()
    data class AddNewPlaylist(val newPlaylist: Playlist): HomeEvent()
    data class AddSongListToQueue(val songList: List<Song>): HomeEvent()
    data class AddSongListNextToCurrentSong(val songList: List<Song>): HomeEvent()
    data class AddSongNextToCurrentSong(val song: Song): HomeEvent()
    data class DeletePlaylist(val playlist: Playlist): HomeEvent()
    data class RenamePlaylist(val id: Int, val name: String): HomeEvent()
    data class OnPlaylistChange(val newPlaylist: Playlist): HomeEvent()
    data class OnSongSelected(val selectedSong: Song) : HomeEvent()
    data class OnSongLikeClick(val song: Song) : HomeEvent()
}