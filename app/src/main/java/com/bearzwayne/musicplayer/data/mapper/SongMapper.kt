package com.bearzwayne.musicplayer.data.mapper

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.data.dto.SongDto
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song

fun SongDto.toSong() =
    Song(
        mediaId = mediaId,
        title = title,
        artist = artist,
        album = album,
        genre = genre,
        year = year,
        songUrl = songUrl,
        imageUrl = imageUrl
    )

fun MediaItem.toSong() =
    Song(
        mediaId = mediaId,
        title = mediaMetadata.title.toString(),
        artist = mediaMetadata.artist.toString(),
        album = mediaMetadata.albumTitle.toString(),
        genre = mediaMetadata.genre.toString(),
        year = mediaMetadata.releaseYear.toString(),
        songUrl = mediaId,
        imageUrl = mediaMetadata.artworkUri.toString()
    )

fun Song.toMediaItem() = MediaItem.Builder()
        .setMediaId(mediaId)
        .setUri(songUrl)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setGenre(genre)
                .setReleaseYear(
                    try {
                        year.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                )
                .setArtworkUri(Uri.parse(imageUrl))
                .build()
        )
        .build()
fun RadioStation.toSong() =
    Song(
        mediaId = url, // You can use the hash of the name as a unique ID
        title = name,
        artist = country, // Radio stations usually don't have a specific artist, so use a placeholder
        album = DataProvider.getString(R.string.live_radio),
        genre = tags, // Assuming tags can represent genres
        year = "", // Radio stations typically don't have a year, leave it empty
        songUrl = url, // Use the station's URL as the song's URL
        imageUrl = favicon, // Use the radio station's favicon as the image URL
        timestamp = System.currentTimeMillis() // Current timestamp
    )
