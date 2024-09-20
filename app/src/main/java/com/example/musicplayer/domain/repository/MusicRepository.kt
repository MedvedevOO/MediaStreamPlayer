package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.Resource
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getSongs(): Flow<Resource<List<Song>>>

    fun getPlaylists(): Flow<Resource<List<Playlist>>>

    fun getAlbums(): Flow<Resource<List<Album>>>

    fun getArtists(): Flow<Resource<List<Artist>>>

    fun getAlbumIdByName(name: String): Int?

    fun getArtistIdByName(name: String): Int?

    fun getAlbumById(id: Int): Album?

    fun getArtistById(id: Int): Artist?

    fun getPlaylistById(id: Int): Playlist?

    fun addOrRemoveFavoriteSong(song: Song)

    fun addNewPlaylist(newPlaylist: Playlist)

    fun removePlaylist(playlist: Playlist)

    fun renamePlaylist(id: Int, name: String)
}