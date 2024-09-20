package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.service.MusicController
import javax.inject.Inject

class SkipToPreviousSongUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke() = musicController.skipToPreviousSong()

}