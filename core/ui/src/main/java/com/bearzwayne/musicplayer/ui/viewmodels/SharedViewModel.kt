package com.bearzwayne.musicplayer.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.ui.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.usecase.AddNewPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.AddSongNextToCurrentUseCase
import com.bearzwayne.musicplayer.domain.usecase.AddSongsToQueueUseCase
import com.bearzwayne.musicplayer.domain.usecase.CloseMusicRepositoryUseCase
import com.bearzwayne.musicplayer.domain.usecase.DestroyMediaControllerUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentSongPositionUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetSongsUseCase
import com.bearzwayne.musicplayer.domain.usecase.ReconnectMusicControllerUseCase
import com.bearzwayne.musicplayer.domain.usecase.SetMediaControllerCallbackUseCase
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val setMediaControllerCallbackUseCase: SetMediaControllerCallbackUseCase,
    private val getCurrentMusicPositionUseCase: GetCurrentSongPositionUseCase,
    private val destroyMediaControllerUseCase: DestroyMediaControllerUseCase,
    private val reconnectMusicControllerUseCase: ReconnectMusicControllerUseCase,
    private val closeMusicRepositoryUseCase: CloseMusicRepositoryUseCase,
    private val addNewPlaylistUseCase: AddNewPlaylistUseCase,
    private val addSongNextToCurrentUseCase: AddSongNextToCurrentUseCase,
    private val addSongsToQueueUseCase: AddSongsToQueueUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
) : ViewModel() {
    private var positionJob: Job? = null
    private val _userMessage = Channel<String>(Channel.BUFFERED)
    val userMessage = _userMessage.receiveAsFlow()

    var musicControllerUiState by mutableStateOf(MusicControllerUiState())
        private set

    init {
        if (musicControllerUiState.playerState == null) {
            setMediaControllerCallback()
        }
        observeSongs()
        observePlaylists()
    }

    fun onEvent(event: SharedViewModelEvent) {
        when (event) {
            is SharedViewModelEvent.AddNewPlaylist -> addNewPlaylistUseCase(event.newPlaylist)
            is SharedViewModelEvent.AddSongToNewPlaylist -> addSongToNewPlaylist(
                event.newPlaylist,
                event.newSong
            )

            is SharedViewModelEvent.AddSongListToQueue -> addSongsToQueueUseCase(event.songList)
            is SharedViewModelEvent.AddSongNextToCurrentSong -> addSongNextToCurrentUseCase(event.song)

        }
    }

    override fun onCleared() {
        super.onCleared()
        destroyMediaController()
        closeMusicRepositoryUseCase()
    }

    private fun setMediaControllerCallback() {
        reconnectMusicControllerUseCase()
        setMediaControllerCallbackUseCase { playerState, currentPlaylist, previousSong, currentSong, nextSong, currentPosition, totalDuration,
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
                startPositionUpdates()
            }
        }
    }

    fun destroyMediaController() {
        destroyMediaControllerUseCase()
    }

    fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = viewModelScope.launch {
            while (isActive) {
                delay(3.seconds)
                musicControllerUiState = musicControllerUiState.copy(
                    currentPosition = getCurrentMusicPositionUseCase()
                )
            }
        }
    }

    fun stopPositionUpdates() { positionJob?.cancel() }

    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase().collect { resource ->
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
            getPlaylistsUseCase().collect { resource ->
                musicControllerUiState = when (resource) {
                    is Resource.Success -> {
                        musicControllerUiState.copy(
                            loading = false,
                            playlists = resource.data ?: emptyList()
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

    private fun addSongToNewPlaylist(newPlaylist: Playlist, newSong: Song) {
        val newSongList = newPlaylist.songList.toMutableList()
            .apply { add(newSong) }
        val resultList = newPlaylist.copy(
            songList = newSongList,
            artWork = newSong.imageUrl
        )
        addNewPlaylistUseCase(resultList)
        val toastText = DataProvider.getString(R.string.track_added_to_playlist, newPlaylist.name)
        viewModelScope.launch { _userMessage.send(toastText) }
    }
}