package com.bearzwayne.musicplayer.ui.search


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlayerStateUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.bearzwayne.musicplayer.domain.usecase.PlaySongUseCase
import com.bearzwayne.musicplayer.domain.usecase.SetPlaylistUseCase
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val addOrRemoveFavoriteSongUseCase: AddOrRemoveFavoriteSongUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val getPlayerStateUseCase: GetPlayerStateUseCase,
    private val getCurrentPlaylistUseCase: GetCurrentPlaylistUseCase,
    private val getCurrentSongUseCase: GetCurrentSongUseCase,
    private val setPlaylistUseCase: SetPlaylistUseCase,
    private val playSongUseCase: PlaySongUseCase,
) : ViewModel() {
    var searchScreenUiState by mutableStateOf(SearchScreenUiState())
        private set

    init {
        observePlaylists()
        observeSelectedPlaylist()
        observeCurrentSong()
        observePlayerState()
    }

    fun onEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.PlaySong -> playSong(event.song)
            is SearchScreenEvent.OnSongLikeClick -> addOrRemoveFavoriteSongUseCase(event.song)
        }
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase.invoke().collect { resource ->
                searchScreenUiState = when (resource) {
                    is Resource.Success -> {
                        searchScreenUiState.copy(
                            loading = false,
                            allSongsPlaylist = resource.data?.first {
                                it.name == DataProvider.getString(
                                    R.string.all_tracks
                                )
                            },
                            favoritesPlaylist = resource.data?.first { it.name == DataProvider.getString(R.string.favorites) }
                        )
                    }

                    is Resource.Loading -> searchScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> searchScreenUiState.copy(
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
                searchScreenUiState = when (resource) {
                    is Resource.Success -> searchScreenUiState.copy(
                        loading = false,
                        selectedPlaylist = resource.data
                    )

                    is Resource.Loading -> searchScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> searchScreenUiState.copy(
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
                searchScreenUiState = when (resource) {
                    is Resource.Success -> searchScreenUiState.copy(
                        loading = false,
                        selectedSong = resource.data
                    )

                    is Resource.Loading -> searchScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> searchScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            getPlayerStateUseCase().collect { playerState ->
                searchScreenUiState = searchScreenUiState.copy(
                    loading = false,
                    playerState = playerState
                )
            }
        }
    }

    private fun playSong(song: Song) {
        if (!(searchScreenUiState.selectedPlaylist?.songList ?: emptyList()).contains(song)) {
            setDefaultPlaylistAndPlay(song)
        } else {
            searchScreenUiState.apply {
                selectedPlaylist?.songList?.indexOf(song)?.let { songIndex ->
                    playSongUseCase(songIndex)
                }
            }
        }

    }

    private fun setDefaultPlaylistAndPlay(song: Song) {
        viewModelScope.launch {
            setPlaylistUseCase(searchScreenUiState.allSongsPlaylist!!)
            searchScreenUiState.allSongsPlaylist?.songList?.indexOf(song)?.let { song ->
                playSongUseCase(song)
            }
        }
    }
}
