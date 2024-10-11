package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.service.MusicController
import javax.inject.Inject

class PlayRadioStationUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(radioStation: RadioStation) {
        musicController.playRadioStation(radioStation)
    }
}