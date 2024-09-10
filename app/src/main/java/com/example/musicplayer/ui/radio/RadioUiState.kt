package com.example.musicplayer.ui.radio

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.RadioCountry
import com.example.musicplayer.domain.model.RadioLanguage
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song

data class RadioUiState(
    val loading: Boolean? = false,
    val allStations: List<RadioStation>? = emptyList(),
    val currentStationsList: List<RadioStation>? = emptyList(),
    val favoriteStations: List<RadioStation>? = emptyList(),
    val popularStations: List<RadioStation>? = emptyList(),
    val topRatedStations: List<RadioStation>? = emptyList(),
    val recentlyChangedStations: List<RadioStation>? = emptyList(),
    val countryList: List<RadioCountry>? = emptyList(),
    val languageList: List<RadioLanguage>? = emptyList(),
    val errorMessage: String? = null,
    val selectedRadioStation: RadioStation? = null
)
