package com.example.musicplayer.data.localdatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.musicplayer.domain.model.Genre

@Dao
interface GenreDao {
    @Insert
    suspend fun insert(genre: Genre)

    @Delete
    suspend fun delete(genre: Genre)

    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<Genre>

    @Query("SELECT * FROM genres WHERE id = :id")
    suspend fun getGenreById(id: Int): Genre?
}