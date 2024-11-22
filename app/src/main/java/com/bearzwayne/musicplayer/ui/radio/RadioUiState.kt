package com.bearzwayne.musicplayer.ui.radio

import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.RadioStation

data class RadioUiState(
    val loading: Boolean? = false,
    val allStations: List<RadioStation>? = emptyList(),
    val currentStationsList: List<RadioStation>? = emptyList(),
    val favoriteStations: List<RadioStation>? = emptyList(),
    val popularStations: List<RadioStation>? = emptyList(),
    val topRatedStations: List<RadioStation>? = emptyList(),
    val recentlyChangedStations: List<RadioStation>? = emptyList(),
    val selectedPlaylist: Playlist? = null,
    val selectedRadioStation: RadioStation? = null,
    val errorMessage: String? = null
)
