package com.example.musicplayer.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.musicplayer.domain.model.Playlist
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
object Home

@Serializable
object Radio

@Serializable
object Search

@Serializable
object Library

@Serializable
data class Detail(
    val type: String,
    val id: Int? = null,
    val name: String? = null
)

@Serializable
data class AddSongs(
    val playlist: Playlist
)

@Serializable
data class EditPlaylist(
    val playlist: Playlist
)

object CustomNavType {
    val playlistType  = object : NavType<Playlist>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): Playlist? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): Playlist {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: Playlist): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: Playlist) {
            bundle.putString(key, Json.encodeToString(value))
        }

    }
}