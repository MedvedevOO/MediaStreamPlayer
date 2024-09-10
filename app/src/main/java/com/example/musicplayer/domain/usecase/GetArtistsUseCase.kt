package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetArtistsUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke() = musicRepository.getArtists()
}
