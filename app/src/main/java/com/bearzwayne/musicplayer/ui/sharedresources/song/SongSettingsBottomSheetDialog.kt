package com.bearzwayne.musicplayer.ui.sharedresources.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.toRoute
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.model.menu.MenuItemData
import com.bearzwayne.musicplayer.ui.navigation.AddSongToPlaylist
import com.bearzwayne.musicplayer.ui.navigation.Detail
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class SongSettingsBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var song: Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = findNavController().currentBackStackEntry?.toRoute<SongSettings>()
        // Retrieve the argument passed to the dialog
        if (args != null) {
            song = args.song
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.song_settings_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedViewModel: SharedViewModel by activityViewModels()
        val navController = findNavController()
        val menuItems = mutableListOf(
            MenuItemData(getString(R.string.add_to_playlist_variant), R.drawable.ic_add),
            MenuItemData(getString(R.string.play_next), R.drawable.ic_queue_music),
            MenuItemData(getString(R.string.add_to_queue), R.drawable.ic_playlist_add),
            MenuItemData(getString(R.string.go_to_artist), R.drawable.ic_person),
            MenuItemData(getString(R.string.go_to_album), R.drawable.ic_album)
        )

        val songImageView: ImageView = requireView().findViewById(R.id.item_image)
        val songTitle: TextView? = requireView().findViewById(R.id.top_card_text)
        val songArist: TextView? = requireView().findViewById(R.id.bottom_card_text)

        Glide.with(requireContext())
            .load(song.imageUrl)
            .placeholder(R.drawable.stocksongcover) // Fallback image
            .into(songImageView)
        songTitle?.text = song.title
        songArist?.text = song.artist


        // Logic to remove specific items
        if (sharedViewModel.musicControllerUiState.value.selectedPlaylist?.songList?.contains(song) == true) {
            menuItems.removeIf { it.title == getString(R.string.add_to_queue) }
        }
        if (song.songUrl == sharedViewModel.musicControllerUiState.value.currentSong?.songUrl) {
            menuItems.removeIf { it.title == getString(R.string.play_next) }
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.song_menu_items_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MenuItemsAdapter(menuItems) { menuItem: MenuItemData ->
            when (menuItem.title) {
                requireContext().getString(R.string.download) -> {}
                requireContext().getString(R.string.add_to_playlist_variant) -> {
                    lifecycleScope.launch {
                            navController.popBackStack()
                            navController.navigate(AddSongToPlaylist(song))
                    }
                }

                requireContext().getString(R.string.add_to_queue) -> sharedViewModel.addSongsToQueue(listOf(song))

                requireContext().getString(R.string.play_next) -> sharedViewModel.addSongNextToCurrent(song)

                requireContext().getString(R.string.go_to_artist) -> {
                    lifecycleScope.launch {
                        navController.popBackStack()
                        navController.navigate(
                            Detail(
                                type = "artist",
                                name = song.artist
                            )
                        )
                    }

                }

                requireContext().getString(R.string.go_to_album) -> {
                    lifecycleScope.launch {
                        navController.popBackStack()
                        navController.navigate(
                            Detail(
                                type = "album",
                                name = song.album
                            )
                        )
                    }
                }

            }
            dismiss()


        }
    }
}



