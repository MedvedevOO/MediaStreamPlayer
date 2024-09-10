package com.example.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
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
import com.example.musicplayer.data.PermissionHandler
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.addsongstoplaylist.AddSongsToPlaylistScreen
import com.example.musicplayer.ui.details.DetailScreen
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.navigation.Destination
import com.example.musicplayer.ui.home.HomeViewModel
import com.example.musicplayer.ui.home.HomeScreen
import com.example.musicplayer.ui.home.components.MusicPlayerScreenAnimatedBackground
import com.example.musicplayer.ui.library.LibraryScreen
import com.example.musicplayer.ui.search.SearchScreen
import com.example.musicplayer.ui.sharedresources.songBar.SongBar
import com.example.musicplayer.ui.viewmodels.SharedViewModel
import com.example.musicplayer.ui.songscreen.SongViewModel
import com.example.musicplayer.ui.editplaylist.EditPlaylistScreen
import com.example.musicplayer.ui.radio.RadioEvent
import com.example.musicplayer.ui.radio.RadioScreen
import com.example.musicplayer.ui.songscreen.SongScreen
import com.example.musicplayer.ui.viewmodels.RadioViewModel
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
    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionHandler.permissions()
    )
    val mainViewModel: HomeViewModel = hiltViewModel()
    val isInitialized = rememberSaveable { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val radioScaffoldState = rememberBottomSheetScaffoldState()
    val showSongBar = remember { mutableStateOf(false) }
    val showPlayer = remember { mutableStateOf(false) }
    if (!isInitialized.value) {

        LaunchedEffect(key1 = Unit) {
            if (storagePermissionsState.allPermissionsGranted) {
                mainViewModel.onEvent(HomeEvent.FetchSong)
                isInitialized.value = true
            }


        }
    }

    LaunchedEffect(currentBackStackEntry) {
        showSongBar.value = musicControllerUiState.playerState != PlayerState.STOPPED
    }
    val menuItems = listOf(
        Pair(Destination.home, Icons.Outlined.Home),
        Pair(Destination.radio,Icons.Outlined.Radio),
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
        bottomBar = {
            NavigationBar(
                containerColor = bottomNavBackground,
                modifier = Modifier.height(40.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                menuItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Icon(imageVector = screen.second, contentDescription = null)
                            }
                        },
                        colors = itemColors,
                        selected = currentDestination?.hierarchy?.any { it.route == screen.first} == true,
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
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            MusicPlayerScreenAnimatedBackground(musicControllerUiState.currentSong, musicControllerUiState.playerState)
            NavHost(navController = navController, startDestination = Destination.home) {

                composable(route = Destination.home) {
                    HomeScreen(
                        storagePermissionsState = storagePermissionsState,
                        homeUiState = mainViewModel.homeUiState,
                        scaffoldState = scaffoldState,
                        onEvent = mainViewModel::onEvent,
                        navController = navController,
                        musicControllerUiState = musicControllerUiState,
                        onQuickAccessItemClick = {
                            navController.navigate("detail/playlist/${it.id}")
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
                        viewModel = radioViewModel,
                        homeUiState = mainViewModel.homeUiState,
                        onEvent = mainViewModel::onEvent
                    )
                }

                composable(route = Destination.search) {
                        SearchScreen(
                            homeUiState = mainViewModel.homeUiState,
                            onEvent = mainViewModel::onEvent,
                            navController = navController,
                            musicControllerUiState = sharedViewModel.musicControllerUiState
                        )
                }

                composable(route = Destination.library) {
                        LibraryScreen(
                            homeUiState = mainViewModel.homeUiState,
                            onEvent = mainViewModel::onEvent,
                            onLibraryItemClick = {
                                var type = ""
                                var id = 0
                                when(it) {
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
                    arguments = listOf(navArgument("type"){type = NavType.StringType}, navArgument("id"){type = NavType.StringType})
                    ) { backStackEntry ->
                    val contentType = backStackEntry.arguments?.getString("type")
                    val id = backStackEntry.arguments?.getString("id")
                    var content: Any? = null
                    if (contentType == "album") {
                        content = mainViewModel.homeUiState.albums!!.first { it.id == id!!.toLong() }
                    }
                    if (contentType == "artist") {
                        content = mainViewModel.homeUiState.artists!!.first { it.id == id!!.toInt() }
                    }
                    if (contentType == "playlist") {
                        content = mainViewModel.homeUiState.playlists!!.first { it.id == id!!.toInt() }
                    }

                    DetailScreen(
                        homeUiState = mainViewModel.homeUiState,
                        onEvent = mainViewModel::onEvent,
                        navController = navController,
                        musicControllerUiState = musicControllerUiState,
                        content = content!!,
                        onNavigateUp = {navController.navigateUp()},
                        onAlbumCardClick = {navController.navigate("detail/album/${it.name}")},
                        onAddTracksClick = {
                            navController.navigate("addSongs/${it.name}")
                        }
                    )


                }

                composable(
                    route = Destination.addSongs,
                    arguments = listOf(navArgument("name"){type = NavType.StringType})
                ) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name")
                    var playlist = mainViewModel.homeUiState.playlists!!.first { it.name == name }
                    AddSongsToPlaylistScreen(
                        allSongsList = mainViewModel.homeUiState.songs!!,
                        playlist = playlist,
                        onAddClicked = {
                            playlist = playlist.copy(
                                songList = playlist.songList.toMutableList().apply { add(it) }
                            )
                            mainViewModel.onEvent(HomeEvent.AddNewPlaylist(playlist))
                        },
                        onBackClick = {navController.navigateUp()}
                    )
                }

                composable(
                    route = Destination.editPlaylist,
                    arguments = listOf(navArgument("name"){type = NavType.StringType})
                ) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name")
                    val playlist = mainViewModel.homeUiState.playlists!!.first { it.name == name }
                    EditPlaylistScreen(
                        playlist = playlist,
                        navController = navController,
                        onEvent = mainViewModel::onEvent)
                }
            }

            if (showSongBar.value && !mainViewModel.homeUiState.songs.isNullOrEmpty()) {
                SongBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(70.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showPlayer.value = true },
                    onEvent = mainViewModel::onEvent,
                    playerState = musicControllerUiState.playerState,
                    previousSong = musicControllerUiState.previousSong,
                    song = musicControllerUiState.currentSong,
                    nextSong = musicControllerUiState.nextSong
                )
            }

            if (showPlayer.value) {
                val songViewModel: SongViewModel = hiltViewModel()

                SongScreen(
                    showScreen = showPlayer,
                    homeUiState = mainViewModel.homeUiState,
                    navController = navController,
                    onHomeEvent = mainViewModel::onEvent,
                    onEvent = songViewModel::onEvent,
                    musicControllerUiState = musicControllerUiState,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }

        }

    }
}

