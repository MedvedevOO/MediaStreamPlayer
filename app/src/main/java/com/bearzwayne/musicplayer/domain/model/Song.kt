package com.bearzwayne.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val mediaId: String,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val year: String,
    val songUrl: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Song

        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        return result
    }
}
