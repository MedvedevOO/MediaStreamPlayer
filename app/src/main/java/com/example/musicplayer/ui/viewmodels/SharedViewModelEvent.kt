package com.example.musicplayer.ui.viewmodels

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

sealed class SharedViewModelEvent {
    data class AddNewPlaylist(val newPlaylist: Playlist): SharedViewModelEvent()//
    data class AddSongListToQueue(val songList: List<Song>): SharedViewModelEvent()
    data class AddSongNextToCurrentSong(val song: Song): SharedViewModelEvent()
    data class FindArtistIdByName(val name: String): SharedViewModelEvent()
    data class FindAlbumIdByName(val name: String): SharedViewModelEvent()
    data class FindPlaylistById(val id: Int): SharedViewModelEvent()
    data object GetSongs : SharedViewModelEvent()
}