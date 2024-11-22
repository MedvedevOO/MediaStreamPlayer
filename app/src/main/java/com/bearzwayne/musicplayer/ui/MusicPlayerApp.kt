package com.bearzwayne.musicplayer.ui

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.PermissionHandler
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.addsongstoplaylist.AddSongsToPlaylistScreen
import com.bearzwayne.musicplayer.ui.details.DetailScreen
import com.bearzwayne.musicplayer.ui.editplaylist.EditPlaylistScreen
import com.bearzwayne.musicplayer.ui.home.HomeScreen
import com.bearzwayne.musicplayer.ui.library.LibraryScreen
import com.bearzwayne.musicplayer.ui.navigation.AddSongToPlaylist
import com.bearzwayne.musicplayer.ui.navigation.AddSongs
import com.bearzwayne.musicplayer.ui.navigation.AppSettings
import com.bearzwayne.musicplayer.ui.navigation.CustomNavType
import com.bearzwayne.musicplayer.ui.navigation.Detail
import com.bearzwayne.musicplayer.ui.navigation.EditPlaylist
import com.bearzwayne.musicplayer.ui.navigation.Home
import com.bearzwayne.musicplayer.ui.navigation.Library
import com.bearzwayne.musicplayer.ui.navigation.Radio
import com.bearzwayne.musicplayer.ui.navigation.Search
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.radio.RadioScreen
import com.bearzwayne.musicplayer.ui.search.SearchScreen
import com.bearzwayne.musicplayer.ui.settings.AppSettingsSheet
import com.bearzwayne.musicplayer.ui.sharedresources.AddNewPlaylistDialog
import com.bearzwayne.musicplayer.ui.sharedresources.MusicPlayerScreenAnimatedBackground
import com.bearzwayne.musicplayer.ui.sharedresources.TopPageBar
import com.bearzwayne.musicplayer.ui.sharedresources.song.AddSongToPlaylistSheet
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongSettingsBottomSheet
import com.bearzwayne.musicplayer.ui.sharedresources.songBar.SongBar
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModelEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlin.reflect.typeOf

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
    val scaffoldState = rememberBottomSheetScaffoldState()
    val radioScaffoldState = rememberBottomSheetScaffoldState()
    val menuItems = listOf(
        Pair(Home, Icons.Outlined.Home),
        Pair(Radio, Icons.Outlined.Radio),
        Pair(Search, Icons.Outlined.Search),
        Pair(Library, Icons.Outlined.LibraryMusic),
    )
    val bottomNavBackground = MaterialTheme.colorScheme.surfaceVariant
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
                    it.route?.contains(
                        Detail::class.qualifiedName.toString(),
                        true
                    ) == false &&
                            it.route?.contains(
                                EditPlaylist::class.qualifiedName.toString(),
                                true
                            ) == false &&
                            it.route?.contains(
                                AddSongs::class.qualifiedName.toString(),
                                true
                            ) == false
                } == true
            ) {
                TopPageBar(pageName = currentDestinationName) { navController.navigate(AppSettings) }
            }
        },
        bottomBar = {

            if (currentDestination?.hierarchy?.any {
                    it.route?.contains(
                        Detail::class.qualifiedName.toString(),
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
                            label = { screen.first::class.simpleName },
                            selected = currentDestination.hierarchy.any { it.route == screen.first::class.qualifiedName },
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
            NavHost(navController = navController, startDestination = Home) {

                composable<Home> {
                    HomeScreen(
                        storagePermissionsState = storagePermissionsState,
                        scaffoldState = scaffoldState,
                        musicControllerUiState = musicControllerUiState,
                        onQuickAccessItemClick = {
                            navController.navigate(
                                Detail(
                                    type = "playlist",
                                    id = it.id
                                )
                            )
                        },
                        onSongListItemSettingsClick = {
                            navController.navigate(
                                SongSettings(
                                    it
                                )
                            )
                        }
                    )
                }

                composable<Radio> {
                    RadioScreen(
                        scaffoldState = radioScaffoldState,
                        currentSong = sharedViewModel.musicControllerUiState.currentSong,
                        playerState = sharedViewModel.musicControllerUiState.playerState
                    )
                }

                composable<Search> {
                    SearchScreen(
                        onSongListItemSettingsClick = {
                            navController.navigate(
                                SongSettings(
                                    it
                                )
                            )
                        }
                    )
                }

                composable<Library> {
                    LibraryScreen(
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
                            navController.navigate(
                                Detail(
                                    type = type,
                                    id = id
                                )
                            )
                        }
                    )
                }

                composable<Detail> { backStackEntry ->
                    val args = backStackEntry.toRoute<Detail>()
                    DetailScreen(
                        contentType = args.type,
                        contentId = args.id,
                        contentName = args.name,
                        onNavigateUp = { navController.popBackStack() },
                        onAlbumCardClick = { albumId ->
                            navController.navigate(
                                Detail(
                                    type = "album",
                                    id = albumId
                                )
                            )
                        },
                        onAddTracksClick = { playlist ->
                            navController.navigate(
                                AddSongs(
                                    playlist = playlist
                                )
                            )
                        },
                        onEditPlayListClick = { playlist ->
                            navController.navigate(
                                EditPlaylist(
                                    playlist = playlist
                                )
                            )
                        },
                        onDeletePlaylistClick = {
                            navController.popBackStack()
                        },
                        onSongListItemSettingsClick = {
                            navController.navigate(
                                SongSettings(
                                    it
                                )
                            )
                        }
                    )
                }

                composable<AddSongs>(
                    typeMap = mapOf(typeOf<Playlist>() to CustomNavType.playlistType)
                ) { backStackEntry ->
                    val args = backStackEntry.toRoute<AddSongs>()
                    val songList = remember { mutableStateOf(args.playlist.songList) }
                    AddSongsToPlaylistScreen(
                        allSongsList = musicControllerUiState.songs!!,
                        songList = songList.value,
                        onAddClicked = {
                            songList.value = songList.value.toMutableList().apply { add(it) }
                            val playlist = args.playlist.copy(
                                songList = songList.value
                            )
                            sharedViewModel.onEvent(SharedViewModelEvent.AddNewPlaylist(playlist))
                        },
                        onBackClick = { navController.navigateUp() }
                    )
                }

                composable<EditPlaylist>(
                    typeMap = mapOf(typeOf<Playlist>() to CustomNavType.playlistType)
                ) { backStackEntry ->
                    val args = backStackEntry.toRoute<EditPlaylist>()
                    val playlist = args.playlist
                    EditPlaylistScreen(
                        playlist = playlist,
                        navController = navController,
                        onEvent = sharedViewModel::onEvent
                    )
                }

                dialog<AppSettings> {
                    AppSettingsSheet { navController.navigateUp() }
                }

                dialog<SongSettings>(
                    typeMap = mapOf(typeOf<Song>() to CustomNavType.songType)
                ) { backStackEntry ->
                    val args = backStackEntry.toRoute<SongSettings>()
                    SongSettingsBottomSheet(
                        selectedPlaylist = musicControllerUiState.selectedPlaylist?.songList
                            ?: emptyList(),
                        currentSong = musicControllerUiState.currentSong,
                        songSettingsItem = args.song,
                        onDetailMenuItemClick = { menuItem, song ->
                            when (menuItem) {
                                context.getString(R.string.download) -> {}
                                context.getString(R.string.add_to_playlist_variant) -> navController.navigate(
                                    AddSongToPlaylist(song)
                                )
                                context.getString(R.string.add_to_queue) -> sharedViewModel.onEvent(
                                    SharedViewModelEvent.AddSongListToQueue(listOf(song))
                                )
                                context.getString(R.string.play_next) -> sharedViewModel.onEvent(
                                    SharedViewModelEvent.AddSongNextToCurrentSong(song)
                                )
                                context.getString(R.string.go_to_artist) -> {
                                    navController.navigate(
                                        Detail(
                                            type = "artist",
                                            name = song.artist
                                        )
                                    )
                                }
                                context.getString(R.string.go_to_album) -> {
                                    navController.navigate(
                                        Detail(
                                            type = "album",
                                            name = song.album
                                        )
                                    )
                                }
                            }
                        },
                        onDismiss = { navController.navigateUp() }
                    )
                }

                dialog<AddSongToPlaylist>(
                    typeMap = mapOf(typeOf<Song>() to CustomNavType.songType)
                ) { backStackEntry ->
                    val args = backStackEntry.toRoute<SongSettings>()
                    val showCreatePlaylistDialog = remember { mutableStateOf(false) }
                    AddSongToPlaylistSheet(
                        playlists = musicControllerUiState.playlists,
                        songSettingsItem = args.song,
                        onDismissRequest = { navController.navigateUp() },
                        onCreatePlaylistClick = { showCreatePlaylistDialog.value = true },
                        onPlaylistToAddSongChosen = {
                            sharedViewModel.onEvent(SharedViewModelEvent.AddNewPlaylist(it))
                            navController.navigateUp()
                        }
                    )
                    if (showCreatePlaylistDialog.value) {
                        AddNewPlaylistDialog(
                            onOkClicked = { newPlaylist ->
                                sharedViewModel.onEvent(
                                    SharedViewModelEvent.AddSongToNewPlaylist(
                                        newPlaylist,
                                        args.song,
                                        context
                                    )
                                )
                                navController.navigateUp()
                            },
                            showDialog = showCreatePlaylistDialog,
                            allPlaylistNames = musicControllerUiState.playlists.map { it.name }
                        )
                    }
                }
            }

            if (sharedViewModel.musicControllerUiState.currentSong != null) {
                SongBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(76.dp)
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 8.dp, end = 4.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .semantics {
                            contentDescription = context.getString(R.string.cd_play)
                        },
                    musicControllerUiState = musicControllerUiState,
                    navController = navController
                )
            }
        }

    }
}

fun getCurrentDestinationName(route: String?): Int {
    return when (route) {
        Home::class.qualifiedName -> R.string.app_name
        Radio::class.qualifiedName -> R.string.nav_radio
        Search::class.qualifiedName -> R.string.nav_search
        Library::class.qualifiedName -> R.string.nav_library
        else -> R.string.nav_home
    }
}