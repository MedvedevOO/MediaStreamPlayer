package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.service.MusicController
import com.example.musicplayer.other.PlayerState
import javax.inject.Inject

class SetMediaControllerCallbackUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(
        callback: (
            playerState: PlayerState,
            currentPlaylist: Playlist?,
            previousSong: Song?,
            currentSong: Song?,
            nextSong: Song?,
            currentPosition: Long,
            totalDuration: Long,
            isShuffleEnabled: Boolean,
            isRepeatOneEnabled: Boolean
        ) -> Unit
    ) {
        musicController.mediaControllerCallback = callback
    }
}