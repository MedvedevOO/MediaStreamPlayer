package com.example.musicplayer.data.localdatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicplayer.domain.model.Artist

@Dao
interface ArtistDao {
    @Insert
    suspend fun insert(artist: Artist)

    @Delete
    suspend fun delete(artist: Artist)

    @Query("SELECT * FROM artists")
    suspend fun getAllArtists(): List<Artist>

    @Query("SELECT * FROM artists WHERE id = :id")
    suspend fun getArtistById(id: Int): Artist?
}