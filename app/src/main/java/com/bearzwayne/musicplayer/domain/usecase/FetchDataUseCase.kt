package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(private val musicRepository: MusicRepository) {
    suspend operator fun invoke() = musicRepository.loadData()
}