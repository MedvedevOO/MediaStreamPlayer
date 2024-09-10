package com.example.musicplayer.domain.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey
    val id: Int,
    var name: String,
    var songList: List<Song>,
    var artWork: Uri // uri
)
//{
//    fun toEntity(): PlaylistEntity {
//        return PlaylistEntity(
//            id = id,
//            name = name.value,
//            songList = songList.toList(),
//            artWork = artWork
//        )
//    }
//}