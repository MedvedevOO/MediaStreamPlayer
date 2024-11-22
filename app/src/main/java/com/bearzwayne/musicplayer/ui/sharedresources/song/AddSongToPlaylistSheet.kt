package com.bearzwayne.musicplayer.ui.sharedresources.song

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.library.components.LibraryHorizontalAddPlaylistItem
import com.bearzwayne.musicplayer.ui.library.components.LibraryHorizontalCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongToPlaylistSheet(
    playlists: List<Playlist>,
    songSettingsItem: Song,
    onDismissRequest: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistToAddSongChosen: (updatedPlaylist: Playlist) -> Unit
) {
    val context = LocalContext.current
    val favoritesName = context.getString(R.string.favorites)
    val allTracksName = context.getString(R.string.all_tracks)
    val recentlyAddedName = context.getString(R.string.recently_added)

    val toastErrorContent = stringResource(id = R.string.track_already_added_to_that_playlist)
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismissRequest,
    ) {
        LazyColumn {
            item { LibraryHorizontalAddPlaylistItem(onItemClicked = onCreatePlaylistClick) }
            itemsIndexed(
                playlists.toList()) { _, item->
                if (
                    item.name != allTracksName &&
                    item.name != recentlyAddedName &&
                    item.name != favoritesName

                ) {
                    LibraryHorizontalCardItem(item) {

                        if (!item.songList.contains(songSettingsItem)){
                            val resultSongList = item.songList.toMutableList().apply { add(songSettingsItem) }
                            val playlistName = item.name
                            val updatedPlaylist = item.copy(
                                songList = resultSongList
                            )
                            onPlaylistToAddSongChosen(updatedPlaylist)
                            val message = context.getString(R.string.track_added_to_playlist, playlistName)
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()

                        } else {

                            Toast.makeText(context,toastErrorContent, Toast.LENGTH_SHORT).show()
                        }

                        onDismissRequest()
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}