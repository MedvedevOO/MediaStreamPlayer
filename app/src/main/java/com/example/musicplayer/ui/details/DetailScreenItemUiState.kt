package com.example.musicplayer.ui.details

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState

data class DetailScreenItemUiState(
    val loading: Boolean? = false,
    val contentType: String = "playlist",
    val contentId: Int = 0,
    val contentName: String = DataProvider.getAllTracksName(),
    val contentDescription: String = "All Tracks",
    val contentArtworkUri: Uri = Uri.EMPTY,
    val contentSongList: List<Song> = emptyList(),
    val contentAlbumsList: List<Album> = emptyList(),
    val newPlaylist: Playlist? = null,
    val errorMessage: String? = null
)
