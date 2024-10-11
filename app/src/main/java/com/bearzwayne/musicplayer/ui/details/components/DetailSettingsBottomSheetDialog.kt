package com.bearzwayne.musicplayer.ui.details.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.domain.model.menu.MenuItemData
import com.bearzwayne.musicplayer.ui.details.DetailScreenItemUiState
import com.bearzwayne.musicplayer.ui.details.DetailScreenViewModel
import com.bearzwayne.musicplayer.ui.navigation.AddSongs
import com.bearzwayne.musicplayer.ui.navigation.EditPlaylist
import com.bearzwayne.musicplayer.ui.sharedresources.song.MenuItemsAdapter
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class DetailSettingsBottomSheetDialog : BottomSheetDialogFragment() {

    private val detailScreenViewModel: DetailScreenViewModel by activityViewModels()

    private lateinit var contentUiState: DetailScreenItemUiState

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.song_settings_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        contentUiState = detailScreenViewModel.detailScreenItemUiState.value
        lifecycleScope.launchWhenStarted {
            detailScreenViewModel.detailScreenItemUiState.collect { uiState ->
                contentUiState = uiState
            }
        }
        val menuItems = when (contentUiState.contentType) {

            "playlist" -> {
                mutableListOf(
                    MenuItemData(getString(R.string.play_next), R.drawable.ic_queue_music),
                    MenuItemData(getString(R.string.add_to_queue), R.drawable.ic_playlist_add),
                    MenuItemData(getString(R.string.edit), R.drawable.ic_edit)
                ).apply {
                    when (contentUiState.contentId) {
                        0 -> {
                        }

                        1, 2 -> {}
                        else -> {
                            add(MenuItemData(getString(R.string.rename), R.drawable.ic_edit_square))
                            add(
                                MenuItemData(
                                    getString(R.string.add_tracks),
                                    R.drawable.ic_playlist_add
                                )
                            )
                            add(
                                MenuItemData(
                                    getString(R.string.delete_playlist),
                                    R.drawable.ic_delete
                                )
                            )
                        }
                    }
                    if (contentUiState.contentSongList.isEmpty()) {
                        remove(MenuItemData(getString(R.string.edit), R.drawable.ic_edit))
                    }
                }

            }

            else -> {
                mutableListOf(
                    MenuItemData(getString(R.string.play_next), R.drawable.ic_queue_music),
                    MenuItemData(getString(R.string.add_to_queue), R.drawable.ic_playlist_add),
                )
            }
        }

        val itemImageView: ImageView = requireView().findViewById(R.id.item_image)
        val itemName: TextView? = requireView().findViewById(R.id.top_card_text)
        val itemDescription: TextView? = requireView().findViewById(R.id.bottom_card_text)

        Glide.with(requireContext())
            .load(contentUiState.contentArtworkUri)
            .placeholder(R.drawable.stocksongcover) // Fallback image
            .into(itemImageView)
        itemName?.text = contentUiState.contentName
        itemDescription?.text = contentUiState.contentDescription


        val recyclerView: RecyclerView = view.findViewById(R.id.song_menu_items_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MenuItemsAdapter(menuItems) { menuItem ->
            when (menuItem.title) {

                DataProvider.getString(R.string.download) -> {}
                DataProvider.getString(R.string.play_next) -> { detailScreenViewModel.addSongListNextToCurrentSong(contentUiState.contentSongList) }
                DataProvider.getString(R.string.add_to_queue) -> { detailScreenViewModel.addSongsToQueue(contentUiState.contentSongList) }
                DataProvider.getString(R.string.edit) -> contentUiState.newPlaylist?.let { playlist ->
                    navController.navigate(
                        EditPlaylist(
                            playlist = playlist
                        )
                    )
                }
                DataProvider.getString(R.string.rename) -> lifecycleScope.launch {
                    navController.popBackStack()
                    showRenamePlaylistDialog(contentUiState.contentId)
                }


                DataProvider.getString(R.string.add_tracks) -> contentUiState.newPlaylist?.let { playlist ->
                    navController.navigate(
                        AddSongs(
                            playlist = playlist
                        )
                    )
                }

                DataProvider.getString(R.string.delete_playlist) -> {
                    lifecycleScope.launch {
                        navController.popBackStack()
                        navController.popBackStack()
                        detailScreenViewModel.deletePlaylist(contentUiState.contentName)
                    }
                }
            }

        }
    }

    private fun showRenamePlaylistDialog(playlistId: Int) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_playlist, null)
        val editTextPlaylistName = dialogView.findViewById<EditText>(R.id.edit_playlist_name)
        val errorMessageView = dialogView.findViewById<TextView>(R.id.text_error_message)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(getString(R.string.rename))
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
                    detailScreenViewModel.renamePlaylist(playlistId, playlistName)
                    detailScreenViewModel.setDetailScreenItem(playlistId, playlistName, "playlist")
                    // Close the dialog
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}



