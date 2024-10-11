package com.bearzwayne.musicplayer.ui.library.components

import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist

sealed class LibraryItem {
    data class Header(val title: String) : LibraryItem()
    data object AddPlaylist: LibraryItem()
    data class PlaylistItem(val playlist: Playlist) : LibraryItem()
    data class ArtistItem(val artist: Artist) : LibraryItem()
    data class AlbumItem(val album: Album) : LibraryItem()
}