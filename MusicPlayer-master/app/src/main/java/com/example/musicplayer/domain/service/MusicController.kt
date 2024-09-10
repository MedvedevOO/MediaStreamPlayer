package com.example.musicplayer.domain.service

import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState

interface MusicController {
    var mediaControllerCallback: (
        (
        playerState: PlayerState,
        previousMusic: Song?,
        currentMusic: Song?,
        nextMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit
    )?

    fun addMediaItems(songs: List<Song>)

    fun play(mediaItemIndex: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun skipToNextSong()

    fun skipToPreviousSong()

    fun getCurrentSong(): Song?

    fun seekTo(position: Long)

    fun addSongNextToCurrentPosition(song: Song)

    fun addSongsNextToCurrentPosition(songList: List<Song>)

    fun addSongsToQueue(songList: List<Song>)

    fun playRadioStation(radioStation: RadioStation)

}