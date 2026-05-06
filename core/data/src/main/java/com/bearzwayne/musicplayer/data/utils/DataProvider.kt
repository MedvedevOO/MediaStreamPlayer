package com.bearzwayne.musicplayer.data.utils

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import com.bearzwayne.musicplayer.data.R
import androidx.core.net.toUri

object DataProvider {
    private lateinit var appContext: Context
    private lateinit var allTracksCover: Uri
    private lateinit var defaultCoverUri: Uri
    private lateinit var favoritesCoverUri: Uri

    fun init(context: Context) {
        appContext = context.applicationContext
        allTracksCover =
            "android.resource://com.bearzwayne.musicplayer/drawable/${R.drawable.allsongsplaylist}".toUri()
        defaultCoverUri =
            "android.resource://com.bearzwayne.musicplayer/drawable/${R.drawable.stocksongcover}".toUri()
        favoritesCoverUri =
            "android.resource://com.bearzwayne.musicplayer/drawable/${R.drawable.likedsongs}".toUri()

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

    fun getAllTracksCover(): Uri {
        return allTracksCover
    }

    fun getDefaultCover(): Uri {
        return defaultCoverUri
    }

    fun getFavoritesCover(): Uri {
        return favoritesCoverUri
    }


}