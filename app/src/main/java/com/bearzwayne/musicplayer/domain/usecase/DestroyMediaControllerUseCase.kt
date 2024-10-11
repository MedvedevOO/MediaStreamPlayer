package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.service.MusicController
import javax.inject.Inject

class DestroyMediaControllerUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke() {
        musicController.destroy()
    }
}