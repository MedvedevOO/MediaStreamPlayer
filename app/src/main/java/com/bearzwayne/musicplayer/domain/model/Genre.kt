package com.bearzwayne.musicplayer.domain.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey
    val id: Int,
    val name: String,
    val artWork: Uri, //uri
    val artists: List<Artist>,
    val albums: List<Album>,
    val songList: List<Song>
    )
