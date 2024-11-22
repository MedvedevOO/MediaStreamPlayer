package com.bearzwayne.musicplayer.data.localdatabase

import android.net.Uri
import androidx.compose.runtime.toMutableStateList
import androidx.core.net.toUri
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseHelper {
    private val musicPlayerDatabase: MusicPlayerDatabase = MusicPlayerDatabase.getDatabase()!!

    suspend fun addSongsToDatabase(songs: List<Song>) {

        musicPlayerDatabase.songDao().getAllSongs().forEach {songFromDB ->
            if (!songs.contains(songFromDB)) {
                musicPlayerDatabase.songDao().deleteSong(songFromDB)
            }
        }
        val songsFromDB = musicPlayerDatabase.songDao().getAllSongs().toMutableList()
        songs.forEach { song ->
            if (songsFromDB.isEmpty()) {
                musicPlayerDatabase.songDao().insertSong(song)
            } else {
                if (!songsFromDB.contains(song)) {
                    musicPlayerDatabase.songDao().insertSong(song)
                }

            }

        }
    }

    private suspend fun getRecentSongs(): Playlist {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        val recentlyAddedSongList =
            musicPlayerDatabase.songDao().getSongsNewerThanOneWeek(oneWeekAgo)
        val recentlyAddedSongsArtwork: Uri = if (recentlyAddedSongList.isNotEmpty()) {
            recentlyAddedSongList[0].imageUrl.toUri()
        } else {
            DataProvider.getDefaultCover()
        }
        return Playlist(
            1,
            DataProvider.getString(R.string.recently_added),
            recentlyAddedSongList,
            recentlyAddedSongsArtwork.toString()
        )
    }

    suspend fun getAllPlaylists(allSongsList: List<Song>): List<Playlist> {
        val allSongsPlaylist = Playlist(0, DataProvider.getString(R.string.all_tracks), allSongsList, DataProvider.getAllTracksCover().toString())
        val recentlyAddedPlaylist = getRecentSongs()
        val favorites = Playlist(2, DataProvider.getString(R.string.favorites), emptyList(), DataProvider.getFavoritesCover().toString())


        val resultList = mutableListOf(allSongsPlaylist,recentlyAddedPlaylist)
        if (musicPlayerDatabase.playlistDao().getPlaylistById(2) == null) {
            resultList.add(favorites.id, favorites)
            musicPlayerDatabase.playlistDao().insertPlaylist(favorites)
        }
        val playlistsFromDB: List<Playlist> = musicPlayerDatabase.playlistDao().getAllPlaylists()
        playlistsFromDB.forEach { playlist ->
            playlist.songList = playlist.songList.filter { song ->
                allSongsList.contains(song)
            }
        }

        playlistsFromDB.forEach {
            if (!resultList.contains(it)) {
                resultList.add(it.id,it)
            }

        }

        return resultList.toList()
    }

    fun putOrRemoveFromFavorites(song: Song, favoritePlaylist: Playlist): List<Song> {
        if (favoritePlaylist.songList.contains(song)){
            val newSongList = favoritePlaylist.songList.toMutableStateList().apply { remove(song) }
            favoritePlaylist.songList = newSongList
        } else {
            val newSongList = favoritePlaylist.songList.toMutableStateList().apply { add(song) }
            favoritePlaylist.songList = newSongList
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateSinglePlayListInDB(favoritePlaylist)
        }

        return favoritePlaylist.songList
    }

    suspend fun writeSinglePlayListToDB(playlist: Playlist){

        musicPlayerDatabase.playlistDao().insertPlaylist(playlist)
    }

    suspend fun updateSinglePlayListInDB(playlist: Playlist){
        musicPlayerDatabase.playlistDao().insertPlaylist(playlist)
    }

    suspend fun deleteSinglePlayListFromDB(playlist: Playlist){

        musicPlayerDatabase.playlistDao().delete(playlist)
    }

    suspend fun getFavoriteRadioStations() : List<RadioStation> {
        return musicPlayerDatabase.radioStationDao().getAllRadioStations()
    }

    suspend fun updateFavoriteRadioStations(newRadioList: List<RadioStation>) {
        val stationsInDB = getFavoriteRadioStations()
        stationsInDB.forEach { stationInDB ->
            if (!newRadioList.contains(stationInDB))
                musicPlayerDatabase.radioStationDao().delete(stationInDB)
        }
        val listToAddToDB = mutableListOf<RadioStation>()
        newRadioList.forEach {radioStation ->
            if (!stationsInDB.contains(radioStation)) {
                listToAddToDB.add(radioStation)
            }
        }
        musicPlayerDatabase.radioStationDao().insertRadioList(listToAddToDB)
    }
}