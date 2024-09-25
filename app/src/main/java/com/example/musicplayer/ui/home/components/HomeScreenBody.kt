package com.example.musicplayer.ui.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState
import com.example.musicplayer.ui.sharedresources.song.SongListScrollable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenBody(
    homeUiState: HomeUiState,
    favoritesSongList: List<Song>,
    onEvent: (HomeEvent) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    musicControllerUiState: MusicControllerUiState,
    onQuickAccessItemClick: (playlist: Playlist) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val density = LocalDensity.current
    val offsetInPx = remember { mutableFloatStateOf(0f) }
    val offsetInDp = remember { mutableStateOf(0.dp) }
    val dynamicAlphaForTopPart = ((offsetInPx.floatValue - 200f) / 1000).coerceIn(0f, 1f)
    if (!homeUiState.songs.isNullOrEmpty()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 200.dp,
            sheetContent = {
                SongListScrollable(
                    allSongs = homeUiState.songs,
                    selectedSongList = homeUiState.selectedPlaylist?.songList ?: emptyList(),
                    currentSong = homeUiState.selectedSong,
                    favoriteSongs = favoritesSongList,
                    playlist = homeUiState.selectedPlaylist?.songList
                        ?: homeUiState.songs,
                    playerState = musicControllerUiState.playerState,
                    onSongListItemClick = {
                        onEvent(HomeEvent.OnSongSelected(it))
                        onEvent(HomeEvent.PlaySong)
                    },
                    onSongListItemLikeClick = { onEvent(HomeEvent.OnSongLikeClick(it)) },
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
                if (!homeUiState.playlists.isNullOrEmpty() && homeUiState.playlists.size >= 3) {
                    if (homeUiState.playlists[1].songList.isNotEmpty()) {
                        QuickAccessItem(
                            playlist = homeUiState.playlists[1],
                            onQuickAccessItemClick = onQuickAccessItemClick
                        )
                    }
                    if (homeUiState.playlists[2].songList.isNotEmpty()) {
                        QuickAccessItem(
                            playlist = homeUiState.playlists[2],
                            onQuickAccessItemClick = onQuickAccessItemClick
                        )
                    }
                }
            }
        }
    } else {
        NoTracksBox()
    }
}