package com.bearzwayne.musicplayer.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.data.dto.SongDto
import com.bearzwayne.musicplayer.data.localdatabase.DatabaseHelper
import com.bearzwayne.musicplayer.data.localdatabase.MusicPlayerDatabase
import com.bearzwayne.musicplayer.data.mapper.toSong
import com.bearzwayne.musicplayer.data.remotedatabase.MusicRemoteDatabase
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import com.bearzwayne.musicplayer.other.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bearzwayne.musicplayer.data.utils.createSongFromCursor
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObjects

class MusicRepositoryImpl @Inject constructor(
    private val context: Context,
    private val musicRemoteDatabase: MusicRemoteDatabase
) :
    MusicRepository {
    private val songsFlow = MutableStateFlow<Resource<List<Song>>>(Resource.Loading(emptyList()))
    private val playlistsFlow = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading(emptyList()))
    private val albumsFlow = MutableStateFlow<Resource<List<Album>>>(Resource.Loading(emptyList()))
    private val artistsFlow = MutableStateFlow<Resource<List<Artist>>>(Resource.Loading(emptyList()))
    private var contentObserver: ContentObserver

    init {
        DataProvider.init(context)
        MusicPlayerDatabase.getDatabase(context)

        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                CoroutineScope(Dispatchers.IO).launch {
                    loadData()
                }
            }
        }
        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    override suspend fun loadData() {
        try {
            songsFlow.emit(Resource.Loading())
            playlistsFlow.emit(Resource.Loading())
            albumsFlow.emit(Resource.Loading())
            artistsFlow.emit(Resource.Loading())

            val songs: MutableList<Song> = fetchSongsFromDevice().map { it.toSong() }.toMutableList()
            val remoteSongs = musicRemoteDatabase.getAllSongs().await().toObjects<SongDto>().map { it.toSong() }

            remoteSongs.forEach { song ->
                if (!songs.contains(song)) {
                    songs.add(song)
                }
            }
            DatabaseHelper().addSongsToDatabase(songs)
            val playlists = DatabaseHelper().getAllPlaylists(songs)
            val albums = createAlbumsFromSongs(songs)
            val artists = createArtistsFromSongs(songs, albums)

            songsFlow.emit(Resource.Success(songs))
            playlistsFlow.emit(Resource.Success(playlists))
            albumsFlow.emit(Resource.Success(albums))
            artistsFlow.emit(Resource.Success(artists))
        } catch (e: Exception) {
            songsFlow.emit(Resource.Error("Error loading songs: ${e.message}"))
            playlistsFlow.emit(Resource.Error("Error loading playlists: ${e.message}"))
            albumsFlow.emit(Resource.Error("Error loading albums: ${e.message}"))
            artistsFlow.emit(Resource.Error("Error loading artists: ${e.message}"))
        }
    }

    override fun getSongs(): Flow<Resource<List<Song>>> = songsFlow

    override fun getPlaylists(): Flow<Resource<List<Playlist>>> = playlistsFlow

    override fun getAlbums(): Flow<Resource<List<Album>>> = albumsFlow

    override fun getArtists(): Flow<Resource<List<Artist>>> = artistsFlow

    override fun getAlbumByName(name: String): Album? =
        (albumsFlow.value.data?.find { it.name == name })

    override fun getArtistByName(name: String): Artist? =
        (artistsFlow.value.data?.find { it.name == name })

    override fun getAlbumById(id: Int): Album? =
        albumsFlow.value.data?.find { it.id == id.toLong() }

    override fun getArtistById(id: Int): Artist? = artistsFlow.value.data?.find { it.id == id }

    override fun getPlaylistById(id: Int): Playlist? =
        playlistsFlow.value.data?.find { it.id == id }

    override fun addOrRemoveFavoriteSong(song: Song) {
        CoroutineScope(Dispatchers.IO).launch {
            val allPlaylists = playlistsFlow.value.data ?: emptyList()
            allPlaylists[2].songList =
                DatabaseHelper().putOrRemoveFromFavorites(song, allPlaylists[2])
            updatePlaylists()
        }

    }

    override fun addNewPlaylist(newPlaylist: Playlist) {
        val resultPlaylist = newPlaylist.copy(
            artWork = newPlaylist.artWork.takeIf { it != DataProvider.getDefaultCover().toString() }
                ?: newPlaylist.songList.firstOrNull()?.imageUrl
                ?: newPlaylist.artWork
        )

        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHelper().writeSinglePlayListToDB(resultPlaylist)
            updatePlaylists()
        }
    }

    override fun removePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHelper().deleteSinglePlayListFromDB(playlist)
            updatePlaylists()
        }
    }

    override fun renamePlaylist(id: Int, name: String) {
        val playlist = playlistsFlow.value.data!!.first { it.id == id }.apply {
            this.name = name
        }

        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHelper().writeSinglePlayListToDB(playlist)
            updatePlaylists()
        }
    }

    private suspend fun updatePlaylists() {
        val songs = fetchSongsFromDevice().map { it.toSong() }
        val playlists = DatabaseHelper().getAllPlaylists(songs)
        playlistsFlow.emit(Resource.Success(playlists))
    }


    @SuppressLint("SuspiciousIndentation")
    private fun fetchSongsFromDevice(): List<SongDto> {
        val songListItems = mutableListOf<SongDto>()
        val songIds = mutableListOf<Long>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.YEAR
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            null
        )

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                songIds.add(id)
                val song = createSongFromCursor(cursor, context)
                songListItems.add(song)
            }

        }

        cursor?.close()
        return songListItems
    }

    private fun createAlbumsFromSongs(songs: List<Song>): List<Album> {
        val albumMap: MutableMap<String, MutableList<Song>> = mutableMapOf()
        val albums = mutableListOf<Album>()

        for (song in songs) {
            if (albumMap.containsKey(song.album)) {
                albumMap[song.album]?.add(song)
            } else {
                albumMap[song.album] = mutableListOf(song)
            }
        }

        for ((albumName, songList) in albumMap) {
            val album = Album(
                id = albums.size.toLong(),
                name = albumName,
                artist = songList.firstOrNull()?.artist ?: "",
                genre = songList.firstOrNull()?.genre ?: "",
                year = songList.firstOrNull()?.year ?: "",
                songList = songList.toMutableList(),
                albumCover = songList.firstOrNull()?.imageUrl ?: ""
            )
            albums.add(album)
        }

        return albums
    }

    private fun createArtistsFromSongs(songs: List<Song>, albums: List<Album>): List<Artist> {
        val artistMap: MutableMap<String, MutableList<Song>> = mutableMapOf()
        val artists = mutableListOf<Artist>()
        for (song in songs) {
            if (artistMap.containsKey(song.artist)) {
                artistMap[song.artist]?.add(song)
            } else {
                artistMap[song.artist] = mutableListOf(song)
            }
        }
        for ((artistName, songList) in artistMap) {

            if (albums.isEmpty() && songs.isNotEmpty()) {
                createAlbumsFromSongs(songs)
            }
            val artistAlbums = albums.filter { it.artist == artistName }
            val artist = Artist(
                id = artists.size,
                name = artistName,
                photo = songList.firstOrNull()?.imageUrl ?: "",
                genre = songList.firstOrNull()?.genre ?: "",
                albumList = artistAlbums.toMutableList(),
                songList = songList.toMutableList()
            )
            artists.add(artist)
        }
        return artists
    }
}

