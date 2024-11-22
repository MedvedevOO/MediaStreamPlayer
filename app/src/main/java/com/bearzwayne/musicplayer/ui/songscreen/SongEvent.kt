package com.bearzwayne.musicplayer.ui.songscreen

sealed class SongEvent {
    data object PauseSong : SongEvent()
    data object ResumeSong : SongEvent()
    data object SkipToNextSong : SongEvent()
    data object SkipToPreviousSong : SongEvent()
    data object SeekToStartOfSong: SongEvent()
    data class SeekSongToPosition(val position: Long) : SongEvent()
}