package com.example.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
@Entity(tableName = "favoriteStations")
data class RadioStation(
    val name: String,
    @PrimaryKey
    val url: String,
    val favicon: String,
    val country: String,
    val language: String,
    val tags: String,
    val codec: String,
    val bitrate: Int,
    val lastcheckok: Int
)