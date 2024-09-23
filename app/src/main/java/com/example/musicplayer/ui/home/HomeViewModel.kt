package com.example.musicplayer.ui.home


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
import com.example.musicplayer.domain.usecase.FetchDataUseCase
import com.example.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.example.musicplayer.domain.usecase.GetCurrentSongUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayer.domain.usecase.GetSongsUseCase
import com.example.musicplayer.domain.usecase.PauseSongUseCase
import com.example.musicplayer.domain.usecase.PlaySongUseCase
import com.example.musicplayer.domain.usecase.ResumeSongUseCase
import com.example.musicplayer.domain.usecase.SetPlaylistUseCase
import com.example.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    init {
        observeSongs()
        observePlaylists()
        observeSelectedPlaylist()
        observeCurrentSong()
    }


    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.PlaySong -> playSong()

            HomeEvent.PauseSong ->  pauseSongUseCase()

            HomeEvent.ResumeSong -> resumeSongUseCase()

            HomeEvent.FetchData -> fetchData()

            is HomeEvent.OnSongSelected -> homeUiState =
                homeUiState.copy(selectedSong = event.selectedSong)

            is HomeEvent.OnSongLikeClick -> addOrRemoveFavoriteSongUseCase(event.song)

            is HomeEvent.OnPlaylistChange -> changePlaylist(event.newPlaylist)

        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            fetchDataUseCase()
        }
    }

    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase().collect { resource ->
                homeUiState = when (resource) {
                    is Resource.Success -> homeUiState.copy(
                        loading = false,
                        songs = resource.data
                    )

                    is Resource.Loading -> homeUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> homeUiState.copy(
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
                homeUiState = when (resource) {
                    is Resource.Success -> {
                        if (homeUiState.selectedPlaylist?.id == -1) {
                            resource.data?.let { playlists ->
                                setPlaylistUseCase(playlists[0])
                            }
                        }
                        homeUiState.copy(
                            loading = false,
                            playlists = resource.data
                        )
                    }

                    is Resource.Loading -> homeUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> homeUiState.copy(
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
                homeUiState = when (resource) {
                    is Resource.Success -> homeUiState.copy(
                        loading = false,
                        selectedPlaylist = resource.data
                    )

                    is Resource.Loading -> homeUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> homeUiState.copy(
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
                homeUiState = when (resource) {
                    is Resource.Success -> homeUiState.copy(
                        loading = false,
                        selectedSong = resource.data
                    )

                    is Resource.Loading -> homeUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> homeUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }


    private fun playSong() {
        homeUiState.apply {
            selectedPlaylist?.songList?.indexOf(selectedSong)?.let { song ->
                playSongUseCase(song)
            }
        }
    }


    private fun changePlaylist(newPlaylist: Playlist) {
        viewModelScope.launch {
            setPlaylistUseCase(newPlaylist)
            playSongUseCase(0)
        }
    }


}
