package com.bearzwayne.musicplayer.ui.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.data.mapper.toSong
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetFavoriteStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlayerStateUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetRecentlyChangedRadioStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetTopRadioStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetTopRatedRadioStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.PlaySongUseCase
import com.bearzwayne.musicplayer.domain.usecase.SetPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.UpdateFavoriteStationsUseCase
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val getCurrentPlaylistUseCase: GetCurrentPlaylistUseCase,
    private val getTopRadioStationsUseCase: GetTopRadioStationsUseCase,
    private val getTopRatedRadioStationsUseCase: GetTopRatedRadioStationsUseCase,
    private val getRecentlyChangedRadioStationsUseCase: GetRecentlyChangedRadioStationsUseCase,
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
    private val updateFavoriteStationsUseCase: UpdateFavoriteStationsUseCase,
    private val setPlaylistUseCase: SetPlaylistUseCase,
    private val playSongUseCase: PlaySongUseCase
) : ViewModel() {

    private val _radioUiState = MutableStateFlow(RadioUiState())
    val radioUiState: StateFlow<RadioUiState> = _radioUiState

    init {
        loadFavoriteStations()
        observeSelectedPlaylist()
    }

    fun fetchAllStations() {
        _radioUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val stations = getTopRadioStationsUseCase(10000)
            _radioUiState.update { state ->
                state.copy(
                    loading = false,
                    currentStationsList = stations,
                    popularStations = stations
                )
            }
        }
    }

    fun loadFavoriteStations() {
        _radioUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val stations = getFavoriteStationsUseCase()
            _radioUiState.update { state ->
                state.copy(
                    loading = false,
                    currentStationsList = stations,
                    favoriteStations = stations
                )
            }
        }
    }

    // Fetch popular stations
    fun fetchPopularStations(count: Int) {
        _radioUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val stations = getTopRadioStationsUseCase(count)
            _radioUiState.update { state ->
                state.copy(
                    loading = false,
                    currentStationsList = stations,
                    popularStations = stations
                )
            }
        }
    }

    // Fetch top-rated radio stations
    fun fetchTopRatedStations(count: Int) {
        _radioUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val stations = getTopRatedRadioStationsUseCase(count)
            _radioUiState.update { state ->
                state.copy(
                    loading = false,
                    currentStationsList = stations,
                    topRatedStations = stations
                )
            }
        }
    }

    // Fetch recently changed stations
    fun fetchRecentlyChangedStations(count: Int) {
        _radioUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val stations = getRecentlyChangedRadioStationsUseCase(count)
            _radioUiState.update { state ->
                state.copy(
                    loading = false,
                    currentStationsList = stations,
                    recentlyChangedStations = stations
                )
            }
        }
    }

    // Add or remove from favorites
    fun toggleFavoriteStation(radioStation: RadioStation) {
        val updatedFavorites = radioUiState.value.favoriteStations?.toMutableList() ?: mutableListOf()
        if (updatedFavorites.contains(radioStation)) {
            updatedFavorites.remove(radioStation)
        } else {
            updatedFavorites.add(radioStation)
        }

        _radioUiState.update { state ->
            state.copy(favoriteStations = updatedFavorites)
        }

        // Update favorites in data source
        viewModelScope.launch {
            updateFavoriteStationsUseCase(updatedFavorites)
        }
    }

    // Observe playlist changes
    private fun observeSelectedPlaylist() {
        viewModelScope.launch {
            getCurrentPlaylistUseCase().collect { resource ->
                _radioUiState.update { state ->
                    when (resource) {
                        is Resource.Success -> state.copy(
                            loading = false,
                            selectedPlaylist = resource.data
                        )
                        is Resource.Loading -> state.copy(loading = true)
                        is Resource.Error -> state.copy(
                            loading = false,
                            errorMessage = resource.message
                        )
                    }
                }
            }
        }
    }
    // Play radio station
    fun playRadio(radioStation: RadioStation, radioList: List<RadioStation>?) {
        val songList = radioList?.map { it.toSong() } ?: emptyList()
        if (!(radioUiState.value.selectedPlaylist?.songList ?: emptyList()).contains(radioStation.toSong())) {
            setPlaylistAndPlay(radioStation.toSong(), songList)
        } else {
            radioUiState.value.selectedPlaylist?.songList?.indexOf(radioStation.toSong())?.let { songIndex ->
                playSongUseCase(songIndex)
            }
        }
    }

    // Set playlist and play
    private fun setPlaylistAndPlay(song: Song, songList: List<Song>) {
        if (songList.isNotEmpty()) {
            viewModelScope.launch {
                setPlaylistUseCase(Playlist(666, "Radio", songList, DataProvider.getDefaultCover().toString()))
                playSongUseCase(songList.indexOf(song))
            }
        }
    }
}
