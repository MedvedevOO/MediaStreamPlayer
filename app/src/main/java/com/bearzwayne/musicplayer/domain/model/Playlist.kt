package com.bearzwayne.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey
    val id: Int,
    var name: String,
    var songList: List<Song>,
    var artWork: String
)
