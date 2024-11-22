package com.bearzwayne.musicplayer.data.mapper

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
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
        mediaId = url,
        title = name,
        artist = country,
        album = DataProvider.getString(R.string.live_radio),
        genre = tags,
        year = "",
        songUrl = url,
        imageUrl = favicon,
        timestamp = System.currentTimeMillis()
    )
