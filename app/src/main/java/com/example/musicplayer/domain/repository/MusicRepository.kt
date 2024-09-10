package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.other.Resource
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getSongs(): Flow<Resource<List<Song>>>

    fun getPlaylists(): Flow<Resource<List<Playlist>>>

    fun getAlbums(): Flow<Resource<List<Album>>>

    fun getArtists(): Flow<Resource<List<Artist>>>

    fun addOrRemoveFavoriteSong(song: Song)

    fun addNewPlaylist(newPlaylist: Playlist)

    fun removePlaylist(playlist: Playlist)

    fun renamePlaylist(playlist: Playlist)
}