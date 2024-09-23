package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.service.MusicService
import com.example.musicplayer.ui.MusicPlayerApp
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.ui.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataProvider.init(this.applicationContext)
        setContent {
            MusicPlayerTheme {
                MusicPlayerApp(
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.destroyMediaController()
        stopService(Intent(this, MusicService::class.java))
    }
}


