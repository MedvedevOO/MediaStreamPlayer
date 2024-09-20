package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.service.MusicController
import javax.inject.Inject

class AddSongsNextToCurrentUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(songs: List<Song>) {
        musicController.addSongsNextToCurrentPosition(songs)
    }
}