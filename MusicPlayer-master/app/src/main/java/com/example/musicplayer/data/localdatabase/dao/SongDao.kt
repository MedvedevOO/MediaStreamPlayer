package com.example.musicplayer.data.localdatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayer.domain.model.Song

@Dao
interface SongDao {

    @Insert
    suspend fun insert(song: Song)

    @Delete
    suspend fun delete(song: Song)

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @Query("SELECT * FROM songs WHERE timestamp > :oneWeekAgo")
    suspend fun getSongsNewerThanOneWeek(oneWeekAgo: Long): List<Song>

    @Query("SELECT * FROM songs WHERE mediaId = :id")
    suspend fun getSongById(id: Long): Song?

    @Query("SELECT * FROM songs WHERE artist = :artistName AND title = :songTitle LIMIT 1")
    fun getSongByArtistAndTitle(artistName: String, songTitle: String): Song?

//    @Query("SELECT * FROM songs WHERE location = :location")
//    suspend fun getSongsByLocation(location: SongLocation): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Update
    suspend fun updateSongLocation(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)


}