package com.example.musicplayer.data.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.mapper.toMediaItem
import com.example.musicplayer.data.mapper.toSong
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.service.MusicController
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.other.Resource
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MusicControllerImpl(context: Context) : MusicController {
    private lateinit var mediaItems: List<MediaItem>
    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val selectedPlaylistFlow = MutableStateFlow<Resource<Playlist>>(Resource.Loading())
    private val selectedSongFlow = MutableStateFlow<Resource<Song?>>(Resource.Loading())
    private val playerStateFlow = MutableStateFlow(PlayerState.STOPPED)
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null
    private val pendingMediaItems = mutableListOf<MediaItem>()
    override var mediaControllerCallback: (
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
    )? = null

    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            val controller = mediaController
            if (controller != null) {
                onMediaControllerReady(controller)
            } else {
                Log.e("MusicController", "MediaController is null.")
            }
        }, MoreExecutors.directExecutor())
    }

    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                val currentSong = player.currentMediaItem?.toSong()
                val playerState = player.playbackState.toPlayerState(player.isPlaying)
                CoroutineScope(Dispatchers.IO).launch {
                    selectedSongFlow.emit(Resource.Loading())
                    selectedSongFlow.emit(Resource.Success(currentSong))
                    playerStateFlow.emit(playerState)
                }
                with(player) {
                    mediaControllerCallback?.invoke(
                        playbackState.toPlayerState(isPlaying),
                        selectedPlaylistFlow.value.data,
                        if (hasPreviousMediaItem()) {
                            getMediaItemAt(previousMediaItemIndex).toSong()
                        } else null,
                        currentMediaItem?.toSong(),
                        if (hasNextMediaItem()) {
                            getMediaItemAt(nextMediaItemIndex).toSong()
                        } else null,
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L),
                        shuffleModeEnabled,
                        repeatMode == Player.REPEAT_MODE_ONE
                    )
                }
            }
        })
    }

    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE -> PlayerState.STOPPED
            Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        }


        override fun addMediaItems(songs: List<Song>) {

        mediaItems = songs.map { song ->
            song.toMediaItem()
        }

        if (mediaController != null) {
            mediaController?.clearMediaItems()
            mediaController?.setMediaItems(mediaItems)
            mediaController?.prepare()
        } else {
            pendingMediaItems.addAll(mediaItems)
        }
    }

    override fun setPlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            selectedPlaylistFlow.emit(Resource.Success(playlist))
        }
        addMediaItems(playlist.songList)
    }


    private fun onMediaControllerReady(controller: MediaController) {
        controllerListener()
        if (pendingMediaItems.isNotEmpty()) {
            controller.clearMediaItems()
            controller.setMediaItems(pendingMediaItems)
            controller.prepare()
            pendingMediaItems.clear()

        }
    }

    override fun play(mediaItemIndex: Int) {
        mediaController?.apply {
            seekToDefaultPosition(mediaItemIndex)
            prepare()
            playWhenReady = true

        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L

    override fun getCurrentSong(): Flow<Resource<Song?>> = selectedSongFlow

    override fun getCurrentPlaylist(): Flow<Resource<Playlist>> = selectedPlaylistFlow

    override fun getPlayerState(): Flow<PlayerState> = playerStateFlow

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    private fun createMixPlaylist() {
        val songs = mediaItems.map { it.toSong() }
        val updatedPlaylist = Playlist(
            id = -2,
            name = DataProvider.getString(R.string.mix),
            songList = songs,
            artWork = ""
        )

        CoroutineScope(Dispatchers.IO).launch {
            selectedPlaylistFlow.emit(Resource.Loading())
            selectedPlaylistFlow.emit(Resource.Success(updatedPlaylist))
        }

    }
    override fun addSongNextToCurrentPosition(song: Song) {
        val newIndex = mediaController?.currentMediaItemIndex?.plus(1)
        val additionalMediaItem = song.toMediaItem()

        if (mediaItems.contains(additionalMediaItem)) {
            val songIndex = mediaItems.indexOf(additionalMediaItem)
            val newMediaItems = mediaItems.toMutableList().apply {
                remove(additionalMediaItem)
                add(newIndex!!, additionalMediaItem)
            }
            mediaItems = newMediaItems
            mediaController?.moveMediaItem(songIndex, newIndex!!)
        } else {
            val newMediaItems = mediaItems.toMutableList().apply {
                add(newIndex!!, additionalMediaItem)
            }
            mediaItems = newMediaItems

            mediaController?.addMediaItems(newIndex!!, listOf(additionalMediaItem))
        }
        createMixPlaylist()
    }

    override fun addSongsNextToCurrentPosition(songList: List<Song>) {
        val index = mediaController?.currentMediaItemIndex?.plus(1)
        val additionalMediaItems = songList.map { song ->
            song.toMediaItem()
        }.toMutableList()

        val iterator = additionalMediaItems.iterator()
        while (iterator.hasNext()) {
            val song = iterator.next()
            if (mediaItems.contains(song)) {
                iterator.remove()
            }
        }
        val newMediaItems = mediaItems.toMutableList().apply {
            addAll(index!!, additionalMediaItems)
        }
        mediaItems = newMediaItems
        mediaController?.addMediaItems(index!!, additionalMediaItems)
        createMixPlaylist()
    }

    override fun addSongsToQueue(songList: List<Song>) {
        val additionalMediaItems = songList.map { song ->
            song.toMediaItem()
        }.toMutableList()

        val iterator = additionalMediaItems.iterator()
        while (iterator.hasNext()) {
            val song = iterator.next()
            if (mediaItems.contains(song)) {
                iterator.remove() // Remove the current element from the list safely
            }
        }
        val newMediaItems = mediaItems.toMutableList().apply {
            addAll(additionalMediaItems)
        }
        mediaItems = newMediaItems
        mediaController?.addMediaItems(additionalMediaItems)
        createMixPlaylist()
    }

    override fun playRadioStation(radioStation: RadioStation) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(radioStation.url)
            .setUri(radioStation.url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(radioStation.name)
                    .setSubtitle(radioStation.country)
                    .setArtist(radioStation.country)
                    .setArtworkUri(Uri.parse(radioStation.favicon))
                    .build()
            )
            .build()

        if (mediaController != null) {
            mediaController?.clearMediaItems()
            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
            mediaController?.play()
        }
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }

    override fun skipToNextSong() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousSong() {
        mediaController?.seekToPrevious()
    }

}