package com.bearzwayne.musicplayer.ui.addsongstoplaylist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.addsongstoplaylist.components.AddSongItemAdapter
import com.bearzwayne.musicplayer.ui.details.DetailScreenItemUiState
import com.bearzwayne.musicplayer.ui.details.DetailScreenViewModel
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.sharedresources.SpacingItemDecoration
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongListAdapter
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSongsFragment : Fragment(R.layout.fragment_add_songs) {

    private lateinit var addSongItemAdapter: AddSongItemAdapter
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailScreenItemUiState: DetailScreenItemUiState
    private lateinit var songList: MutableList<Song>

    private val detailScreenViewModel: DetailScreenViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.back_button)
        recyclerView = view.findViewById(R.id.song_list_recyclerview)
        detailScreenItemUiState = detailScreenViewModel.detailScreenItemUiState.value
        songList = detailScreenViewModel.detailScreenItemUiState.value.contentSongList.toMutableList()

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        // Initialize RecyclerView
        addSongItemAdapter = AddSongItemAdapter(
            songs = emptyList(),
            onAddSongButtonClick = {song ->
                songList.add(song)
                val playlist = Playlist(
                    id = detailScreenItemUiState.contentId,
                    name = detailScreenItemUiState.contentName,
                    songList = songList,
                    artWork = detailScreenItemUiState.contentArtworkUri.toString()
                )
                sharedViewModel.addNewPlaylist(playlist)
                detailScreenViewModel.setDetailScreenItem(detailScreenItemUiState.contentId, detailScreenItemUiState.contentName, "playlist")
                updateUi(songList)
            },
        )
        recyclerView.adapter = addSongItemAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpacingItemDecoration(16))
        // Observe the UI state from the ViewModel
        lifecycleScope.launchWhenStarted {
            detailScreenViewModel.detailScreenUiState.collect{uiState->

                uiState.playlists?.get(detailScreenItemUiState.contentId)?.songList?.let {
                    updateUi(
                        it
                    )
                }
            }
        }
    }

    private fun updateUi(contentSongList: List<Song>) {
        // Update the list of songs based on the filtered results
        val filteredSongs = detailScreenViewModel.detailScreenUiState.value.songs?.toMutableList()
        contentSongList.forEach {
            filteredSongs?.remove(it)
        }

        if (filteredSongs != null) {
            addSongItemAdapter.updateSongs(filteredSongs)
        }
    }
}