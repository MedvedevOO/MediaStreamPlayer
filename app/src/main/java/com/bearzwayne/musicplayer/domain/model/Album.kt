package com.bearzwayne.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey
    val id: Long,
    val name: String,
    val artist: String,
    val genre: String,
    val year: String,
    val songList: List<Song>,
    val albumCover: String
    )
