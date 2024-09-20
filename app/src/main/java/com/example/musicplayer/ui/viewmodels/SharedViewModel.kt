package com.example.musicplayer.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.usecase.AddNewPlaylistUseCase
import com.example.musicplayer.domain.usecase.AddSongNextToCurrentUseCase
import com.example.musicplayer.domain.usecase.AddSongsToQueueUseCase
import com.example.musicplayer.domain.usecase.DestroyMediaControllerUseCase
import com.example.musicplayer.domain.usecase.GetAlbumIdByNameUseCase
import com.example.musicplayer.domain.usecase.GetArtistIdByNameUseCase
import com.example.musicplayer.domain.usecase.GetCurrentSongPositionUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistByIdUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayer.domain.usecase.GetSongsUseCase
import com.example.musicplayer.domain.usecase.SetMediaControllerCallbackUseCase
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val getAlbumIdByNameUseCase: GetAlbumIdByNameUseCase,
    private val getArtistIdByNameUseCase: GetArtistIdByNameUseCase,
    private val getPlaylistByIdUseCase: GetPlaylistByIdUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,

) : ViewModel() {

    var musicControllerUiState by mutableStateOf(MusicControllerUiState())
        private set

    init {
        if (musicControllerUiState.playerState == null){
            setMediaControllerCallback()
        }
        observeSongs()
        observePlaylists()
    }

    fun onEvent(event: SharedViewModelEvent): Any? {
        when (event) {
            is SharedViewModelEvent.AddNewPlaylist -> addNewPlaylistUseCase.invoke(event.newPlaylist)
            is SharedViewModelEvent.AddSongListToQueue -> addSongsToQueueUseCase.invoke(event.songList)
            is SharedViewModelEvent.AddSongNextToCurrentSong -> addSongNextToCurrentUseCase.invoke(event.song)
            is SharedViewModelEvent.FindAlbumIdByName -> return getAlbumIdByNameUseCase.invoke(event.name)
            is SharedViewModelEvent.FindArtistIdByName -> return getArtistIdByNameUseCase.invoke(event.name)
            is SharedViewModelEvent.FindPlaylistById -> return getPlaylistByIdUseCase.invoke(event.id)
            SharedViewModelEvent.GetSongs -> return musicControllerUiState.songs

        }
        return null
    }

    private fun setMediaControllerCallback() {
        setMediaControllerCallbackUseCase { playerState, currentPlaylist,previousSong, currentSong, nextSong, currentPosition, totalDuration,
                                            isShuffleEnabled, isRepeatOneEnabled ->
            musicControllerUiState = musicControllerUiState.copy(
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
                        musicControllerUiState = musicControllerUiState.copy(
                            currentPosition = getCurrentMusicPositionUseCase()
                        )
                    }
                }
            }
        }
    }

    fun destroyMediaController() {
        destroyMediaControllerUseCase()
    }

    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase.invoke().collect { resource ->
                musicControllerUiState = when (resource) {
                    is Resource.Success -> musicControllerUiState.copy(
                        loading = false,
                        songs = resource.data
                    )

                    is Resource.Loading -> musicControllerUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> musicControllerUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase.invoke().collect { resource ->
                musicControllerUiState = when (resource) {
                    is Resource.Success -> {
                        musicControllerUiState.copy(
                            loading = false,
                            playlists = resource.data
                        )
                    }

                    is Resource.Loading -> musicControllerUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> musicControllerUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }
}