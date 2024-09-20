package com.example.musicplayer.ui.details.components

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Playlist

@Composable
fun RenamePlaylistDialog(
    allPlaylists: List<Playlist>,
    showRenamePlaylist: MutableState<Boolean>,
    onOkClick: (newName: String) -> Unit) {
    if (showRenamePlaylist.value) {
        val newName = remember { mutableStateOf("") }
        val colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
        val errorText = stringResource(R.string.playlist_already_exists)
        val errorMessage = remember(newName.value) {
            val allPlaylistNames: List<String> = allPlaylists.map { it.name }

            if (allPlaylistNames.contains(newName.value)) errorText else ""
        }

        AlertDialog(
            shape = RoundedCornerShape(10.dp),
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            onDismissRequest = { showRenamePlaylist.value = false },
            title = { Text(text = stringResource(R.string.enter_playlist_name)) },
            text = {
                Column {
                    TextField(
                        value = newName.value,
                        onValueChange = {
                            newName.value = it
                        },
                        label = { Text(stringResource(R.string.playlist_name)) },
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
                            onOkClick(newName.value)
                            newName.value =""
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
                        showRenamePlaylist.value = false
                        newName.value = ""
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}