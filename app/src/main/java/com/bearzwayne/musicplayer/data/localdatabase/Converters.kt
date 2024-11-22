package com.bearzwayne.musicplayer.data.localdatabase

import android.net.Uri
import androidx.room.TypeConverter
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }

    @TypeConverter
    fun fromSongList(songList: List<Song>): String {
        val gson = Gson()
        return gson.toJson(songList)
    }

    @TypeConverter
    fun toSongList(songListString: String): List<Song> {
        val gson = Gson()
        val listType = object : TypeToken<List<Song>>() {}.type
        return gson.fromJson(songListString, listType)
    }

    @TypeConverter
    fun fromAlbumList(songList: List<Album>): String {
        val gson = Gson()
        return gson.toJson(songList)
    }

    @TypeConverter
    fun toAlbumList(songListString: String): List<Album> {
        val gson = Gson()
        val listType = object : TypeToken<List<Album>>() {}.type
        return gson.fromJson(songListString, listType)
    }

    @TypeConverter
    fun fromArtistList(songList: List<Artist>): String {
        val gson = Gson()
        return gson.toJson(songList)
    }

    @TypeConverter
    fun toArtistList(songListString: String): List<Artist> {
        val gson = Gson()
        val listType = object : TypeToken<List<Artist>>() {}.type
        return gson.fromJson(songListString, listType)
    }




}