package com.bearzwayne.musicplayer.data.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.dto.SongDto
import java.io.FileNotFoundException

fun createSongFromCursor(cursor: Cursor, context: Context): SongDto {
    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
    val albumIdColumn =
        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID) // Get the column index for album ID
    val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
    val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

    val id = cursor.getLong(idColumn)
    val title = cursor.getString(titleColumn) ?: "unknown_track"
    val artist = cursor.getString(artistColumn) ?: "unknown_artist"
    val album = cursor.getString(albumColumn) ?: "unknown_album"
    val albumId = cursor.getLong(albumIdColumn)
    val genre = cursor.getString(genreColumn) ?: "unknown_genre"
    val year = cursor.getString(yearColumn) ?: "0"

    val contentUri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        id
    )
    val albumArtworkUri = Uri.parse("content://media/external/audio/albumart")
    val albumArtworkUriWithAlbumId = ContentUris.withAppendedId(albumArtworkUri, albumId)
    val artwork: Uri =
        if (doesArtworkExist(albumArtworkUriWithAlbumId, context.contentResolver)) {
            albumArtworkUriWithAlbumId
        } else {


            Uri.parse("android.resource://com.bearzwayne.musicplayer/drawable/${R.drawable.stocksongcover}")
        }

    return SongDto(
        mediaId = contentUri.toString(),
        title = title,
        artist = artist,
        album = album,
        genre = genre,
        year = year,
        songUrl = contentUri.toString(),
        imageUrl = artwork.toString()
    )
}

fun doesArtworkExist(uri: Uri, contentResolver: ContentResolver): Boolean {
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    try {
        parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        // Perform your operations using the parcelFileDescriptor
        // For example, you can check if the file exists, etc.
    } catch (e: FileNotFoundException) {
        return false
    } finally {
        parcelFileDescriptor?.close()
    }
    return true
}