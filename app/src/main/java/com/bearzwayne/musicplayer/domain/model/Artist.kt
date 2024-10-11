package com.bearzwayne.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey
    val id: Int,
    val name: String,
    val photo: String,//uri
    val genre: String,
    val albumList: List<Album>,
    val songList: List<Song>
)
