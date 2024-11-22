package com.bearzwayne.musicplayer.ui.library

import com.bearzwayne.musicplayer.domain.model.Playlist

sealed class LibraryScreenEvent {
    data class AddNewPlaylist(val newPlaylist: Playlist): LibraryScreenEvent()
    data class DeletePlaylist(val playlist: Playlist): LibraryScreenEvent()
    data class RenamePlaylist(val id: Int, val name: String): LibraryScreenEvent()
}