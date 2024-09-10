package com.example.musicplayer.ui.details

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.details.components.AnimatedGradientBackgroundBox
import com.example.musicplayer.ui.details.components.AnimatedToolBar
import com.example.musicplayer.ui.details.components.BottomScrollableContent
import com.example.musicplayer.ui.details.components.DetailSettingsSheet
import com.example.musicplayer.ui.details.components.RenamePlaylistDialog
import com.example.musicplayer.ui.details.components.TopSectionOverlay
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState
import com.example.musicplayer.ui.sharedresources.albumCoverImage
import com.example.musicplayer.ui.theme.extensions.generateDominantColorState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun DetailScreen(
    homeUiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    navController: NavController,
    musicControllerUiState: MusicControllerUiState,
    content: Any,
    onNavigateUp: () -> Unit,
    onAlbumCardClick: (album: Album) -> Unit,
    onAddTracksClick: (playlist: Playlist) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val showSettings = remember { mutableStateOf(false) }
    val showRenamePlaylist = remember { mutableStateOf(false) }
    val contentName = remember {
        mutableStateOf("content.name")
    }
    val shuffledName = stringResource(R.string.shuffled, contentName.value)
    var contentArtworkUri = Uri.EMPTY
    var songList = mutableStateListOf<Song>()
    var description = "content.description"
    var albumsList = listOf<Album>()
    var newPlaylist: Playlist? = null

    when(content) {
        is Playlist -> {
            val playList = homeUiState.playlists!!.first{it.id == content.id}
            contentName.value = playList.name
            contentArtworkUri = if (playList.id > 2) {
                playList.songList.first().imageUrl.toUri()
            } else {
                playList.artWork
            }
            songList = playList.songList.toMutableStateList()
            description = stringResource(R.string.tracks, songList.size)
            newPlaylist = playList
        }
        is Artist -> {
            val artist = homeUiState.artists!![content.id]
            contentName.value = artist.name
            contentArtworkUri = artist.photo.toUri()
            songList = artist.songList.toMutableStateList()
            albumsList = artist.albumList
            description = stringResource(R.string.albums_tracks, albumsList.size, songList.size)
            newPlaylist = Playlist(
                id = content.id,
                name = contentName.value,
                songList = songList,
                artWork = contentArtworkUri,
            )

        }
        is Album -> {
            val album = homeUiState.albums!![content.id.toInt()]
            contentName.value = album.name
            contentArtworkUri = album.albumCover.toUri()
            songList = album.songList.toMutableStateList()
            description = stringResource(R.string.album_by, album.artist)
            newPlaylist =  Playlist(
                id = content.id.toInt(),
                name = contentName.value,
                songList = songList,
                artWork = contentArtworkUri,
            )
        }
    }

    val onPlayButtonClickLambda = {
        if (homeUiState.selectedPlaylist!!.name == contentName.value){
            if (musicControllerUiState.playerState == PlayerState.PLAYING){
                onEvent(HomeEvent.PauseSong)
            } else if(homeUiState.selectedSong != null){
                onEvent(HomeEvent.ResumeSong)
            } else {
                onEvent(HomeEvent.OnSongSelected(songList[0]))
                onEvent(HomeEvent.ResumeSong)

            }
        } else {
            onEvent(HomeEvent.OnPlaylistChange(newPlaylist!!))
        }
    }


    // Fetch the dynamic image based on the imageUri
    var bitmap by remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply { eraseColor(0xFF454343.toInt()) }) }
    LaunchedEffect(content) {
        bitmap = albumCoverImage(contentArtworkUri?: DataProvider.getDefaultCover(), context)
    }

    bitmap?.let { image ->
        val swatch = image.generateDominantColorState()
        val dominantColors = listOf(Color(swatch.rgb), MaterialTheme.colorScheme.surface)
        val dominantGradient = remember(swatch) { dominantColors }
        // Define gradients and swatches based on the content being displayed
        val surfaceGradient = DataProvider.surfaceGradient(SettingsKeys.isSystemDark(context)).asReversed()

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedGradientBackgroundBox(dominantGradient)

            TopSectionOverlay(
                contentName = contentName.value,
                contentArtworkUri = contentArtworkUri,
                description = description,
                scrollState = scrollState,
                gradient = surfaceGradient
            )

            BottomScrollableContent(
                homeUiState = homeUiState,
                onEvent = onEvent,
                navController = navController,
                musicControllerUiState = musicControllerUiState,
                contentName = contentName.value,
                songList = songList,
                albumsList = albumsList,
                scrollState = scrollState,
                showSettings = showSettings,
                onSongListItemClick = {
                    if (homeUiState.selectedPlaylist!!.songList == songList){
                        onEvent(HomeEvent.OnSongSelected(it))
                        onEvent(HomeEvent.PlaySong)
                    } else {
                        onEvent(HomeEvent.OnPlaylistChange(newPlaylist!!))
                    } },
                onSongListItemLikeClick ={ onEvent(HomeEvent.OnSongLikeClick(it))},
                onPlayButtonClick = onPlayButtonClickLambda,
                onShuffleClick = {
                    newPlaylist = newPlaylist!!.copy(
                        name = shuffledName,
                        songList = songList.shuffled()
                    )
                    onEvent(HomeEvent.OnPlaylistChange(newPlaylist!!))
                },
                onAlbumCardClick = onAlbumCardClick,
                onAddTracksClick = onAddTracksClick
            )
            AnimatedToolBar(
                contentName = contentName.value,
                selectedPlaylist = homeUiState.selectedPlaylist!!,
                playerState = musicControllerUiState.playerState!!,
                scrollState = scrollState,
                surfaceGradient = surfaceGradient,
                onNavigateUp = onNavigateUp,
                onPlayButtonClick = onPlayButtonClickLambda
            )




            if (showSettings.value) {
                DetailSettingsSheet(
                    content = content,
                    onDismiss = {showSettings.value = false },
                    onDetailMenuItemClick = { menuItem, playlistName ->
                        showSettings.value = false
                        when(menuItem) {

                            DataProvider.getString(R.string.download) -> { }
                            DataProvider.getString(R.string.play_next) -> {
                                onEvent(HomeEvent.AddSongListNextToCurrentSong(songList))
                            }
                            DataProvider.getString(R.string.add_to_queue) -> {
                                onEvent(HomeEvent.AddSongListToQueue(songList))
                            }
                            DataProvider.getString(R.string.edit) -> {
                                navController.navigate("editPlaylist/$playlistName")
                            }
                            DataProvider.getString(R.string.rename) -> {
                                showRenamePlaylist.value = true

                            }
                            DataProvider.getString(R.string.add_tracks) -> {
                                navController.navigate("addSongs/${playlistName}")
                            }
                            DataProvider.getString(R.string.delete_playlist) -> {
                                navController.popBackStack()
                                onEvent(HomeEvent.DeletePlaylist(content as Playlist))
                            }
                        }

                    }
                )

            }

            RenamePlaylistDialog(
                homeUiState = homeUiState,
                showRenamePlaylist = showRenamePlaylist
            ) {newName ->
                contentName.value = newName
                onEvent(HomeEvent.RenamePlaylist(newPlaylist!!.id, newName))
                showRenamePlaylist.value = false
            }
        }


    }

//    SongSettingsItem(showSongSettings, showAddToPlaylistDialog, songSettingsItem, surfaceGradient)
}








//@Preview
//@Composable
//fun PreviewDetailScreen() {
//    val context = LocalContext.current
//    val defaultCoverUri = Uri.parse("android.resource://${context.packageName}/${R.drawable.stocksongcover}")
//
//    val album = Album(
//        id = 1,
//        name = "Test",
//        artist = "Test Artist",
//        genre = "Pop",
//        year = "2022",
//        songList = allSongsList.toMutableList(),
//        albumCover = defaultCoverUri
//    )
////        AlbumsDataProvider.album
//
//    ComposeCookBookTheme(true) {
//        SpotifyDetailScreen(album = album)
//    }
//
//}