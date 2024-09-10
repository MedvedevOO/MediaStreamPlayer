package com.example.musicplayer.ui.radio

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song

sealed class RadioEvent {
    data object FetchAllRadioStations : RadioEvent()
    data object FetchFavoriteStations : RadioEvent()
    data object FetchPopularStations : RadioEvent()
    data object FetchTopRatedStations : RadioEvent()
    data object FetchRecentlyChangedStations : RadioEvent()
    data object FetchCountryList: RadioEvent()
    data object FetchLanguageList: RadioEvent()
    data class OnRadioLikeClick(val radioStation: RadioStation) : RadioEvent()
}