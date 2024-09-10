package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke() = musicRepository.getAlbums()
}
