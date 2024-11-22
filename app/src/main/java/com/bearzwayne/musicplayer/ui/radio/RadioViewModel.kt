package com.bearzwayne.musicplayer.ui.radio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.data.mapper.toSong
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetFavoriteStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetRecentlyChangedRadioStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetTopRadioStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetTopRatedRadioStationsUseCase
import com.bearzwayne.musicplayer.domain.usecase.PlaySongUseCase
import com.bearzwayne.musicplayer.domain.usecase.SetPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.UpdateFavoriteStationsUseCase
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var radioUiState by mutableStateOf(RadioUiState())
        private set

    init {
        getPopularStations(200)
        observeSelectedPlaylist()

    }


    fun onEvent(event: RadioEvent) {
        when (event) {
            RadioEvent.FetchAllRadioStations -> getAllStations()
            RadioEvent.FetchFavoriteStations -> getFavoriteStations()

            RadioEvent.FetchPopularStations -> getPopularStations(200)

            RadioEvent.FetchTopRatedStations -> getTopRatedRadioStations(200)

            RadioEvent.FetchRecentlyChangedStations -> getRecentlyChangedRadioStations(200)

            is RadioEvent.OnRadioLikeClick -> addOrRemoveFromFavorites(event.radioStation)
            is RadioEvent.PlayRadio -> playRadio(event.radioStation, event.radioList)
        }
    }

    private fun observeSelectedPlaylist() {
        viewModelScope.launch {
            getCurrentPlaylistUseCase().collect { resource ->
                radioUiState = when (resource) {
                    is Resource.Success -> radioUiState.copy(
                        loading = false,
                        selectedPlaylist = resource.data
                    )

                    is Resource.Loading -> radioUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> radioUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )


                }
            }
        }
    }

    private fun getAllStations() {
        radioUiState = radioUiState.copy(loading = true)
        if (radioUiState.allStations.isNullOrEmpty()) {
            viewModelScope.launch {
                val stations = getTopRadioStationsUseCase(10000)
                radioUiState = radioUiState.copy(
                    loading = false,
                    currentStationsList = stations,
                    popularStations = stations
                )
            }
        } else {
            radioUiState = radioUiState.copy(
                loading = false,
                currentStationsList = radioUiState.popularStations
            )
        }

    }

    private fun getFavoriteStations() {
        radioUiState = radioUiState.copy(loading = true)
        if (radioUiState.favoriteStations.isNullOrEmpty()) {
            viewModelScope.launch {
                val stations = getFavoriteStationsUseCase()
                radioUiState = radioUiState.copy(
                    loading = false,
                    currentStationsList = stations,
                    favoriteStations = stations
                )
            }
        }
        else {
            radioUiState = radioUiState.copy(
                loading = false,
                currentStationsList = radioUiState.favoriteStations
            )
        }

    }

    private fun getPopularStations(count: Int) {
        radioUiState = radioUiState.copy(loading = true)
        if (radioUiState.popularStations.isNullOrEmpty()) {
            viewModelScope.launch {
                val stations = getTopRadioStationsUseCase(count)
                radioUiState = radioUiState.copy(
                    loading = false,
                    currentStationsList = stations,
                    popularStations = stations
                )
            }
        } else {
            radioUiState = radioUiState.copy(
                loading = false,
                currentStationsList = radioUiState.popularStations
            )
        }


    }

    private fun getTopRatedRadioStations(count: Int) {
        radioUiState = radioUiState.copy(loading = true)
        if (radioUiState.topRatedStations.isNullOrEmpty()) {
            viewModelScope.launch {
                val stations = getTopRatedRadioStationsUseCase(count)
                radioUiState = radioUiState.copy(
                    loading = false,
                    currentStationsList = stations,
                    topRatedStations = stations
                )
            }
        } else {
            radioUiState = radioUiState.copy(
                loading = false,
                currentStationsList = radioUiState.topRatedStations
            )
        }


    }

    private fun getRecentlyChangedRadioStations(count: Int) {
        radioUiState = radioUiState.copy(loading = true)
        if (radioUiState.recentlyChangedStations.isNullOrEmpty()) {
            viewModelScope.launch {
                val stations = getRecentlyChangedRadioStationsUseCase(count)
                radioUiState = radioUiState.copy(
                    loading = false,
                    currentStationsList = stations,
                    recentlyChangedStations = stations
                )
            }
        } else {
            radioUiState = radioUiState.copy(
                loading = false,
                currentStationsList = radioUiState.recentlyChangedStations
            )
        }


    }

    private fun addOrRemoveFromFavorites(radioStation: RadioStation) {
       val favorites = radioUiState.favoriteStations?.toMutableList()
        if (favorites!!.contains(radioStation)) {
            favorites.remove(radioStation)
        } else {
            favorites.add(radioStation)
        }
        radioUiState = radioUiState.copy(
            favoriteStations = favorites
        )

        viewModelScope.launch {
            updateFavoriteStationsUseCase(favorites)
        }
    }

    private fun playRadio(radioStation: RadioStation, radioList: List<RadioStation>?) {
        val songList = radioList?.map { radio ->
            radio.toSong()
        }

        if (!(radioUiState.selectedPlaylist?.songList ?: emptyList()).contains(radioStation.toSong())) {
            setPlaylistAndPlay(radioStation.toSong(), songList ?: emptyList())
        } else {
            radioUiState.apply {
                selectedPlaylist?.songList?.indexOf(radioStation.toSong())?.let { songIndex ->
                    playSongUseCase(songIndex)
                }
            }
        }

    }

    private fun setPlaylistAndPlay(song: Song, songList: List<Song>) {
        if (songList.isNotEmpty()) {
            viewModelScope.launch {
                setPlaylistUseCase(Playlist(666, "Radio", songList, DataProvider.getDefaultCover().toString()))
                playSongUseCase(songList.indexOf(song))
            }
        }
    }
}