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
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.details.components.AnimatedGradientBackgroundBox
import com.example.musicplayer.ui.details.components.AnimatedToolBar
import com.example.musicplayer.ui.details.components.BottomScrollableContent
import com.example.musicplayer.ui.details.components.DetailSettingsSheet
import com.example.musicplayer.ui.details.components.RenamePlaylistDialog
import com.example.musicplayer.ui.details.components.TopSectionOverlay
import com.example.musicplayer.ui.sharedresources.albumCoverImage
import com.example.musicplayer.ui.theme.extensions.generateDominantColorState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun DetailScreen(
    uiState: DetailScreenUiState,
    onEvent: (DetailScreenEvent) -> Unit,
    content: Any,
    onNavigateUp: () -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlistId: Int) -> Unit,
    onEditPlayListClick: (playlistId: Int) -> Unit,
    onDeletePlaylistClick: () -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
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
            contentName.value = content.name
            contentArtworkUri = if (content.id > 2 && content.songList.isNotEmpty()) {
                content.songList.first().imageUrl.toUri()
            } else {
                content.artWork
            }
            songList = content.songList.toMutableStateList()
            description = stringResource(R.string.tracks, songList.size)
            newPlaylist = content
        }
        is Artist -> {
            contentName.value = content.name
            contentArtworkUri = content.photo.toUri()
            songList = content.songList.toMutableStateList()
            albumsList = content.albumList
            description = stringResource(R.string.albums_tracks, albumsList.size, songList.size)
            newPlaylist = Playlist(
                id = content.id,
                name = contentName.value,
                songList = songList,
                artWork = contentArtworkUri,
            )

        }
        is Album -> {
            contentName.value = content.name
            contentArtworkUri = content.albumCover.toUri()
            songList = content.songList.toMutableStateList()
            description = stringResource(R.string.album_by, content.artist)
            newPlaylist = Playlist(
                id = content.id.toInt(),
                name = contentName.value,
                songList = songList,
                artWork = contentArtworkUri,
            )
        }
    }

    val onPlayButtonClickLambda = {
        if (uiState.selectedPlaylist!!.name == contentName.value){
            if (uiState.playerState == PlayerState.PLAYING){
                onEvent(DetailScreenEvent.PauseSong)
            } else if(uiState.selectedSong != null){
                onEvent(DetailScreenEvent.ResumeSong)
            } else {
                onEvent(DetailScreenEvent.OnSongSelected(songList[0]))
                onEvent(DetailScreenEvent.ResumeSong)

            }
        } else {
            onEvent(DetailScreenEvent.OnPlaylistChange(newPlaylist!!))
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
                uiState = uiState,
                onEvent = onEvent,
                contentName = contentName.value,
                songList = songList,
                albumsList = albumsList,
                scrollState = scrollState,
                showSettings = showSettings,
                onSongListItemClick = {
                    if (uiState.selectedPlaylist!!.songList == songList){
                        onEvent(DetailScreenEvent.OnSongSelected(it))
                        onEvent(DetailScreenEvent.PlaySong)
                    } else {
                        onEvent(DetailScreenEvent.OnPlaylistChange(newPlaylist!!))
                    } },
                onSongListItemLikeClick ={ onEvent(DetailScreenEvent.OnSongLikeClick(it))},
                onPlayButtonClick = onPlayButtonClickLambda,
                onShuffleClick = {
                    newPlaylist = newPlaylist!!.copy(
                        name = shuffledName,
                        songList = songList.shuffled()
                    )
                    onEvent(DetailScreenEvent.OnPlaylistChange(newPlaylist!!))
                },
                onAlbumCardClick = onAlbumCardClick,
                onAddTracksClick = onAddTracksClick,
                onSongListItemSettingsClick = onSongListItemSettingsClick
            )
            AnimatedToolBar(
                contentName = contentName.value,
                selectedPlaylist = uiState.selectedPlaylist!!,
                playerState = uiState.playerState,
                scrollState = scrollState,
                surfaceGradient = surfaceGradient,
                onNavigateUp = onNavigateUp,
                onPlayButtonClick = onPlayButtonClickLambda
            )




            if (showSettings.value) {
                DetailSettingsSheet(
                    content = content,
                    onDismiss = {showSettings.value = false },
                    onDetailMenuItemClick = { menuItem, playlistId ->
                        showSettings.value = false
                        when(menuItem) {

                            DataProvider.getString(R.string.download) -> { }
                            DataProvider.getString(R.string.play_next) -> {
                                onEvent(DetailScreenEvent.AddSongListNextToCurrentSong(songList))
                            }
                            DataProvider.getString(R.string.add_to_queue) -> {
                                onEvent(DetailScreenEvent.AddSongListToQueue(songList))
                            }
                            DataProvider.getString(R.string.edit) -> onEditPlayListClick(playlistId)
                            DataProvider.getString(R.string.rename) -> {
                                showRenamePlaylist.value = true

                            }
                            DataProvider.getString(R.string.add_tracks) -> onAddTracksClick(playlistId)
                            DataProvider.getString(R.string.delete_playlist) -> {
                                onDeletePlaylistClick()
                                onEvent(DetailScreenEvent.DeletePlaylist(contentName.value))
                            }
                        }

                    }
                )

            }

            RenamePlaylistDialog(
                allPlaylists = uiState.playlists!!,
                showRenamePlaylist = showRenamePlaylist
            ) {newName ->
                contentName.value = newName
                onEvent(DetailScreenEvent.RenamePlaylist(newPlaylist!!.id, newName))
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