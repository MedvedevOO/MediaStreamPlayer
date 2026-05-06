package com.bearzwayne.musicplayer.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.RadioUiState


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
        items(
            count = radioStations.size,
            itemContent = { index ->
                val station = radioStations[index]
                RadioListItem(
                    radioUiState = radioUiState,
                    currentSong = currentSong,
                    playerState = playerState,
                    radioStation = station,
                    onItemClick = { onItemClick(station) },
                    onLikeClick = { onLikeClick(station) }
                )

            }
        )
    }
}