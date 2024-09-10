package com.example.musicplayer.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.usecase.GetCountryListUseCase
import com.example.musicplayer.domain.usecase.GetFavoriteStationsUseCase
import com.example.musicplayer.domain.usecase.GetLanguageListUseCase
import com.example.musicplayer.domain.usecase.GetRecentlyChangedRadioStationsUseCase
import com.example.musicplayer.domain.usecase.GetTopRadioStationsUseCase
import com.example.musicplayer.domain.usecase.GetTopRatedRadioStationsUseCase
import com.example.musicplayer.domain.usecase.UpdateFavoriteStationsUseCase
import com.example.musicplayer.ui.radio.RadioEvent
import com.example.musicplayer.ui.radio.RadioUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val getTopRadioStationsUseCase: GetTopRadioStationsUseCase,
    private val getTopRatedRadioStationsUseCase: GetTopRatedRadioStationsUseCase,
    private val getRecentlyChangedRadioStationsUseCase: GetRecentlyChangedRadioStationsUseCase,
    private val getCountryListUseCase: GetCountryListUseCase,
    private val getLanguageListUseCase: GetLanguageListUseCase,
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
    private val updateFavoriteStationsUseCase: UpdateFavoriteStationsUseCase
) : ViewModel() {

    var radioUiState by mutableStateOf(RadioUiState())
        private set

    init {
        getCountryList()
        getLanguageList()
        getFavoriteStations()
    }


    fun onEvent(event: RadioEvent) {
        when (event) {
            RadioEvent.FetchAllRadioStations -> getAllStations()
            RadioEvent.FetchFavoriteStations -> getFavoriteStations()

            RadioEvent.FetchPopularStations -> getPopularStations(200)

            RadioEvent.FetchTopRatedStations -> getTopRatedRadioStations(200)

            RadioEvent.FetchRecentlyChangedStations -> getRecentlyChangedRadioStations(200)

            RadioEvent.FetchCountryList -> getCountryList()

            RadioEvent.FetchLanguageList -> getLanguageList()

            is RadioEvent.OnRadioLikeClick -> addOrRemoveFromFavorites(event.radioStation)



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
                val stations = getFavoriteStationsUseCase.invoke()
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
                val stations = getTopRatedRadioStationsUseCase.invoke(count)
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
                val stations = getRecentlyChangedRadioStationsUseCase.invoke(count)
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

    private fun getCountryList() {
        radioUiState = radioUiState.copy(loading = true)
        viewModelScope.launch {
            val countries = getCountryListUseCase.invoke()
            radioUiState = radioUiState.copy(
                loading = false,
                countryList = countries
            )
        }

    }


    private fun getLanguageList() {
        radioUiState = radioUiState.copy(loading = true)
        viewModelScope.launch {
            val languages = getLanguageListUseCase.invoke()
            radioUiState = radioUiState.copy(
                loading = false,
                languageList = languages
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
            updateFavoriteStationsUseCase.invoke(favorites)
        }
    }
}