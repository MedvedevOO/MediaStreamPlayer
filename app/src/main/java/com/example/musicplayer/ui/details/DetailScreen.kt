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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys
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
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.ui.theme.extensions.generateDominantColorState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun DetailScreen(
    contentType: String,
    contentId: Int?,
    contentName: String?,
    onNavigateUp: () -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlist: Playlist) -> Unit,
    onEditPlayListClick: (playlist: Playlist) -> Unit,
    onDeletePlaylistClick: () -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val detailScreenViewModel: DetailScreenViewModel = hiltViewModel()
    val onEvent = detailScreenViewModel::onEvent

    LaunchedEffect(contentId, contentType) {
        onEvent(DetailScreenEvent.SetDetailScreenItem(contentId,contentName, contentType))
    }
    DetailScreenBody(
        uiState = detailScreenViewModel.detailScreenUiState,
        contentUiState = detailScreenViewModel.detailScreenItemUiState,
        onEvent = detailScreenViewModel::onEvent,
        onNavigateUp = onNavigateUp,
        onAlbumCardClick = onAlbumCardClick,
        onAddTracksClick = onAddTracksClick,
        onEditPlayListClick = onEditPlayListClick,
        onDeletePlaylistClick = onDeletePlaylistClick,
        onSongListItemSettingsClick = onSongListItemSettingsClick
    )
}

@Composable
fun DetailScreenBody(
    uiState: DetailScreenUiState,
    contentUiState: DetailScreenItemUiState,
    onEvent: (DetailScreenEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlist: Playlist) -> Unit,
    onEditPlayListClick: (playlist: Playlist) -> Unit,
    onDeletePlaylistClick: () -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val showSettings = remember { mutableStateOf(false) }
    val showRenamePlaylist = remember { mutableStateOf(false) }

    // Fetch the dynamic image based on the imageUri
    var bitmap by remember {
        mutableStateOf<Bitmap?>(
            Bitmap.createBitmap(
                100,
                100,
                Bitmap.Config.ARGB_8888
            ).apply { eraseColor(0xFF454343.toInt()) })
    }
    LaunchedEffect(contentUiState) {
        bitmap = albumCoverImage(contentUiState.contentArtworkUri, context)
    }

    bitmap?.let { image ->
        val swatch = image.generateDominantColorState()
        val dominantColors = listOf(Color(swatch.rgb), MaterialTheme.colorScheme.surface)
        val dominantGradient = remember(swatch) { dominantColors }
        // Define gradients and swatches based on the content being displayed
        val surfaceGradient =
            DataProvider.surfaceGradient(SettingsKeys.isSystemDark(context)).asReversed()

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedGradientBackgroundBox(dominantGradient)

            TopSectionOverlay(
                contentName = contentUiState.contentName,
                contentArtworkUri = contentUiState.contentArtworkUri,
                description = contentUiState.contentDescription,
                scrollState = scrollState,
                gradient = surfaceGradient
            )

            BottomScrollableContent(
                uiState = uiState,
                contentName = contentUiState.contentName,
                description = contentUiState.contentDescription,
                songList = contentUiState.contentSongList.toMutableStateList(),
                albumsList = contentUiState.contentAlbumsList,
                scrollState = scrollState,
                showSettings = showSettings,
                onSongListItemClick = { onEvent(DetailScreenEvent.onSongListItemClick(it)) },
                onSongListItemLikeClick = { onEvent(DetailScreenEvent.OnSongLikeClick(it)) },
                onPlayButtonClick = { onEvent(DetailScreenEvent.OnPlayButtonClick) },
                onShuffleClick = { onEvent(DetailScreenEvent.ShufflePlay) },
                onAlbumCardClick = onAlbumCardClick,
                onAddTracksClick = onAddTracksClick,
                onSongListItemSettingsClick = onSongListItemSettingsClick
            )
            AnimatedToolBar(
                contentName = contentUiState.contentName,
                selectedPlaylist = uiState.selectedPlaylist!!,
                playerState = uiState.playerState,
                scrollState = scrollState,
                surfaceGradient = surfaceGradient,
                onNavigateUp = onNavigateUp,
                onPlayButtonClick = { onEvent(DetailScreenEvent.OnPlayButtonClick) }
            )




            if (showSettings.value) {
                DetailSettingsSheet(
                    contentUiState = contentUiState,
                    onDismiss = { showSettings.value = false },
                    onDetailMenuItemClick = { menuItem, playlistId ->
                        showSettings.value = false
                        when (menuItem) {

                            DataProvider.getString(R.string.download) -> {}
                            DataProvider.getString(R.string.play_next) -> {
                                onEvent(
                                    DetailScreenEvent.AddSongListNextToCurrentSong(
                                        contentUiState.contentSongList
                                    )
                                )
                            }

                            DataProvider.getString(R.string.add_to_queue) -> {
                                onEvent(DetailScreenEvent.AddSongListToQueue(contentUiState.contentSongList))
                            }

                            DataProvider.getString(R.string.edit) -> contentUiState.newPlaylist?.let {
                                onEditPlayListClick(it)
                            }
                            DataProvider.getString(R.string.rename) -> {
                                showRenamePlaylist.value = true

                            }

                            DataProvider.getString(R.string.add_tracks) -> contentUiState.newPlaylist?.let {
                                onAddTracksClick(it)
                            }

                            DataProvider.getString(R.string.delete_playlist) -> {
                                onDeletePlaylistClick()
                                onEvent(DetailScreenEvent.DeletePlaylist(contentUiState.contentName))
                            }
                        }

                    }
                )

            }

            RenamePlaylistDialog(
                allPlaylists = uiState.playlists!!,
                showRenamePlaylist = showRenamePlaylist
            ) { newName ->
                onEvent(DetailScreenEvent.RenamePlaylist(contentUiState.contentId, newName))
                showRenamePlaylist.value = false
            }
        }

    }
}

@Preview
@Composable
fun PreviewDetailScreen() {
    val context = LocalContext.current
    val defaultCoverUri =
        Uri.parse("android.resource://${context.packageName}/${R.drawable.allsongsplaylist}").toString()
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

    val uiState = DetailScreenUiState(
        loading = false,
        playlists = listOf(Playlist(0,"All Tracks", emptyList(), defaultCoverUri),Playlist(0,"All Tracks", emptyList(), defaultCoverUri),Playlist(0,"All Tracks", emptyList(), defaultCoverUri)),
        playerState = PlayerState.STOPPED,
        selectedSong = testSong,
        selectedPlaylist = Playlist(0,"All Tracks", emptyList(), defaultCoverUri),
        errorMessage = null
    )


    val contentUiState = DetailScreenItemUiState(
        loading = false,
        contentType = "playlist",
        contentId = 0,
        contentName = "AllTracks1111111111111111111111111111111111111111111111111111",
        contentDescription = "Tracks: 511111111111111111111111111111111111111111111",
        contentArtworkUri = defaultCoverUri.toUri(),
        contentSongList = listOf(testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong,testSong),
        contentAlbumsList = emptyList(),
        newPlaylist = null,
        errorMessage = null

    )

    MusicPlayerTheme {
        DetailScreenBody(
            uiState = uiState,
            contentUiState =contentUiState,
            onEvent = {},
            onNavigateUp = {},
            onAlbumCardClick ={},
            onAddTracksClick ={},
            onEditPlayListClick ={},
            onDeletePlaylistClick = {},
            onSongListItemSettingsClick ={}
        )
    }

}