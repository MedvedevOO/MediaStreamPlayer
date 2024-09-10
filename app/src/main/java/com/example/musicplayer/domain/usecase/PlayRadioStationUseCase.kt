package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.service.MusicController
import javax.inject.Inject

class PlayRadioStationUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(radioStation: RadioStation) {
        musicController.playRadioStation(radioStation)
    }
}