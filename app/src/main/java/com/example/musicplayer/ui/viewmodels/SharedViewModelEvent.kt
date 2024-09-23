package com.example.musicplayer.ui.viewmodels

import android.content.Context
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

sealed class SharedViewModelEvent {
    data class AddNewPlaylist(val newPlaylist: Playlist) : SharedViewModelEvent()//
    data class AddSongToNewPlaylist(
        val newPlaylist: Playlist,
        val newSong: Song,
        val context: Context
    ) : SharedViewModelEvent()
    data class AddSongListToQueue(val songList: List<Song>) : SharedViewModelEvent()
    data class AddSongNextToCurrentSong(val song: Song) : SharedViewModelEvent()
}