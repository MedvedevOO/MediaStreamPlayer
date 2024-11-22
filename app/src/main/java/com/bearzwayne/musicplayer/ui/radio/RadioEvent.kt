package com.bearzwayne.musicplayer.ui.radio

import com.bearzwayne.musicplayer.domain.model.RadioStation

sealed class RadioEvent {
    data object FetchAllRadioStations : RadioEvent()
    data object FetchFavoriteStations : RadioEvent()
    data object FetchPopularStations : RadioEvent()
    data object FetchTopRatedStations : RadioEvent()
    data object FetchRecentlyChangedStations : RadioEvent()
    data class PlayRadio(val radioStation: RadioStation, val radioList: List<RadioStation>?): RadioEvent()
    data class OnRadioLikeClick(val radioStation: RadioStation) : RadioEvent()
}