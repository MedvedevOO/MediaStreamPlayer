package com.bearzwayne.musicplayer

import android.app.Application
import com.bearzwayne.musicplayer.data.localdatabase.MusicPlayerDatabase
import com.bearzwayne.musicplayer.data.utils.DataProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DataProvider.init(applicationContext)
        MusicPlayerDatabase.getDatabase(applicationContext)
    }
}