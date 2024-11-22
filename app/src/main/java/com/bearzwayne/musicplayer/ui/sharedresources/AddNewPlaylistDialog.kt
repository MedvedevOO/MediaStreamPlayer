package com.bearzwayne.musicplayer.ui.sharedresources

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist

@Composable
fun AddNewPlaylistDialog(
    onOkClicked: (newPlaylist: Playlist) -> Unit,
    showDialog: MutableState<Boolean>,
    allPlaylistNames: List<String>
) {
    val newPlaylistName = remember { mutableStateOf("") }
    val errorText = stringResource(R.string.playlist_already_exists)
    val alertDialogTitle = stringResource(R.string.enter_playlist_name)
    val textFieldLabel = stringResource(R.string.playlist_name)
    val cancelText = stringResource(R.string.cancel)
    val errorMessage = remember(newPlaylistName.value) {
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
                    },
                    label = { Text(textFieldLabel) },
                    isError = errorMessage.isNotEmpty(),
                    supportingText = { Text(errorMessage) }
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
                            allPlaylistNames.size,
                            newPlaylistName.value,
                            mutableStateListOf(),
                            DataProvider.getDefaultCover().toString()
                        )
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
                    newPlaylistName.value = ""
                },
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = cancelText, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}