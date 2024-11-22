package com.bearzwayne.musicplayer.ui.details

import android.net.Uri
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song

data class DetailScreenItemUiState(
    val loading: Boolean? = false,
    val contentType: String = "playlist",
    val contentId: Int = 0,
    val contentName: String = DataProvider.getString(R.string.all_tracks),
    val contentDescription: String = "All Tracks",
    val contentArtworkUri: Uri = Uri.EMPTY,
    val contentSongList: List<Song> = emptyList(),
    val contentAlbumsList: List<Album> = emptyList(),
    val newPlaylist: Playlist? = null,
    val errorMessage: String? = null
)
