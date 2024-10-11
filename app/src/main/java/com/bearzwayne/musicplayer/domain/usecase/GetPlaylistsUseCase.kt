package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke() = musicRepository.getPlaylists()
}
