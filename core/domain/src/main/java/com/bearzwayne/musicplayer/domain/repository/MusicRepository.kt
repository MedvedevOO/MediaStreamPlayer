package com.bearzwayne.musicplayer.domain.repository

import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.Resource
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun loadData()

    fun getSongs(): Flow<Resource<List<Song>>>

    fun getPlaylists(): Flow<Resource<List<Playlist>>>

    fun getAlbums(): Flow<Resource<List<Album>>>

    fun getArtists(): Flow<Resource<List<Artist>>>

    fun getAlbumByName(name: String): Album?

    fun getArtistByName(name: String): Artist?

    fun getAlbumById(id: Int): Album?

    fun getArtistById(id: Int): Artist?

    fun getPlaylistById(id: Int): Playlist?

    fun addOrRemoveFavoriteSong(song: Song)

    fun addNewPlaylist(newPlaylist: Playlist)

    fun removePlaylist(playlist: Playlist)

    fun renamePlaylist(id: Int, name: String)
}