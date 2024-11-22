package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetPlaylistByIdUseCase @Inject constructor (private val musicRepository: MusicRepository) {
    operator fun invoke(id: Int) = musicRepository.getPlaylistById(id)
}
