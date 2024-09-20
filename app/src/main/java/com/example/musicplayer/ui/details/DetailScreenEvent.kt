package com.example.musicplayer.ui.details

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

sealed class DetailScreenEvent {
    data object PlaySong : DetailScreenEvent()//
    data object PauseSong : DetailScreenEvent()//
    data object ResumeSong : DetailScreenEvent()//
    data class AddSongListToQueue(val songList: List<Song>): DetailScreenEvent()//
    data class AddSongListNextToCurrentSong(val songList: List<Song>): DetailScreenEvent()//
    data class DeletePlaylist(val playlistName: String): DetailScreenEvent()//
    data class RenamePlaylist(val id: Int, val name: String): DetailScreenEvent()//
    data class OnPlaylistChange(val newPlaylist: Playlist): DetailScreenEvent()//
    data class OnSongSelected(val selectedSong: Song) : DetailScreenEvent()//
    data class OnSongLikeClick(val song: Song) : DetailScreenEvent()//
    data class FindArtistById(val id: Int): DetailScreenEvent()
    data class FindAlbumById(val id: Int): DetailScreenEvent()

}