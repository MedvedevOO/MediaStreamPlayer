package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(private val musicRepository: MusicRepository) {
    operator fun invoke(newPlaylist: Playlist) = musicRepository.removePlaylist(newPlaylist)
}