package com.bearzwayne.musicplayer.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.usecase.AddNewPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.AddSongNextToCurrentUseCase
import com.bearzwayne.musicplayer.domain.usecase.AddSongsToQueueUseCase
import com.bearzwayne.musicplayer.domain.usecase.DestroyMediaControllerUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentSongPositionUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetSongsUseCase
import com.bearzwayne.musicplayer.domain.usecase.SetMediaControllerCallbackUseCase
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val setMediaControllerCallbackUseCase: SetMediaControllerCallbackUseCase,
    private val getCurrentMusicPositionUseCase: GetCurrentSongPositionUseCase,
    private val destroyMediaControllerUseCase: DestroyMediaControllerUseCase,
    private val addNewPlaylistUseCase: AddNewPlaylistUseCase,
    private val addSongNextToCurrentUseCase: AddSongNextToCurrentUseCase,
    private val addSongsToQueueUseCase: AddSongsToQueueUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
) : ViewModel() {

    // Holds the UI state of the music player controller
    private val _musicControllerUiState = MutableStateFlow(MusicControllerUiState())
    val musicControllerUiState: StateFlow<MusicControllerUiState> = _musicControllerUiState.asStateFlow()

    init {
        initializeMusicController()
        observeSongs()
        observePlaylists()
    }
    private fun initializeMusicController() {
        if (_musicControllerUiState.value.playerState == null) {
            setMediaControllerCallback()
        }
    }

    // Set media controller callback and observe media state changes
    private fun setMediaControllerCallback() {
        setMediaControllerCallbackUseCase { playerState, currentPlaylist, previousSong, currentSong, nextSong, currentPosition, totalDuration,
                                            isShuffleEnabled, isRepeatOneEnabled ->

            _musicControllerUiState.value = _musicControllerUiState.value.copy(
                playerState = playerState,
                selectedPlaylist = currentPlaylist,
                previousSong = previousSong,
                currentSong = currentSong,
                nextSong = nextSong,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                isShuffleEnabled = isShuffleEnabled,
                isRepeatOneEnabled = isRepeatOneEnabled
            )

            if (playerState == PlayerState.PLAYING) {
                viewModelScope.launch {
                    while (true) {
                        delay(3.seconds)
                        _musicControllerUiState.value = _musicControllerUiState.value.copy(
                            currentPosition = getCurrentMusicPositionUseCase()
                        )
                    }
                }
            }
        }
    }

    // Observe changes in songs and update the UI state
    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _musicControllerUiState.value = _musicControllerUiState.value.copy(
                            loading = false,
                            songs = resource.data
                        )
                    }
                    is Resource.Loading -> {
                        _musicControllerUiState.value = _musicControllerUiState.value.copy(
                            loading = true
                        )
                    }
                    is Resource.Error -> {
                        _musicControllerUiState.value = _musicControllerUiState.value.copy(
                            loading = false,
                            errorMessage = resource.message
                        )
                    }
                }
            }
        }
    }

    // Observe changes in playlists and update the UI state
    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _musicControllerUiState.value = _musicControllerUiState.value.copy(
                            loading = false,
                            playlists = resource.data ?: emptyList()
                        )
                    }
                    is Resource.Loading -> {
                        _musicControllerUiState.value = _musicControllerUiState.value.copy(
                            loading = true
                        )
                    }
                    is Resource.Error -> {
                        _musicControllerUiState.value = _musicControllerUiState.value.copy(
                            loading = false,
                            errorMessage = resource.message
                        )
                    }
                }
            }
        }
    }

    // Add a new playlist
    fun addNewPlaylist(newPlaylist: Playlist) {
        addNewPlaylistUseCase(newPlaylist)
    }

    // Add songs to the queue
    fun addSongsToQueue(songList: List<Song>) {
        addSongsToQueueUseCase(songList)
    }

    // Add a song next to the current song in the queue
    fun addSongNextToCurrent(song: Song) {
        addSongNextToCurrentUseCase(song)
    }

    // Add a song to a new playlist and show a confirmation toast
    private fun addSongToNewPlaylist(newPlaylist: Playlist, newSong: Song, context: Context) {
        val newSongList = newPlaylist.songList.toMutableList().apply { add(newSong) }
        val updatedPlaylist = newPlaylist.copy(songList = newSongList, artWork = newSong.imageUrl)
        addNewPlaylistUseCase(updatedPlaylist)
        val toastText = context.getString(R.string.track_added_to_playlist, newPlaylist.name)
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
    }

    // Destroy the media controller when the activity or fragment is stopped
    fun destroyMediaController() {
        destroyMediaControllerUseCase()
    }
}
