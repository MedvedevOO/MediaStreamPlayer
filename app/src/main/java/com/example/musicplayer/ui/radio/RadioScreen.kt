package com.example.musicplayer.ui.radio

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.radio.components.RadioFilterItem
import com.example.musicplayer.ui.search.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RadioScreen(
    currentSong: Song?,
    scaffoldState: BottomSheetScaffoldState,
    playerState: PlayerState?
) {
    val radioViewModel: RadioViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        radioViewModel.onEvent(RadioEvent.FetchPopularStations)
    }
    val radioUiState = radioViewModel.radioUiState
    val onRadioEvent = radioViewModel::onEvent
    var searchText by remember { mutableStateOf("") }
    val stations = radioUiState.currentStationsList
    val filteredRadioStations = filterRadioStations(stations, searchText)
    val density = LocalDensity.current

    // Track the height of the content behind the sheet
    var contentHeight by remember { mutableStateOf(0.dp) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp // Get the screen height

    val offsetInPx = remember { mutableFloatStateOf(0f) }
    val offsetInDp = remember { mutableStateOf(0.dp) }
    val dynamicAlphaForTopPart = ((offsetInPx.floatValue) / 1000).coerceIn(0f, 1f)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = (screenHeight - contentHeight), // Ensure minimum peek height
        sheetContent = {
            with(radioUiState) {
                when {
                    loading == true -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(100.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.Center)
                                    .padding(
                                        top = 16.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )
                            )
                        }
                    }
                    else -> {
                        if (filteredRadioStations.isNotEmpty()) {
                            RadioStationList(
                                radioUiState = radioUiState,
                                radioStations = filteredRadioStations,
                                currentSong = currentSong,
                                playerState = playerState,
                                onItemClick = { radioStation ->
                                    onRadioEvent(RadioEvent.PlayRadio(radioStation, stations))
                                },
                                onLikeClick = {
                                    onRadioEvent(RadioEvent.OnRadioLikeClick(it))
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        },
        sheetDragHandle = {},
        sheetShape = RoundedCornerShape(5.dp),
        containerColor = Color.Transparent,
        sheetShadowElevation = 4.dp,
        sheetContainerColor = Color.Transparent
    ) {
        // Capture the height of the content behind the sheet
        LaunchedEffect(scaffoldState.bottomSheetState) {
            snapshotFlow { scaffoldState.bottomSheetState.requireOffset() }
                .collect { newOffsetInPx ->
                    offsetInPx.floatValue = newOffsetInPx
                    offsetInDp.value = with(density) { (newOffsetInPx.toDp() - 64.dp) }
                }
        }

        // Measure content height and adjust the sheet peek height dynamically
        Column(modifier = Modifier
            .onGloballyPositioned { coordinates ->
                contentHeight = with(density) {
                    coordinates.size.height.toDp() * 1.36f
                }
            }
            .graphicsLayer { alpha = dynamicAlphaForTopPart }
        ) {
            SearchBar(
                descriptionText = R.string.search_for_radiostation,
                onValueChange = { value -> searchText = value }
            )
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(top = 4.dp), content = {
                item(span = { GridItemSpan(2) }) {
                    RadioFilterItem(filterName = stringResource(R.string.all_radio_stations)) {
                        onRadioEvent(RadioEvent.FetchAllRadioStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(R.string.popular)) {
                        onRadioEvent(RadioEvent.FetchPopularStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(id = R.string.favorites)) {
                        onRadioEvent(RadioEvent.FetchFavoriteStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(R.string.top_rated)) {
                        onRadioEvent(RadioEvent.FetchTopRatedStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(R.string.recently_changed)) {
                        onRadioEvent(RadioEvent.FetchRecentlyChangedStations)
                    }
                }
            })
        }
    }
}


private fun filterRadioStations(radioStations: List<RadioStation>?, query: String): List<RadioStation> {
    return if (radioStations.isNullOrEmpty()) emptyList()
    else {
        radioStations.filter { station ->
            station.name.contains(query, ignoreCase = true) ||
                    station.country.contains(query, ignoreCase = true)
        }
    }

}