package com.example.musicplayer.ui.radio

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.radio.components.RadioListItem
import com.example.musicplayer.ui.viewmodels.RadioViewModel

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