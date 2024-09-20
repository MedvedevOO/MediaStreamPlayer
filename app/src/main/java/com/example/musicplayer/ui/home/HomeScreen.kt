package com.example.musicplayer.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.ui.home.components.BoxTopSectionForMainScreen
import com.example.musicplayer.ui.home.components.NoTracksOrPermitBox
import com.example.musicplayer.ui.home.components.QuickAccessItem
import com.example.musicplayer.ui.sharedresources.song.SongListScrollable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    storagePermissionsState: MultiplePermissionsState,
    homeUiState: HomeUiState,
    scaffoldState: BottomSheetScaffoldState,
    onEvent: (HomeEvent) -> Unit,
    musicControllerUiState: MusicControllerUiState,
    onQuickAccessItemClick: (playlist: Playlist) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val density = LocalDensity.current
    val offsetInPx = remember { mutableFloatStateOf(0f) }
    val offsetInDp = remember { mutableStateOf(0.dp) }
    val dynamicAlphaForTopPart = ((offsetInPx.floatValue - 200f) / 1000).coerceIn(0f, 1f)

    val favoritesSongList =  if (!homeUiState.playlists.isNullOrEmpty() && homeUiState.playlists.size >= 3) {
        homeUiState.playlists[2].songList
    } else {
        emptyList()
    }
    with(homeUiState) {
        when {
            loading == true && storagePermissionsState.allPermissionsGranted -> {
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

            loading == false -> {
                if (!songs.isNullOrEmpty()) {
                    BottomSheetScaffold(
                        scaffoldState = scaffoldState,
                        sheetPeekHeight = 200.dp,
                        sheetContent = {
                            SongListScrollable(
                                allSongs = songs,
                                selectedSongList = selectedPlaylist?.songList ?: emptyList(),
                                currentSong = musicControllerUiState.currentSong,
                                favoriteSongs =  favoritesSongList,
                                playlist = selectedPlaylist?.songList
                                    ?: songs,
                                playerState = musicControllerUiState.playerState,
                                onSongListItemClick = {
                                    onEvent(HomeEvent.OnSongSelected(it))
                                    onEvent(HomeEvent.PlaySong)
                                },
                                onSongListItemLikeClick = {onEvent(HomeEvent.OnSongLikeClick(it))},
                                onSongListItemSettingsClick = onSongListItemSettingsClick
                            )
                            Spacer(modifier = Modifier.height(50.dp))
                        },
                        sheetDragHandle = {},
                        sheetShape = RoundedCornerShape(5.dp),
                        containerColor = Color.Transparent,
                        sheetShadowElevation = 4.dp,
                        sheetContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                    ) {
                        LaunchedEffect(scaffoldState.bottomSheetState) {
                            snapshotFlow { scaffoldState.bottomSheetState.requireOffset() }
                                .collect { newOffsetInPx ->
                                    // Convert the offset from px to dp within the LaunchedEffect block
                                    offsetInPx.floatValue = newOffsetInPx
                                    offsetInDp.value =
                                        with(density) { (newOffsetInPx.toDp() - 64.dp) }
                                }
                        }

                        BoxTopSectionForMainScreen(
                            homeUiState = homeUiState,
                            musicControllerUiState = musicControllerUiState,
                            onEvent = onEvent,
                            dynamicAlphaForTopPart = dynamicAlphaForTopPart,
                        )
                        Row(
                            modifier = Modifier
                                .offset(y = offsetInDp.value)
                                .horizontalScroll(rememberScrollState(0))
                                .graphicsLayer { alpha = dynamicAlphaForTopPart }
                        ) {
                            if (!playlists.isNullOrEmpty() && playlists.size >= 3) {
                                if (playlists[1].songList.isNotEmpty()) {
                                    QuickAccessItem(
                                        playlist = playlists[1],
                                        onQuickAccessItemClick = onQuickAccessItemClick
                                    )
                                }

                                if (playlists[2].songList.isNotEmpty()) {
                                    QuickAccessItem(
                                        playlist = playlists[2],
                                        onQuickAccessItemClick = onQuickAccessItemClick
                                    )
                                }
                            }
                        }



                    }
                } else {
                    NoTracksOrPermitBox(storagePermissionsState) {
//                        onEvent(HomeEvent.FetchSong)
                    }
                }


            }


            else -> {
                Log.d("HomeScreenErrorMessage", errorMessage.toString())
            }
        }
    }


}




