package com.example.musicplayer.ui.radio

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.radio.components.RadioListItem

@Composable
fun RadioStationList(
    radioUiState: RadioUiState,
    radioStations: List<RadioStation>,
    currentSong: Song?,
    playerState: PlayerState?,
    onItemClick: (station : RadioStation) -> Unit,
    onLikeClick: (station : RadioStation) -> Unit
) {

    LazyColumn {
        itemsIndexed(radioStations) { index, station ->
            RadioListItem(
                radioUiState = radioUiState,
                currentSong = currentSong,
                playerState = playerState,
                radioStation = station,
                onItemClick = {onItemClick(station)},
                onLikeClick = {onLikeClick(station)}
            ) {

            }
        }
    }
}