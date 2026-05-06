package com.bearzwayne.musicplayer.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.bearzwayne.musicplayer.feature.detail.R
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.navigation.ContentType
import com.bearzwayne.musicplayer.ui.components.AnimatedGradientBackgroundBox
import com.bearzwayne.musicplayer.ui.components.AnimatedToolBar
import com.bearzwayne.musicplayer.ui.components.BottomScrollableContent
import com.bearzwayne.musicplayer.ui.components.DetailSettingsSheet
import com.bearzwayne.musicplayer.ui.components.RenamePlaylistDialog
import com.bearzwayne.musicplayer.ui.components.TopSectionOverlay
import com.bearzwayne.musicplayer.ui.sharedresources.albumCoverImage
import com.bearzwayne.musicplayer.ui.extensions.generateDominantColorState
import com.bearzwayne.musicplayer.ui.util.isSystemDark
import com.bearzwayne.musicplayer.ui.util.surfaceGradient
import androidx.core.graphics.createBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun DetailScreen(
    contentType: ContentType,
    contentId: Int?,
    contentName: String?,
    onNavigateUp: () -> Unit,
    onAlbumCardClick: (albumId: Int) -> Unit,
    onAddTracksClick: (playlist: Playlist) -> Unit,
    onEditPlayListClick: (playlist: Playlist) -> Unit,
    onDeletePlaylistClick: () -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit
) {
    val detailScreenViewModel: DetailScreenViewModel =
        hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current) {
                    "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
                }, null)
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
    val strDownload = stringResource(R.string.download)
    val strPlayNext = stringResource(R.string.play_next)
    val strAddToQueue = stringResource(R.string.add_to_queue)
    val strEdit = stringResource(R.string.edit)
    val strRename = stringResource(R.string.rename)
    val strAddTracks = stringResource(R.string.add_tracks)
    val strDeletePlaylist = stringResource(R.string.delete_playlist)
    val scrollState = rememberScrollState()
    val showSettings = remember { mutableStateOf(false) }
    val showRenamePlaylist = remember { mutableStateOf(false) }
    var bitmap by remember {
        mutableStateOf<Bitmap?>(
            createBitmap(100, 100).apply { eraseColor(0xFF454343.toInt()) })
    }
    LaunchedEffect(contentUiState) {
        bitmap = albumCoverImage(contentUiState.contentArtworkUri, context)
    }

    bitmap?.let { image ->
        val swatch: Palette.Swatch = image.generateDominantColorState()
            ?: Palette.Swatch(bestOrange.value.toInt(), 1000)
        val dominantColors = listOf(Color(swatch.rgb), MaterialTheme.colorScheme.surface)
        val dominantGradient = remember(swatch) { dominantColors }
        val surfaceGradient = surfaceGradient(isSystemDark(context)).asReversed()

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedGradientBackgroundBox(dominantGradient)

            TopSectionOverlay(
                contentArtworkUri = contentUiState.contentArtworkUri,
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
                onSongListItemClick = { onEvent(DetailScreenEvent.OnSongListItemClick(it)) },
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
                    onDetailMenuItemClick = { menuItem ->
                        showSettings.value = false
                        when (menuItem) {

                            strDownload -> {}
                            strPlayNext -> {
                                onEvent(
                                    DetailScreenEvent.AddSongListNextToCurrentSong(
                                        contentUiState.contentSongList
                                    )
                                )
                            }

                            strAddToQueue -> {
                                onEvent(DetailScreenEvent.AddSongListToQueue(contentUiState.contentSongList))
                            }

                            strEdit -> contentUiState.newPlaylist?.let {
                                onEditPlayListClick(it)
                            }
                            strRename -> {
                                showRenamePlaylist.value = true

                            }

                            strAddTracks -> contentUiState.newPlaylist?.let {
                                onAddTracksClick(it)
                            }

                            strDeletePlaylist -> {
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
        "android.resource://${context.packageName}/${R.drawable.allsongsplaylist}".toUri().toString()
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
        playlists = listOf(
            Playlist(0, "All Tracks", emptyList(), defaultCoverUri),
            Playlist(0, "All Tracks", emptyList(), defaultCoverUri),
            Playlist(0, "All Tracks", emptyList(), defaultCoverUri)
        ),
        playerState = PlayerState.STOPPED,
        selectedSong = testSong,
        selectedPlaylist = Playlist(0, "All Tracks", emptyList(), defaultCoverUri),
        errorMessage = null
    )


    val contentUiState = DetailScreenItemUiState(
        loading = false,
        contentType = ContentType.Playlist,
        contentId = 0,
        contentName = "AllTracks1111111111111111111111111111111111111111111111111111",
        contentDescription = "Tracks: 511111111111111111111111111111111111111111111",
        contentArtworkUri = defaultCoverUri.toUri(),
        contentSongList = listOf(
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong,
            testSong
        ),
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