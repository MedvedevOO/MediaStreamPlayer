package com.example.musicplayer.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.PermissionHandler
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.addsongstoplaylist.AddSongsToPlaylistScreen
import com.example.musicplayer.ui.details.DetailScreen
import com.example.musicplayer.ui.details.DetailScreenEvent
import com.example.musicplayer.ui.details.DetailScreenViewModel
import com.example.musicplayer.ui.editplaylist.EditPlaylistScreen
import com.example.musicplayer.ui.home.HomeScreen
import com.example.musicplayer.ui.home.HomeViewModel
import com.example.musicplayer.ui.sharedresources.MusicPlayerScreenAnimatedBackground
import com.example.musicplayer.ui.library.LibraryScreen
import com.example.musicplayer.ui.library.LibraryScreenViewModel
import com.example.musicplayer.ui.navigation.Destination
import com.example.musicplayer.ui.radio.RadioEvent
import com.example.musicplayer.ui.radio.RadioScreen
import com.example.musicplayer.ui.radio.RadioViewModel
import com.example.musicplayer.ui.search.SearchScreen
import com.example.musicplayer.ui.search.SearchScreenViewModel
import com.example.musicplayer.ui.settings.AppSettingsSheet
import com.example.musicplayer.ui.sharedresources.TopPageBar
import com.example.musicplayer.ui.sharedresources.song.SongSettingsBottomSheet
import com.example.musicplayer.ui.sharedresources.songBar.SongBar
import com.example.musicplayer.ui.songscreen.SongScreen
import com.example.musicplayer.ui.songscreen.SongViewModel
import com.example.musicplayer.ui.viewmodels.SharedViewModel
import com.example.musicplayer.ui.viewmodels.SharedViewModelEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun MusicPlayerApp(sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()

    MusicPlayerNavHost(
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MusicPlayerNavHost(navController: NavHostController, sharedViewModel: SharedViewModel) {
    val musicControllerUiState = sharedViewModel.musicControllerUiState
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentDestinationName = getCurrentDestinationName(currentDestination?.route)
    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionHandler.permissions()
    )
    val context = LocalContext.current

    val isInitialized = rememberSaveable { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val radioScaffoldState = rememberBottomSheetScaffoldState()
    val showSongBar = remember { mutableStateOf(false) }
    val showPlayer = remember { mutableStateOf(false) }
    val showAppSettings = remember { mutableStateOf(false) }
    val showSongSettings = remember { mutableStateOf(false) }
    val showAddToPlaylistDialog = remember { mutableStateOf(false) }
    val songSettingsItem = remember {
        mutableStateOf(
            //Todo: иправить костыль
            Song(
                mediaId = "",
                title = "Test Song",
                artist = "Test Artist",
                album = " Test Album",
                imageUrl = DataProvider.getDefaultCover().toString(),
                genre = "Pop",
                year = "2022",
                songUrl = ""
            )
        )
    }
    if (!isInitialized.value) {

        LaunchedEffect(key1 = Unit) {
            if (storagePermissionsState.allPermissionsGranted) {
//                mainViewModel.onEvent(HomeEvent.FetchSong)
                isInitialized.value = true
            }


        }
    }

    LaunchedEffect(currentBackStackEntry) {
        showSongBar.value = musicControllerUiState.playerState != PlayerState.STOPPED
    }
    val menuItems = listOf(
        Pair(Destination.home, Icons.Outlined.Home),
        Pair(Destination.radio, Icons.Outlined.Radio),
        Pair(Destination.search, Icons.Outlined.Search),
        Pair(Destination.library, Icons.Outlined.LibraryMusic),
    )
    val bottomNavBackground = MaterialTheme.colorScheme.background
    val itemColors = NavigationBarItemColors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.onSurface,
        selectedIndicatorColor = Color.Transparent,
        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledIconColor = Color.Gray,
        disabledTextColor = Color.LightGray
    )
    Scaffold(
        topBar = {
            if (currentDestination?.hierarchy?.any {
                    it.route?.startsWith(
                        "detail",
                        true
                    ) == false
                } == true) {
                TopPageBar(pageName = currentDestinationName, showAppSettings = showAppSettings)
            }
        },
        bottomBar = {

            if (currentDestination?.hierarchy?.any {
                    it.route?.startsWith(
                        "detail",
                        true
                    ) == false
                } == true) {
                NavigationBar(
                    containerColor = bottomNavBackground,
                    modifier = Modifier.height(52.dp)
                ) {

                    menuItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Box(
                                    modifier = Modifier.size(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = screen.second, contentDescription = null)
                                }
                            },
                            colors = itemColors,
                            selected = currentDestination.hierarchy.any { it.route == screen.first },
                            onClick = {
                                navController.navigate(screen.first) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }

        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MusicPlayerScreenAnimatedBackground(
                currentSong = musicControllerUiState.currentSong,
                playerState = musicControllerUiState.playerState,
                currentBackStackEntry = currentBackStackEntry
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            NavHost(navController = navController, startDestination = Destination.home) {

                composable(route = Destination.home) {
                    val mainViewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        storagePermissionsState = storagePermissionsState,
                        homeUiState = mainViewModel.homeUiState,
                        scaffoldState = scaffoldState,
                        onEvent = mainViewModel::onEvent,
                        musicControllerUiState = musicControllerUiState,
                        onQuickAccessItemClick = {
                            navController.navigate("detail/playlist/${it.id}")
                        },
                        onSongListItemSettingsClick = {
                            showSongSettings.value = true
                            songSettingsItem.value = it
                        }
                    )
                }

                composable(route = Destination.radio) {
                    val radioViewModel: RadioViewModel = hiltViewModel()
                    LaunchedEffect(Unit) {
                        radioViewModel.onEvent(RadioEvent.FetchPopularStations)
                    }
                    RadioScreen(
                        scaffoldState = radioScaffoldState,
                        currentSong = sharedViewModel.musicControllerUiState.currentSong,
                        playerState = sharedViewModel.musicControllerUiState.playerState,
                        radioUiState = radioViewModel.radioUiState,
                        onRadioEvent = radioViewModel::onEvent
                    )
                }

                composable(route = Destination.search) {
                    val searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
                    SearchScreen(
                        uiState = searchScreenViewModel.searchScreenUiState,
                        onEvent = searchScreenViewModel::onEvent,
                        onSongListItemSettingsClick = {
                            showSongSettings.value = true
                            songSettingsItem.value = it

                        }
                    )
                }

                composable(route = Destination.library) {
                    val libraryScreenViewModel: LibraryScreenViewModel = hiltViewModel()

                    LibraryScreen(
                        uiState = libraryScreenViewModel.libraryScreenUiState,
                        onEvent = libraryScreenViewModel::onEvent,
                        onLibraryItemClick = {
                            var type = ""
                            var id = 0
                            when (it) {
                                is Album -> {
                                    type = "album"
                                    id = it.id.toInt()
                                }

                                is Artist -> {
                                    type = "artist"
                                    id = it.id
                                }

                                is Playlist -> {
                                    type = "playlist"
                                    id = it.id
                                }

                                else -> {}
                            }
                            navController.navigate("detail/$type/$id")
                        })
                }

                composable(
                    route = Destination.detail,
                    arguments = listOf(
                        navArgument("type") { type = NavType.StringType },
                        navArgument("id") { type = NavType.StringType })
                ) { backStackEntry ->
                    val detailScreenViewModel: DetailScreenViewModel = hiltViewModel()
                    val contentType = backStackEntry.arguments?.getString("type")
                    val id = backStackEntry.arguments?.getString("id")
                    var content: Any? = null
                    if (contentType == "album") {
                        content =
                            detailScreenViewModel.onEvent(DetailScreenEvent.FindAlbumById(id!!.toInt()))
                    }
                    if (contentType == "artist") {
                        content =
                            detailScreenViewModel.onEvent(DetailScreenEvent.FindArtistById(id!!.toInt()))
                    }
                    if (contentType == "playlist") {
                        content =
                            detailScreenViewModel.detailScreenUiState.playlists!!.first { it.id == id!!.toInt() }
                    }


                    DetailScreen(
                        uiState = detailScreenViewModel.detailScreenUiState,
                        onEvent = detailScreenViewModel::onEvent,
                        content = content!!,
                        onNavigateUp = { navController.navigateUp() },
                        onAlbumCardClick = { albumId ->
                            navController.navigate("detail/album/$albumId")
                        },
                        onAddTracksClick = { playlistId ->
                            navController.navigate("addSongs/$playlistId")
                        },
                        onEditPlayListClick = { playlistId ->
                            navController.navigate("editPlaylist/$playlistId")
                        },
                        onDeletePlaylistClick = {
                            navController.popBackStack()
                        },
                        onSongListItemSettingsClick = {
                            showSongSettings.value = true
                            songSettingsItem.value = it
                        }
                    )


                }

                composable(
                    route = Destination.addSongs,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    var playlist =
                        sharedViewModel.onEvent(SharedViewModelEvent.FindPlaylistById(id!!.toInt())) as Playlist
                    AddSongsToPlaylistScreen(
                        allSongsList = musicControllerUiState.songs!!,
                        playlist = playlist,
                        onAddClicked = {
                            playlist = playlist.copy(
                                songList = playlist.songList.toMutableList().apply { add(it) }
                            )
                            sharedViewModel.onEvent(SharedViewModelEvent.AddNewPlaylist(playlist))
                        },
                        onBackClick = { navController.navigateUp() }
                    )
                }

                composable(
                    route = Destination.editPlaylist,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    val playlist =
                        sharedViewModel.onEvent(SharedViewModelEvent.FindPlaylistById(id!!.toInt())) as Playlist
                    EditPlaylistScreen(
                        playlist = playlist,
                        navController = navController,
                        onEvent = sharedViewModel::onEvent
                    )
                }
            }
            val songViewModel: SongViewModel = hiltViewModel()
            if (showSongBar.value && sharedViewModel.musicControllerUiState.currentSong != null) {
                SongBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(76.dp)
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 8.dp, end = 4.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showPlayer.value = true },
                    onEvent = songViewModel::onEvent,
                    playerState = musicControllerUiState.playerState,
                    previousSong = musicControllerUiState.previousSong,
                    song = musicControllerUiState.currentSong,
                    nextSong = musicControllerUiState.nextSong
                )
            }

            if (showPlayer.value && musicControllerUiState.currentSong != null) {
                SongScreen(
                    showScreen = showPlayer,
                    onEvent = songViewModel::onEvent,
                    musicControllerUiState = musicControllerUiState,
                    onSongListItemSettingsClick = {
                        showSongSettings.value = true
                        songSettingsItem.value = it
                    },
                    onGotoArtistClick = {
                        val authorId = sharedViewModel.onEvent(
                            SharedViewModelEvent.FindArtistIdByName(musicControllerUiState.currentSong.artist)
                        ) as Int
                        navController.navigate("detail/artist/$authorId")
                        showPlayer.value = false
                    },
                    onGotoAlbumClick = {
                        val albumId = sharedViewModel.onEvent(
                            SharedViewModelEvent.FindAlbumIdByName(musicControllerUiState.currentSong.album)
                        ) as Int
                        navController.navigate("detail/album/$albumId")
                        showPlayer.value = false
                    }


                )

            }

            if (showAppSettings.value) {
                AppSettingsSheet(showAppSettings)
            }

            SongSettingsBottomSheet(
                playlists = musicControllerUiState.playlists ?: emptyList(),
                selectedPlaylist = musicControllerUiState.selectedPlaylist?.songList ?: emptyList(),
                currentSong = musicControllerUiState.currentSong,
                showSongSettings = showSongSettings,
                showAddToPlaylistDialog = showAddToPlaylistDialog,
                songSettingsItem = songSettingsItem.value,
                onOkAddPlaylistClick = { newPlaylist ->

                    val newSongList = newPlaylist.songList.toMutableList()
                        .apply { add(songSettingsItem.value) }
                    val resultList = newPlaylist.copy(
                        songList = newSongList,
                        artWork = songSettingsItem.value.imageUrl.toUri()
                    )
                    sharedViewModel.onEvent(SharedViewModelEvent.AddNewPlaylist(resultList))
                    val toastText =
                        context.getString(R.string.track_added_to_playlist, resultList.name)
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    showAddToPlaylistDialog.value = false

                },
                onPlaylistToAddSongChosen = {
                    sharedViewModel.onEvent(SharedViewModelEvent.AddNewPlaylist(it))
                },
                onDetailMenuItemClick = { menuItem, song ->
                    showSongSettings.value = false
                    showPlayer.value = false
                    when (menuItem) {
                        context.getString(R.string.download) -> {}
                        context.getString(R.string.add_to_playlist_variant) -> showAddToPlaylistDialog.value =
                            true

                        context.getString(R.string.add_to_queue) -> sharedViewModel.onEvent(
                            SharedViewModelEvent.AddSongListToQueue(listOf(song))
                        )

                        context.getString(R.string.play_next) -> sharedViewModel.onEvent(
                            SharedViewModelEvent.AddSongNextToCurrentSong(song)
                        )

                        context.getString(R.string.go_to_artist) -> {
                            val authorId = sharedViewModel.onEvent(
                                SharedViewModelEvent.FindArtistIdByName(song.artist)
                            ) as Int
                            navController.navigate("detail/artist/$authorId")

                        }

                        context.getString(R.string.go_to_album) -> {
                            val albumId = sharedViewModel.onEvent(
                                SharedViewModelEvent.FindAlbumIdByName(song.album)
                            ) as Int
                            navController.navigate("detail/album/$albumId")


                        }

                    }
                }
            )
        }

    }
}

fun getCurrentDestinationName(route: String?): Int {
    return when (route) {
        "home" -> R.string.app_name
        "radio" -> R.string.nav_radio
        "search" -> R.string.nav_search
        "library" -> R.string.nav_library
        else -> R.string.nav_home
    }
}