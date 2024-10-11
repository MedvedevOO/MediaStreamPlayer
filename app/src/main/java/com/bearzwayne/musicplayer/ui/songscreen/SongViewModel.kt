package com.bearzwayne.musicplayer.ui.songscreen

import androidx.lifecycle.ViewModel
import com.bearzwayne.musicplayer.domain.usecase.PauseSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.ResumeSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.SeekSongToPositionUseCase
import com.bearzwayne.musicplayer.domain.usecase.SkipToNextSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.SkipToPreviousSongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val skipToNextSongUseCase: SkipToNextSongUseCase,
    private val skipToPreviousSongUseCase: SkipToPreviousSongUseCase,
    private val seekSongToPositionUseCase: SeekSongToPositionUseCase
) : ViewModel() {

    fun pauseSong() {
        pauseSongUseCase()
    }

    fun resumeSong() {
        resumeSongUseCase()
    }

    fun seekSongToPosition(position: Long) {
        seekSongToPositionUseCase(position)
    }

    fun skipToNextSong() {
        skipToNextSongUseCase()
    }

    fun skipToPreviousSong() {
        skipToPreviousSongUseCase()
    }

    fun seekToStartOfSong() {
        seekSongToPositionUseCase(0)
    }
 }


