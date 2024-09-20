package com.example.musicplayer.data.localdatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayer.domain.model.RadioStation

@Dao
interface RadioStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadioList(radioList: List<RadioStation>)

    @Update
    fun updateRadioList(radioList: List<RadioStation>)

    @Delete
    suspend fun delete(radio: RadioStation)

    @Query("SELECT * FROM favoriteStations")
    suspend fun getAllRadioStations(): List<RadioStation>

}