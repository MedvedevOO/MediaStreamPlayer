package com.bearzwayne.musicplayer


import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.ui.MusicPlayerApp
import com.bearzwayne.musicplayer.ui.theme.MusicPlayerTheme
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       //actionBar?.hide()
        DataProvider.init(this.applicationContext)
        setContent {
            MusicPlayerTheme {
                MusicPlayerApp(
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }
}


