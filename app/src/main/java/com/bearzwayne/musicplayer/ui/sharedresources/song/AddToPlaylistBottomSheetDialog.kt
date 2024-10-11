package com.bearzwayne.musicplayer.ui.sharedresources.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.toRoute
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.home.HomeViewModel
import com.bearzwayne.musicplayer.ui.library.components.LibraryAdapter
import com.bearzwayne.musicplayer.ui.library.components.LibraryItem
import com.bearzwayne.musicplayer.ui.navigation.AddSongToPlaylist
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class AddToPlaylistBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addToPlaylistLayout: LinearLayout
    private lateinit var libraryAdapter: LibraryAdapter
    private lateinit var song: Song

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_to_playlist_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val args = navController.currentBackStackEntry?.toRoute<AddSongToPlaylist>()
        // Retrieve the argument passed to the dialog
        if (args != null) {
            song = args.song
        }
        val toastErrorContent = requireContext().getString(R.string.track_already_added_to_that_playlist)
        addToPlaylistLayout = view.findViewById(R.id.add_to_playlist)

        addToPlaylistLayout.setOnClickListener {
            lifecycleScope.launch {
                navController.popBackStack()
                showAddPlaylistDialog(song, sharedViewModel.musicControllerUiState.value.playlists.size)
            }

        }
        recyclerView = view.findViewById(R.id.add_to_playlist_recyclerview)
        libraryAdapter = LibraryAdapter(onLibraryItemClick = { content ->
            val item = content as Playlist
            if (!item.songList.contains(song)){
                val resultSongList = item.songList.toMutableList().apply { add(song) }
                val playlistName = item.name  // Assuming this is the playlist name
                val updatedPlaylist = item.copy(
                    songList = resultSongList
                )
                sharedViewModel.addNewPlaylist(updatedPlaylist)
                val message = requireContext().getString(R.string.track_added_to_playlist, playlistName)
                Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context,toastErrorContent, Toast.LENGTH_SHORT).show()
            }
            dismiss()
        })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = libraryAdapter


        lifecycleScope.launchWhenStarted {
            homeViewModel.homeUiState.collect { uiState ->
                val items = mutableListOf<LibraryItem>()
                uiState.playlists?.let {

                    items.addAll(it.filter { playlist ->
                        playlist.name != requireContext().getString(R.string.recently_added)
                                && playlist.name != DataProvider.getString(R.string.all_tracks)
                    }
                        .map { playlist -> LibraryItem.PlaylistItem(playlist) })
                }
                libraryAdapter.updateItems(items)
            }
        }

    }
    private fun showAddPlaylistDialog(songToAdd: Song, playlistId: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_playlist, null)
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

                if (playlistName.isBlank()) {
                    // Show error message if the playlist name is empty
                    errorMessageView.text = getString(R.string.playlist_name)
                    errorMessageView.visibility = View.VISIBLE
                } else {
                    // Reset error message
                    errorMessageView.visibility = View.GONE

                    // Handle adding new playlist logic here
                    val newPlaylist = Playlist(
                        id = playlistId,
                        name = playlistName,
                        songList = listOf(songToAdd),
                        artWork = songToAdd.imageUrl // Set default cover
                    )

                    // Call your event handler
                    sharedViewModel.addNewPlaylist(newPlaylist)

                    // Close the dialog
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}



