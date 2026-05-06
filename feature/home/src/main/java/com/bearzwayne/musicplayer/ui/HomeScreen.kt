package com.bearzwayne.musicplayer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.bearzwayne.musicplayer.feature.home.R
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.ui.components.HomeScreenBody
import com.bearzwayne.musicplayer.ui.components.NoPermitBox
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import androidx.core.net.toUri


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    storagePermissionsState: MultiplePermissionsState,
    scaffoldState: BottomSheetScaffoldState,
    musicControllerUiState: MusicControllerUiState,
    onQuickAccessItemClick: (playlist: Playlist) -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val mainViewModel: HomeViewModel =
        hiltViewModel(checkNotNull<ViewModelStoreOwner>(LocalViewModelStoreOwner.current) {
                    "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
                }, null)
    val homeUiState = mainViewModel.homeUiState
    val onEvent = mainViewModel::onEvent
    LaunchedEffect(storagePermissionsState.allPermissionsGranted) {
        if (storagePermissionsState.allPermissionsGranted) {
            onEvent(HomeEvent.FetchData)
        }
    }
    val favoritesSongList =
        if (!homeUiState.playlists.isNullOrEmpty() && homeUiState.playlists.size >= 3) {
            homeUiState.playlists[2].songList
        } else {
            emptyList()
        }
    with(homeUiState) {
        when {
            !storagePermissionsState.allPermissionsGranted -> {
                NoPermitBox(
                    allPermissionsGranted = storagePermissionsState.allPermissionsGranted,
                    shouldShowRationale = storagePermissionsState.shouldShowRationale,
                    launchMultiplePermissionRequest = { storagePermissionsState.launchMultiplePermissionRequest() }) {
                    onEvent(HomeEvent.FetchData)
                }
            }

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
                HomeScreenBody(
                    homeUiState = homeUiState,
                    favoritesSongList = favoritesSongList,
                    onEvent = onEvent,
                    scaffoldState = scaffoldState,
                    musicControllerUiState = musicControllerUiState,
                    onQuickAccessItemClick = onQuickAccessItemClick,
                    onSongListItemSettingsClick = onSongListItemSettingsClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewHomeScreen() {
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val defaultCoverUri =
        "android.resource://${context.packageName}/${R.drawable.allsongsplaylist}".toUri()
            .toString()
    val testSong = Song(
        mediaId = "0",
        title = "Title",
        artist = "Artist",
        album = "Album",
        genre = "Genre",
        year = "2024",
        songUrl = "",
        imageUrl = defaultCoverUri,
    )

    val homeUiState = HomeUiState(
        loading = false,
        songs = listOf(testSong, testSong, testSong),
        playlists = listOf(
            Playlist(0, "All Tracks", emptyList(), defaultCoverUri),
            Playlist(0, "All Tracks", emptyList(), defaultCoverUri),
            Playlist(0, "All Tracks", emptyList(), defaultCoverUri)
        ),
        selectedSong = testSong,
        selectedPlaylist = Playlist(0, "All Tracks", emptyList(), defaultCoverUri),
        errorMessage = null
    )
    MusicPlayerTheme {
        HomeScreenBody(
            homeUiState =homeUiState,
            favoritesSongList = emptyList(),
            onEvent ={},
            scaffoldState =scaffoldState,
            musicControllerUiState = MusicControllerUiState(),
            onQuickAccessItemClick ={},
            onSongListItemSettingsClick ={}
        )
    }

}

