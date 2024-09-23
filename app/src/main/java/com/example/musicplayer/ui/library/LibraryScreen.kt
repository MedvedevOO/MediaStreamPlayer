package com.example.musicplayer.ui.library

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.library.components.LibraryHorizontalAddPlaylistItem
import com.example.musicplayer.ui.library.components.LibraryHorizontalCardItem
import com.example.musicplayer.ui.library.components.Title
import com.example.musicplayer.ui.sharedresources.AddNewPlaylistDialog
import com.example.musicplayer.ui.sharedresources.MusicPlayerScreenAnimatedBackground
import com.example.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun LibraryScreen(
    onLibraryItemClick: (content: Any) -> Unit
) {
    val libraryScreenViewModel: LibraryScreenViewModel = hiltViewModel()

    LibraryScreenBody(
        uiState = libraryScreenViewModel.libraryScreenUiState,
        onEvent = libraryScreenViewModel::onEvent,
        onLibraryItemClick = onLibraryItemClick
    )

}

@Composable
fun LibraryScreenBody(
    uiState: LibraryScreenUiState,
    onEvent: (LibraryScreenEvent) -> Unit,
    onLibraryItemClick: (content: Any) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }
    val recentlyAddedName = DataProvider.getRecentlyAddedName()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Title(text = stringResource(R.string.playlists)) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { LibraryHorizontalAddPlaylistItem(onItemClicked = { showDialog.value = true }) }

            uiState.playlists!!.forEach {
                if (it.name != recentlyAddedName) {
                    item { LibraryHorizontalCardItem(content = it, onClick = onLibraryItemClick) }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            if (!uiState.artists.isNullOrEmpty()) {
                item { Title(text = stringResource(R.string.artists)) }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                itemsIndexed(uiState.artists) { _, item ->
                    LibraryHorizontalCardItem(content = item, onClick = onLibraryItemClick)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
            if (!uiState.albums.isNullOrEmpty()) {
                item { Title(text = stringResource(R.string.albums)) }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                itemsIndexed(uiState.albums) { _, item ->
                    LibraryHorizontalCardItem(content = item, onClick = onLibraryItemClick)
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    if (showDialog.value) {
        AddNewPlaylistDialog(
            onOkClicked = { onEvent(LibraryScreenEvent.AddNewPlaylist(it)) },
            showDialog = showDialog,
            allPlaylistNames = uiState.playlists!!.map { it.name }
        )
    }
}

@Preview
@Composable
fun LibraryScreenBodyPreview() {
    val context = LocalContext.current
    DataProvider.init(context)
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

    val testArtist = Artist(
        id = 0,
        name = "Artist",
        photo = defaultCoverUri,
        genre = "",
        albumList = emptyList(),
        songList = emptyList()
    )

    val testAlbum = Album (
        id = 0,
        name = "Album",
        artist = "Artist",
        genre = "",
        year = "2022",
        songList = emptyList(),
        albumCover = defaultCoverUri
    )

    val uiState = LibraryScreenUiState(
        playlists = listOf(Playlist(0,"All Tracks", emptyList(), defaultCoverUri),Playlist(0,"All Tracks", emptyList(), defaultCoverUri),Playlist(0,"All Tracks", emptyList(), defaultCoverUri)),
        artists = listOf(testArtist,testArtist,testArtist,testArtist),
        albums = listOf(testAlbum,testAlbum,testAlbum,testAlbum)
    )
    MusicPlayerTheme {
        Box(modifier = Modifier.fillMaxSize()){
            MusicPlayerScreenAnimatedBackground(
                currentSong = testSong,
                playerState = PlayerState.STOPPED
            )
        }
        LibraryScreenBody(uiState = uiState, onEvent = {}, onLibraryItemClick = {})
    }
}



