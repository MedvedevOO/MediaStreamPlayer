package com.bearzwayne.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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