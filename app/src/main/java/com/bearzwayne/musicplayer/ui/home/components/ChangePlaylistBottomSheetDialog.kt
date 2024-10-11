package com.bearzwayne.musicplayer.ui.home.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.ui.home.HomeViewModel
import com.bearzwayne.musicplayer.ui.library.components.LibraryAdapter
import com.bearzwayne.musicplayer.ui.library.components.LibraryItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangePlaylistBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var libraryAdapter: LibraryAdapter

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_playlist_bottom_sheet, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.playlist_menu_items_recyclerview)
        libraryAdapter = LibraryAdapter(onLibraryItemClick = { content ->
            homeViewModel.changePlaylist(content as Playlist)
            dismiss()
        })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = libraryAdapter


        lifecycleScope.launchWhenStarted {
            homeViewModel.homeUiState.collect { uiState ->
                val items = mutableListOf<LibraryItem>()
                uiState.playlists?.let {

                    items.addAll(it.filter { playlist -> playlist.songList.isNotEmpty() }
                        .map { playlist -> LibraryItem.PlaylistItem(playlist) })
                }
                libraryAdapter.updateItems(items)
            }
        }

    }
}



