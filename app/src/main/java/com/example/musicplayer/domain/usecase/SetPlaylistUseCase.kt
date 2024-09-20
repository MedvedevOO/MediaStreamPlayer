package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.service.MusicController
import javax.inject.Inject

class SetPlaylistUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(playlist: Playlist) {
        musicController.setPlaylist(playlist)
    }
}