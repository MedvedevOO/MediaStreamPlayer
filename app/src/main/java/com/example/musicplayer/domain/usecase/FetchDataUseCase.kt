package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(private val musicRepository: MusicRepository) {
    suspend operator fun invoke() = musicRepository.loadData()
}