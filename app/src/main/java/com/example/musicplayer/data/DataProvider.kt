package com.example.musicplayer.data

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.graySurface

object DataProvider {
    private lateinit var appContext: Context
    private lateinit var allTracksName: String
    private lateinit var favoritesName: String
    private lateinit var recentlyAddedName: String
    private lateinit var allTracksCover: Uri
    private lateinit var defaultCoverUri: Uri
    private lateinit var favoritesCoverUri: Uri

    fun init(context: Context) {
        appContext = context.applicationContext
        allTracksName = getString(R.string.all_tracks)
        favoritesName = getString(R.string.favorites)
        recentlyAddedName = getString(R.string.recently_added)
        allTracksCover = Uri.parse("android.resource://com.example.musicplayer/drawable/${R.drawable.allsongsplaylist}")
        defaultCoverUri = Uri.parse("android.resource://com.example.musicplayer/drawable/${R.drawable.stocksongcover}")
        favoritesCoverUri = Uri.parse("android.resource://com.example.musicplayer/drawable/${R.drawable.likedsongs}")

    }

    fun getString(@StringRes id: Int): String {
        return appContext.getString(id)

    }

    fun getString(@StringRes id: Int, parameter: Any): String {
        return appContext.getString(id, parameter)

    }

    fun getString(@StringRes id: Int, numberA: Int, numberB: Int): String {
        return appContext.getString(id, numberA, numberB)

    }
    fun surfaceGradient(isDark: Boolean) =
        if (isDark) listOf(graySurface.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.7f)) else listOf(Color.White.copy(alpha = 0.7f), Color.LightGray.copy(alpha = 0.7f))

    fun getAllTracksName(): String {
        return allTracksName
    }

    fun getAllTracksCover(): Uri {
        return allTracksCover
    }

    fun getDefaultCover(): Uri {
        return defaultCoverUri
    }

    fun getFavoritesCover(): Uri {
        return favoritesCoverUri
    }

    fun getFavoritesName(): String {
        return favoritesName
    }

    fun getRecentlyAddedName(): String {
        return recentlyAddedName
    }


}