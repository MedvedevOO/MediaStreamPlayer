package com.example.musicplayer.ui.radio

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys
import com.example.musicplayer.data.mapper.toSong
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.RadioStation
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState
import com.example.musicplayer.ui.radio.components.RadioFilterItem
import com.example.musicplayer.ui.search.components.SearchBar
import com.example.musicplayer.ui.settings.AppSettingsSheet
import com.example.musicplayer.ui.sharedresources.TopPageBar
import com.example.musicplayer.ui.sharedresources.song.SongListScrollable
import com.example.musicplayer.ui.theme.modifiers.verticalGradientBackground
import com.example.musicplayer.ui.viewmodels.RadioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RadioScreen(
    currentSong: Song?,
    scaffoldState: BottomSheetScaffoldState,
    playerState: PlayerState?,
    viewModel: RadioViewModel,
    homeUiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    val context = LocalContext.current
    val surfaceGradient = DataProvider.surfaceGradient(SettingsKeys.isSystemDark(context)).asReversed()
    val showAppSettings = remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val stations = viewModel.radioUiState.currentStationsList
    val filteredRadioStations = filterRadioStations(stations, searchText)
    val density = LocalDensity.current
    val offsetInPx = remember { mutableFloatStateOf(0f) }
    val offsetInDp = remember { mutableStateOf(0.dp) }
    val dynamicAlphaForTopPart = ((offsetInPx.floatValue)/ 1000).coerceIn(0f, 1f)
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 416.dp,
        sheetContent = {
            with(viewModel.radioUiState) {

                when{
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
                        if(filteredRadioStations.isNotEmpty()) {

                            RadioStationList(
                                radioUiState = viewModel.radioUiState,
                                radioStations = filteredRadioStations,
                                currentSong = currentSong,
                                playerState = playerState,
                                onItemClick = {
                                    val songList = stations?.map { radioStation ->
                                        radioStation.toSong()
                                    }
                                    if (homeUiState.selectedPlaylist!!.songList == songList){
                                        onEvent(HomeEvent.OnSongSelected(it.toSong()))
                                        onEvent(HomeEvent.PlaySong)
                                    } else {
                                        onEvent(HomeEvent.OnPlaylistChange(Playlist(666,"Radio", songList!!, DataProvider.getDefaultCover())))
                                        onEvent(HomeEvent.OnSongSelected(it.toSong()))
                                        onEvent(HomeEvent.PlaySong)
                                    }
                                },
                                onLikeClick = {
                                    viewModel.onEvent(RadioEvent.OnRadioLikeClick(it))
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
    )
    {
        LaunchedEffect(scaffoldState.bottomSheetState) {
            snapshotFlow { scaffoldState.bottomSheetState.requireOffset() }
                .collect { newOffsetInPx ->
                    offsetInPx.floatValue = newOffsetInPx
                    offsetInDp.value = with(density) { (newOffsetInPx.toDp() - 64.dp) }
                }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(surfaceGradient))
            .graphicsLayer { alpha = dynamicAlphaForTopPart }
        ) {
            TopPageBar(pageName = R.string.nav_radio, showAppSettings = showAppSettings)
            SearchBar(
                descriptionText = R.string.search_for_radiostation,
                onValueChange = { value -> searchText = value}
            )
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(top = 16.dp), content = {
                item(span = { GridItemSpan(2) }) {
                    RadioFilterItem(filterName = stringResource(R.string.all_radio_stations) ) {
                        viewModel.onEvent(RadioEvent.FetchAllRadioStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(R.string.popular) ) {
                        viewModel.onEvent(RadioEvent.FetchPopularStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(id = R.string.favorites) ) {
                        viewModel.onEvent(RadioEvent.FetchFavoriteStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(R.string.top_rated) ) {
                        viewModel.onEvent(RadioEvent.FetchTopRatedStations)
                    }
                }
                item {
                    RadioFilterItem(filterName = stringResource(R.string.recently_changed) ) {
                        viewModel.onEvent(RadioEvent.FetchRecentlyChangedStations)
                    }
                }
            }
            )
        }
    }


    if (showAppSettings.value) {
        AppSettingsSheet(showAppSettings)
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