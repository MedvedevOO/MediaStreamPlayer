package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetArtistUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke(id: Int) = musicRepository.getArtistById(id)
    operator fun invoke(name: String) = musicRepository.getArtistByName(name)
}
