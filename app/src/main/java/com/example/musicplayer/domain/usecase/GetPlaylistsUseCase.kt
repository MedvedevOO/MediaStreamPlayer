package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke() = musicRepository.getPlaylists()
}
