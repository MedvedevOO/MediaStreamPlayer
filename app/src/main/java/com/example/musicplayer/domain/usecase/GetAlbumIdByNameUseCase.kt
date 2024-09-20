package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetAlbumIdByNameUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke(name: String) = musicRepository.getAlbumIdByName(name)
}
