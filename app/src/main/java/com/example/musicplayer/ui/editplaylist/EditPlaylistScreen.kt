package com.example.musicplayer.ui.editplaylist

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.ui.editplaylist.components.DragAndDropList
import com.example.musicplayer.ui.editplaylist.components.EditPlaylistTopBar
import com.example.musicplayer.ui.editplaylist.components.move
import com.example.musicplayer.ui.viewmodels.SharedViewModelEvent

@Composable
fun EditPlaylistScreen(
    playlist: Playlist,
    navController: NavController,
    onEvent: (SharedViewModelEvent) -> Unit
) {
    val context = LocalContext.current
    val surfaceGradient = DataProvider.surfaceGradient(SettingsKeys.isSystemDark(context)).asReversed()
    val editedSongList = remember {
        SnapshotStateList<Song>().apply { addAll(playlist.songList) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = surfaceGradient
                )
            )
    ) {
        Column {
            EditPlaylistTopBar(
                onBackClick = {navController.navigateUp()},
                onOkClick = {
                    if (playlist.id > 2) {
                        playlist.artWork = if (editedSongList.size > 0) {
                            editedSongList[0].imageUrl.toUri()
                        } else {
                            DataProvider.getDefaultCover()
                        }
                    }


                    playlist.songList = editedSongList
                    onEvent(SharedViewModelEvent.AddNewPlaylist(playlist))
                    navController.navigateUp()

                }
            )



            DragAndDropList(editedSongList = editedSongList, onMove = { fromIndex, toIndex ->
                Log.d("EditPlaylistScreen", "Moving item from $fromIndex to $toIndex")
                editedSongList.move(fromIndex, toIndex)
            })
        }
    }
}



