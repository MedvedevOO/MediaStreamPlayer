package com.bearzwayne.musicplayer.domain.service

import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.other.Resource
import kotlinx.coroutines.flow.Flow

interface MusicController {
    var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentPlaylist: Playlist?,
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

    fun setPlaylist(playlist: Playlist)

    fun play(mediaItemIndex: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun skipToNextSong()

    fun skipToPreviousSong()

    fun getCurrentSong(): Flow<Resource<Song?>>

    fun getCurrentPlaylist(): Flow<Resource<Playlist>>

    fun getPlayerState(): Flow<PlayerState>

    fun seekTo(position: Long)

    fun addSongNextToCurrentPosition(song: Song)

    fun addSongsNextToCurrentPosition(songList: List<Song>)

    fun addSongsToQueue(songList: List<Song>)

    fun playRadioStation(radioStation: RadioStation)

}