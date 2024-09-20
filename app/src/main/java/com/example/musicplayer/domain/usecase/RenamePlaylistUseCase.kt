package com.example.musicplayer.domain.usecase

import com.example.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class RenamePlaylistUseCase @Inject constructor(private val musicRepository: MusicRepository) {
    operator fun invoke(id: Int, name: String) = musicRepository.renamePlaylist(id, name)
}