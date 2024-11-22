package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.service.MusicController
import javax.inject.Inject

class GetPlayerStateUseCase @Inject constructor (private val musicController: MusicController) {
    operator fun invoke() = musicController.getPlayerState()
}