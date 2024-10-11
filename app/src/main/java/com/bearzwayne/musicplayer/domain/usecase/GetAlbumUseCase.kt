package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetAlbumUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke(id: Int) = musicRepository.getAlbumById(id)
    operator fun invoke(name: String) = musicRepository.getAlbumByName(name)
}
