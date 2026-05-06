package com.bearzwayne.musicplayer.data.localdatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bearzwayne.musicplayer.domain.model.Album

@Dao
interface AlbumDao {
    @Insert
    suspend fun insert(album: Album)

    @Delete
    suspend fun delete(album: Album)

    @Query("SELECT * FROM albums")
    suspend fun getAllAlbums(): List<Album>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): Album?

    @Query("SELECT MAX(id) FROM albums")
    suspend fun getMaxAlbumId(): Long?
}