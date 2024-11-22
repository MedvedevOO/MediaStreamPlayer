package com.bearzwayne.musicplayer.ui.editplaylist

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
import androidx.navigation.NavController
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.editplaylist.components.DragAndDropList
import com.bearzwayne.musicplayer.ui.editplaylist.components.EditPlaylistTopBar
import com.bearzwayne.musicplayer.ui.editplaylist.components.move
import com.bearzwayne.musicplayer.ui.theme.util.isSystemDark
import com.bearzwayne.musicplayer.ui.theme.util.surfaceGradient
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModelEvent

@Composable
fun EditPlaylistScreen(
    playlist: Playlist,
    navController: NavController,
    onEvent: (SharedViewModelEvent) -> Unit
) {
    val context = LocalContext.current
    val surfaceGradient = surfaceGradient(isSystemDark(context)).asReversed()
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
                            editedSongList[0].imageUrl
                        } else {
                            DataProvider.getDefaultCover().toString()
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



