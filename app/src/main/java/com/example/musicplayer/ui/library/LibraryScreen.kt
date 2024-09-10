package com.example.musicplayer.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys.isSystemDark
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState
import com.example.musicplayer.ui.library.components.LibraryHorizontalAddPlaylistItem
import com.example.musicplayer.ui.library.components.LibraryHorizontalCardItem
import com.example.musicplayer.ui.library.components.Title
import com.example.musicplayer.ui.settings.AppSettingsSheet
import com.example.musicplayer.ui.sharedresources.TopPageBar
import com.example.musicplayer.ui.theme.modifiers.verticalGradientBackground

@Composable
fun LibraryScreen(
    homeUiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onLibraryItemClick : (content: Any) -> Unit
) {

    val context = LocalContext.current
    val surfaceGradient = DataProvider.surfaceGradient(isSystemDark(context)).asReversed()
    val showDialog = remember { mutableStateOf(false) }
    val showAppSettings = remember { mutableStateOf(false) }
    val recentlyAddedName = DataProvider.getRecentlyAddedName()
    Column(modifier = Modifier
        .verticalGradientBackground(surfaceGradient)
        .fillMaxSize()) {
        TopPageBar(pageName = R.string.your_collection, showAppSettings = showAppSettings)
        LazyColumn {
            // use `item` for separate elements like headers
            // and `items` for lists of identical elements
            item { Title(text = stringResource(R.string.playlists)) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { LibraryHorizontalAddPlaylistItem(onItemClicked = {showDialog.value = true}) }

            homeUiState.playlists!!.forEach {
                if (it.name != recentlyAddedName) {
                    item { LibraryHorizontalCardItem(content = it,onClick = onLibraryItemClick) }
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            if (!homeUiState.songs.isNullOrEmpty()) {
                item { Title(text = stringResource(R.string.artists)) }
                itemsIndexed(homeUiState.artists!!) { _, item ->
                    LibraryHorizontalCardItem(content = item, onClick = onLibraryItemClick)
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
                item { Title(text = stringResource(R.string.albums)) }
                itemsIndexed(homeUiState.albums!!) { _, item ->
                    LibraryHorizontalCardItem(content = item, onClick = onLibraryItemClick)
                }
            }
            item{Spacer(modifier = Modifier.height(100.dp))}
        }
    }

    if (showDialog.value) {
        AddNewPlaylistDialog(
            onOkClicked = {
                onEvent(HomeEvent.AddNewPlaylist(it))
            },
            showDialog = showDialog,
            homeUiState = homeUiState
        )
    }

    if (showAppSettings.value) {
        AppSettingsSheet(showAppSettings)
    }
}

@Composable
fun AddNewPlaylistDialog(
    onOkClicked: (newPlaylist: Playlist) -> Unit,
    showDialog: MutableState<Boolean>,
    homeUiState: HomeUiState
) {
    val newPlaylistName = remember { mutableStateOf("") }
    val errorText = stringResource(R.string.playlist_already_exists)
    val alertDialogTitle = stringResource(R.string.enter_playlist_name)
    val textFieldLabel = stringResource(R.string.playlist_name)
    val cancelText = stringResource(R.string.cancel)
    val errorMessage = remember(newPlaylistName.value) {
        val allPlaylistNames: List<String> = homeUiState.playlists!!.map { it.name }

        if (allPlaylistNames.contains(newPlaylistName.value)) errorText else ""
    }

    val colors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )

    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
        onDismissRequest = { showDialog.value = false },
        title = { Text(text = alertDialogTitle) },
        text = {
            Column {
                TextField(
                    value = newPlaylistName.value,
                    onValueChange = {
                        newPlaylistName.value = it
                        // No need to update error message here; it's already being observed
                    },
                    label = { Text(textFieldLabel) },
                    isError = errorMessage.isNotEmpty(),
                    supportingText = {Text(errorMessage)}
                )
            }
        },
        textContentColor = MaterialTheme.colorScheme.onSurface,
        confirmButton = {


            Button(
                colors = colors,
                onClick = {
                    if (errorMessage.isEmpty()) {
                        showDialog.value = false
                        val newPlaylist = Playlist(
                            homeUiState.playlists!!.size,
                            newPlaylistName.value,
                            mutableStateListOf(),
                            DataProvider.getDefaultCover()
                        )
                        // Reset the text field for next use
                        newPlaylistName.value = ""
                        onOkClicked(newPlaylist)
                    }
                },
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "OK", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        dismissButton = {
            Button(
                colors = colors,
                onClick = {
                    showDialog.value = false
                    newPlaylistName.value = "" // Reset the text field for next use
                },
                shape = RoundedCornerShape(5.dp)
            ) {

                Text(text = cancelText, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}




