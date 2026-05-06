package com.bearzwayne.musicplayer.ui.viewmodels

import android.content.Context
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song

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