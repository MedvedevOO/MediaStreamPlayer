package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetAlbumUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke(id: Int) = musicRepository.getAlbumById(id)
    operator fun invoke(name: String) = musicRepository.getAlbumByName(name)
}
