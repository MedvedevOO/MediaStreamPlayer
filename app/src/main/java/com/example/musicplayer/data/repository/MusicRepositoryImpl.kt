package com.example.musicplayer.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.dto.SongDto
import com.example.musicplayer.data.localdatabase.DatabaseHelper
import com.example.musicplayer.data.localdatabase.MusicPlayerDatabase
import com.example.musicplayer.data.mapper.toSong
import com.example.musicplayer.data.remotedatabase.MusicRemoteDatabase
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.other.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val context: Context,
    private val musicRemoteDatabase: MusicRemoteDatabase
) :
    MusicRepository {

    private var allSongsList: List<Song> = emptyList()
    private var allPlaylists: List<Playlist> = emptyList()
    private var allArtists: List<Artist> = emptyList()
    private var allAlbums: List<Album> = emptyList()
    private var contentObserver: ContentObserver

    init {
        DataProvider.init(context)
        MusicPlayerDatabase.getDatabase(context)
        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                getSongs()

            }
        }
        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    override fun getSongs() =
        flow {
            val songs = fetchSongsFromDevice()
//            val remoteSongs = musicRemoteDatabase.getAllSongs().await().toObjects<SongDto>()
            val allSongs = mutableListOf<SongDto>()
            allSongs.addAll(songs)
            allSongsList = allSongs.map { it.toSong() }
            DatabaseHelper().addSongsToDatabase(allSongsList)
            if (allSongsList.isNotEmpty()) {
                emit(Resource.Success(allSongsList))
            }

        }

    override fun getPlaylists()=
        flow {
            allPlaylists = DatabaseHelper().getAllPlaylists(allSongsList)
            if (allPlaylists.isNotEmpty()) {
                emit(Resource.Success(allPlaylists))
            }
        }

    override fun getAlbums() =
        flow {
            createAlbumsFromSongs()
            if (allAlbums.isNotEmpty()) {
                emit(Resource.Success(allAlbums))
            }

        }

    override fun getArtists() =
        flow {
            createArtistsFromSongs()
            if (allArtists.isNotEmpty()) {
                emit(Resource.Success(allArtists))
            }

        }

    override fun getAlbumIdByName(name: String): Int? {
        TODO("Not yet implemented")
    }

    override fun getArtistIdByName(name: String): Int? {
        TODO("Not yet implemented")
    }

    override fun getAlbumById(id: Int): Album? {
        TODO("Not yet implemented")
    }

    override fun getArtistById(id: Int): Artist? {
        TODO("Not yet implemented")
    }

    override fun getPlaylistById(id: Int): Playlist? {
        TODO("Not yet implemented")
    }

    override fun addOrRemoveFavoriteSong(song: Song) {
        allPlaylists[2].songList = DatabaseHelper().putOrRemoveFromFavorites(song, allPlaylists[2])
    }

    override fun addNewPlaylist(newPlaylist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHelper().writeSinglePlayListToDB(newPlaylist)
        }
    }

    override fun removePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHelper().deleteSinglePlayListFromDB(playlist)

        }
    }

    override fun renamePlaylist(id: Int, name: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            DatabaseHelper().writeSinglePlayListToDB(playlist)
//        }
    }

    private fun createSongFromCursor(cursor: Cursor): SongDto {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID) // Get the column index for album ID
        val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
        val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

        val id = cursor.getLong(idColumn)
        val title = cursor.getString(titleColumn) ?: "unknown_track"
        val artist = cursor.getString(artistColumn) ?: "unknown_artist"
        val album = cursor.getString(albumColumn) ?: "unknown_album"
        val albumId = cursor.getLong(albumIdColumn)
        val genre = cursor.getString(genreColumn) ?: "unknown_genre"
        val year = cursor.getString(yearColumn) ?: "unknown_year"

        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )
        val albumArtworkUri = Uri.parse("content://media/external/audio/albumart")
        val albumArtworkUriWithAlbumId = ContentUris.withAppendedId(albumArtworkUri, albumId)
        val artwork: Uri = if (doesArtworkExist(albumArtworkUriWithAlbumId,context.contentResolver)) {
            albumArtworkUriWithAlbumId
        } else {


            Uri.parse("android.resource://com.example.musicplayer/drawable/${R.drawable.allsongsplaylist}")
        }

        return SongDto(
            mediaId = contentUri.toString(),
            title = title,
            artist = artist,
            album = album,
            genre = genre,
            year = year,
            songUrl = contentUri.toString(),
            imageUrl = artwork.toString()
        )
    }

    private fun doesArtworkExist(uri: Uri, contentResolver: ContentResolver): Boolean {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            // Perform your operations using the parcelFileDescriptor
            // For example, you can check if the file exists, etc.
        } catch (e: FileNotFoundException) {
            return false
        } finally {
            parcelFileDescriptor?.close()
        }
        return true
    }

    @SuppressLint("SuspiciousIndentation")
    private fun fetchSongsFromDevice() : List<SongDto>{
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
        val cursor = contentResolver.query(uri, projection, "${MediaStore.Audio.Media.IS_MUSIC} != 0", null,null)

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                songIds.add(id)
                val song = createSongFromCursor(cursor)
                songListItems.add(song)
            }

        }

        cursor?.close()
        return songListItems

//            currentPlaylist = allSongsList
    }

    private fun createAlbumsFromSongs(){
        val albumMap: MutableMap<String, MutableList<Song>> = mutableMapOf()

        // Step 1: Group songs by album
        for (song in allSongsList) {
            if (albumMap.containsKey(song.album)) {
                albumMap[song.album]?.add(song)
            } else {
                albumMap[song.album] = mutableListOf(song)
            }
        }

        // Step 2: Create albums from grouped songs
        val albums = mutableListOf<Album>()
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

        // Step 3: Convert albums to a list
        allAlbums = albums
    }

    private fun createArtistsFromSongs() {
        // Step 1: Group songs by artist
        val artistMap: MutableMap<String, MutableList<Song>> = mutableMapOf()
        for (song in allSongsList) {
            if (artistMap.containsKey(song.artist)) {
                artistMap[song.artist]?.add(song)
            } else {
                artistMap[song.artist] = mutableListOf(song)
            }
        }

        // Step 2: Create artists from grouped songs
        val artists = mutableListOf<Artist>()
        for ((artistName, songList) in artistMap) {
            if (allAlbums.isEmpty() && allSongsList.isNotEmpty()) {
                createAlbumsFromSongs()
            }
            val artistAlbums = allAlbums.filter { it.artist == artistName }
            val artist = Artist(
                id = artists.size, // Assuming artist IDs are incremental
                name = artistName,
                photo = songList.firstOrNull()?.imageUrl ?: "",
                genre = songList.firstOrNull()?.genre ?: "",
                albumList = artistAlbums.toMutableList(),
                songList = songList.toMutableList()
            )
            artists.add(artist)
        }

        // Step 3: Convert artists to a list
        allArtists = artists
    }
}

