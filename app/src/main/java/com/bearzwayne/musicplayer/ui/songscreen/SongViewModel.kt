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

    fun onEvent(event: SongEvent) {
        when (event) {
            SongEvent.PauseSong -> pauseSongUseCase()
            SongEvent.ResumeSong -> resumeSongUseCase()
            is SongEvent.SeekSongToPosition -> seekSongToPositionUseCase(event.position)
            SongEvent.SkipToNextSong -> skipToNextSongUseCase()
            SongEvent.SkipToPreviousSong -> skipToPreviousSongUseCase()
            SongEvent.SeekToStartOfSong -> seekSongToPositionUseCase(0)
        }
    }
 }

