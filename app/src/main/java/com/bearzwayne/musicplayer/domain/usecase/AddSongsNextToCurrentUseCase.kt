package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.service.MusicController
import javax.inject.Inject

class AddSongsNextToCurrentUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(songs: List<Song>) {
        musicController.addSongsNextToCurrentPosition(songs)
    }
}