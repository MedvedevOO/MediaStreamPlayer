package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.service.MusicController
import javax.inject.Inject

class SeekSongToPositionUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke(position: Long) {
        musicController.seekTo(position)
    }
}