package com.bearzwayne.musicplayer.ui.search


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.data.DataProvider
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // Exposing the UI state via StateFlow
    private val _searchScreenUiState = MutableStateFlow(SearchScreenUiState())
    val searchScreenUiState: StateFlow<SearchScreenUiState> = _searchScreenUiState
    private var searchQuery: String = ""
    init {
        observePlaylists()
        observeSelectedPlaylist()
        observeCurrentSong()
        observePlayerState()
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        filterSongs()
    }

    private fun filterSongs() {
        val allSongsList = _searchScreenUiState.value.allSongsPlaylist?.songList ?: emptyList()
        val filteredSongs = if (searchQuery.isBlank()) allSongsList else {
            allSongsList.filter { song ->
                song.title.contains(searchQuery, ignoreCase = true) ||
                        song.artist.contains(searchQuery, ignoreCase = true)
            }
        }
        _searchScreenUiState.value = _searchScreenUiState.value.copy(
            filteredSongs = filteredSongs
        )
    }
    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _searchScreenUiState.value = _searchScreenUiState.value.copy(
                            loading = false,
                            allSongsPlaylist = resource.data?.firstOrNull { it.name == DataProvider.getAllTracksName() },
                            favoritesPlaylist = resource.data?.firstOrNull { it.name == DataProvider.getFavoritesName() }
                        )
                    }
                    is Resource.Loading -> _searchScreenUiState.value = _searchScreenUiState.value.copy(
                        loading = true
                    )
                    is Resource.Error -> _searchScreenUiState.value = _searchScreenUiState.value.copy(
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
                when (resource) {
                    is Resource.Success -> {
                        _searchScreenUiState.value = _searchScreenUiState.value.copy(
                            loading = false,
                            selectedPlaylist = resource.data
                        )
                    }
                    is Resource.Loading -> _searchScreenUiState.value = _searchScreenUiState.value.copy(
                        loading = true
                    )
                    is Resource.Error -> _searchScreenUiState.value = _searchScreenUiState.value.copy(
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
                when (resource) {
                    is Resource.Success -> {
                        _searchScreenUiState.value = _searchScreenUiState.value.copy(
                            loading = false,
                            selectedSong = resource.data
                        )
                    }
                    is Resource.Loading -> _searchScreenUiState.value = _searchScreenUiState.value.copy(
                        loading = true
                    )
                    is Resource.Error -> _searchScreenUiState.value = _searchScreenUiState.value.copy(
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
                _searchScreenUiState.value = _searchScreenUiState.value.copy(
                    playerState = playerState
                )
            }
        }
    }

    fun playSong(song: Song) {
        val playlist = _searchScreenUiState.value.selectedPlaylist
        if (playlist?.songList?.contains(song) == true) {
            playlist.songList.indexOf(song).let { index ->
                playSongUseCase(index)
            }
        } else {
            setDefaultPlaylistAndPlay(song)
        }
    }

    private fun setDefaultPlaylistAndPlay(song: Song) {
        viewModelScope.launch {
            val allSongsPlaylist = _searchScreenUiState.value.allSongsPlaylist ?: return@launch
            setPlaylistUseCase(allSongsPlaylist)
            allSongsPlaylist.songList.indexOf(song).let { index ->
                playSongUseCase(index)
            }
        }
    }

    fun addOrRemoveFromFavorites(song: Song) = addOrRemoveFavoriteSongUseCase(song)
}
