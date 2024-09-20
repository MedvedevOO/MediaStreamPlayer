package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetArtistIdByNameUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke(name: String) = musicRepository.getArtistIdByName(name)
}
