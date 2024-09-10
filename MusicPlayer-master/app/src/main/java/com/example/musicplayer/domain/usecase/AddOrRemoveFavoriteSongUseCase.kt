package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class AddOrRemoveFavoriteSongUseCase @Inject constructor(private val musicRepository: MusicRepository) {
    operator fun invoke(song: Song) = musicRepository.addOrRemoveFavoriteSong(song)
}