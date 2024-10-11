package com.bearzwayne.musicplayer.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.sharedresources.SpacingItemDecoration
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongListAdapter
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var songListAdapter: SongListAdapter
    private lateinit var searchInput: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private val searchScreenViewModel: SearchScreenViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchInput = view.findViewById(R.id.search_input)
        recyclerView = view.findViewById(R.id.song_list_recyclerview)

        searchInput.setHint(R.string.search_for_tracks)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchScreenViewModel.onSearchQueryChanged(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        songListAdapter = SongListAdapter(
            allSongsList = searchScreenViewModel.searchScreenUiState.value.allSongsPlaylist?.songList
                ?: emptyList(),
            playerState = searchScreenViewModel.searchScreenUiState.value.playerState,
            currentSong = searchScreenViewModel.searchScreenUiState.value.selectedSong,
            favoriteSongs = searchScreenViewModel.searchScreenUiState.value.favoritesPlaylist?.songList ?: emptyList(),
            songs = emptyList(),
            onSongClick = { song ->
                searchScreenViewModel.playSong(song)
            },
            onSongLikeClick = {song->
                searchScreenViewModel.addOrRemoveFromFavorites(song)
            },
            onSongSettingsClick = {
                findNavController().navigate(SongSettings(song = it))
            }
        )
        recyclerView.adapter = songListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpacingItemDecoration(16))
        lifecycleScope.launchWhenStarted {
            searchScreenViewModel.searchScreenUiState.collect { uiState ->
                updateUi(uiState)
            }
        }
    }

    private fun updateUi(uiState: SearchScreenUiState) {
        val filteredSongs = uiState.filteredSongs
        val favoriteSongs = uiState.favoritesPlaylist?.songList ?: emptyList()
        songListAdapter.updateSongs(filteredSongs, favoriteSongs, uiState.playerState, uiState.selectedSong)
    }
}