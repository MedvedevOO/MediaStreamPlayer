package com.bearzwayne.musicplayer.ui.home


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.FetchDataUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlayerStateUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetSongsUseCase
import com.bearzwayne.musicplayer.domain.usecase.PauseSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.PlaySongUseCase
import com.bearzwayne.musicplayer.domain.usecase.ResumeSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.SetPlaylistUseCase
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val addOrRemoveFavoriteSongUseCase: AddOrRemoveFavoriteSongUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val fetchDataUseCase: FetchDataUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val setPlaylistUseCase: SetPlaylistUseCase,
    private val getCurrentPlaylistUseCase: GetCurrentPlaylistUseCase,
    private val getCurrentSongUseCase: GetCurrentSongUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val getPlayerStateUseCase: GetPlayerStateUseCase
) : ViewModel() {

    // StateFlow to represent the UI state
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        fetchData()
        observeSongs()
        observePlaylists()
        observeSelectedPlaylist()
        observeCurrentSong()
        observePlayerState()
    }

    // Functions corresponding to actions (no event class)
    fun playSong() {
        _homeUiState.value.selectedPlaylist?.songList?.indexOf(_homeUiState.value.selectedSong)?.let { song ->
            playSongUseCase(song)
        }
    }

    fun pauseSong() {
        pauseSongUseCase()
    }

    fun resumeSong() {
        resumeSongUseCase()
    }

    fun fetchData() {
        viewModelScope.launch {
            fetchDataUseCase()
        }
    }

    fun selectSong(song: Song) {
        _homeUiState.value = _homeUiState.value.copy(selectedSong = song)
    }

    fun likeSong(song: Song) {
        viewModelScope.launch {
            addOrRemoveFavoriteSongUseCase(song)
        }
    }

    fun changePlaylist(newPlaylist: Playlist) {
        viewModelScope.launch {
            setPlaylistUseCase(newPlaylist)
            playSongUseCase(0)
        }
    }


    private fun observePlayerState() {
        viewModelScope.launch {
            getPlayerStateUseCase().collect { playerState ->
                _homeUiState.value = _homeUiState.value.copy(
                    loading = false,
                    playerState = playerState
                )
            }
        }
    }

    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase().collect { resource ->
                _homeUiState.value = when (resource) {
                    is Resource.Success -> _homeUiState.value.copy(
                        loading = false,
                        songs = resource.data
                    )
                    is Resource.Loading -> _homeUiState.value.copy(loading = true)
                    is Resource.Error -> _homeUiState.value.copy(
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
                _homeUiState.value = when (resource) {
                    is Resource.Success -> {
                        if (_homeUiState.value.selectedPlaylist?.id == -1) {
                            resource.data?.let { playlists ->
                                setPlaylistUseCase(playlists[0])
                            }
                        }
                        _homeUiState.value.copy(
                            loading = false,
                            playlists = resource.data
                        )
                    }
                    is Resource.Loading -> _homeUiState.value.copy(loading = true)
                    is Resource.Error -> _homeUiState.value.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observeSelectedPlaylist() {
        viewModelScope.launch {
            getCurrentPlaylistUseCase().collect { resource ->
                _homeUiState.value = when (resource) {
                    is Resource.Success -> _homeUiState.value.copy(
                        loading = false,
                        selectedPlaylist = resource.data
                    )
                    is Resource.Loading -> _homeUiState.value.copy(loading = true)
                    is Resource.Error -> _homeUiState.value.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observeCurrentSong() {
        viewModelScope.launch {
            getCurrentSongUseCase().collect { resource ->
                _homeUiState.value = when (resource) {
                    is Resource.Success -> _homeUiState.value.copy(
                        loading = false,
                        selectedSong = resource.data
                    )
                    is Resource.Loading -> _homeUiState.value.copy(loading = true)
                    is Resource.Error -> _homeUiState.value.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }
}