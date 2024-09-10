package com.example.musicplayer.data.mapper

import androidx.media3.common.MediaItem
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.dto.SongDto
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song

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
        artist = mediaMetadata.subtitle.toString(),
        album = mediaMetadata.albumTitle.toString(),
        genre = mediaMetadata.genre.toString(),
        year = mediaMetadata.releaseYear.toString(),
        songUrl = mediaId,
        imageUrl = mediaMetadata.artworkUri.toString()
    )

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
