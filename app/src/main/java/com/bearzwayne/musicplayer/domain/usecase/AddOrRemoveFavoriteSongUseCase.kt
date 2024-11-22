package com.bearzwayne.musicplayer.domain.usecase

import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class AddOrRemoveFavoriteSongUseCase @Inject constructor(private val musicRepository: MusicRepository) {
    operator fun invoke(song: Song) = musicRepository.addOrRemoveFavoriteSong(song)
}