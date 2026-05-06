package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.service.MusicController
import javax.inject.Inject

class SetPlaylistUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(playlist: Playlist) {
        musicController.setPlaylist(playlist)
    }
}