package com.bearzwayne.musicplayer.ui.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.library.components.LibraryAdapter
import com.bearzwayne.musicplayer.ui.library.components.LibraryItem
import com.bearzwayne.musicplayer.ui.navigation.Detail
import com.bearzwayne.musicplayer.ui.navigation.Home
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val libraryScreenViewModel: LibraryScreenViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var libraryAdapter: LibraryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        recyclerView = view.findViewById(R.id.library_recyclerview)

        // Initialize RecyclerView Adapter
        libraryAdapter = LibraryAdapter(onLibraryItemClick = {
            var type = ""
            var id = 0
            when (it) {
                is Album -> {
                    type = "album"
                    id = it.id.toInt()
                }

                is Artist -> {
                    type = "artist"
                    id = it.id
                }

                is Playlist -> {
                    type = "playlist"
                    id = it.id
                }

                else -> {}
            }
            navController.navigate(
                Detail(
                    type = type,
                    id = id
                )
            )
        },
            onAddPlaylistClick = {
                lifecycleScope.launch {
                    showAddPlaylistDialog(
                        libraryScreenViewModel.libraryScreenUiState.value.playlists?.size ?: 0,
                        libraryScreenViewModel.libraryScreenUiState.value.playlists ?: emptyList()
                    )
                }
            })
        recyclerView.adapter = libraryAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the UI state from the ViewModel and update RecyclerView
        lifecycleScope.launchWhenStarted {
            libraryScreenViewModel.libraryScreenUiState.collect { uiState ->
                updateUi(uiState)
            }
        }
    }

    private fun updateUi(uiState: LibraryScreenUiState) {
        // Update RecyclerView with playlists, artists, albums, etc.
        val items = mutableListOf<LibraryItem>()

        // Add Playlists
        uiState.playlists?.let {
            items.add(LibraryItem.Header(getString(R.string.playlists)))
            items.add(LibraryItem.AddPlaylist)
            items.addAll(it.filter { playlist -> playlist.name != DataProvider.getRecentlyAddedName() }
                .map { playlist -> LibraryItem.PlaylistItem(playlist) })
        }

        // Add Artists if available
        uiState.artists?.let {
            if (it.isNotEmpty()) {
                items.add(LibraryItem.Header(getString(R.string.artists)))
                items.addAll(it.map { artist -> LibraryItem.ArtistItem(artist) })
            }
        }

        // Add Albums if available
        uiState.albums?.let {
            if (it.isNotEmpty()) {
                items.add(LibraryItem.Header(getString(R.string.albums)))
                items.addAll(it.map { album -> LibraryItem.AlbumItem(album) })
            }
        }

        libraryAdapter.updateItems(items)
    }

    private fun showAddPlaylistDialog(playlistId: Int, playLists: List<Playlist>) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_playlist, null)
        val editTextPlaylistName = dialogView.findViewById<EditText>(R.id.edit_playlist_name)
        val errorMessageView = dialogView.findViewById<TextView>(R.id.text_error_message)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(getString(R.string.create_playlist))
            .setPositiveButton("OK", null)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss() // Close the dialog on cancel
            }
            .create()

        dialog.setOnShowListener {
            val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val playlistName = editTextPlaylistName.text.toString()
                val playlistNames = playLists.map { it.name }
                if (playlistName.isBlank()) {
                    // Show error message if the playlist name is empty
                    errorMessageView.text = getString(R.string.enter_playlist_name)
                    errorMessageView.visibility = View.VISIBLE
                } else if(playlistNames.contains(playlistName)) {
                    errorMessageView.text = getString(R.string.playlist_already_exists)
                    errorMessageView.visibility = View.VISIBLE
                } else {
                    // Reset error message
                    errorMessageView.visibility = View.GONE

                    // Handle adding new playlist logic here
                    val newPlaylist = Playlist(
                        id = playlistId,
                        name = playlistName,
                        songList = emptyList(),
                        artWork = DataProvider.getDefaultCover().toString() // Set default cover
                    )

                    // Call your event handler
                    libraryScreenViewModel.newPlaylist(newPlaylist)

                    // Close the dialog
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

}