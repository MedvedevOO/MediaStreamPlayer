package com.bearzwayne.musicplayer.ui.radio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.ui.sharedresources.SpacingItemDecoration
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RadioScreenFragment : Fragment(R.layout.fragment_radio) {

    private lateinit var radioAdapter: RadioAdapter
    private lateinit var fixedContent: FrameLayout
    private val radioViewModel: RadioViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fixedContent = view.findViewById(R.id.fixed_content)
        val allStationsButton: Button = view.findViewById(R.id.button_all_radio_stations)
        allStationsButton.setOnClickListener {
            radioViewModel.fetchAllStations()
        }
        val popularStationsButton: Button = view.findViewById(R.id.button_popular)
        popularStationsButton.setOnClickListener {
            radioViewModel.fetchPopularStations(200)
        }

        val favoriteStationsButton: Button = view.findViewById(R.id.button_favorites)
        favoriteStationsButton.setOnClickListener {
            radioViewModel.loadFavoriteStations()
        }

        val topRatedStationsButton: Button = view.findViewById(R.id.button_top_rated)
        topRatedStationsButton.setOnClickListener {
            radioViewModel.fetchTopRatedStations(200)
        }

        // Set up RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.radio_recyclerview)
        radioAdapter = RadioAdapter(
            radioStations = emptyList(),
            favoriteStations = radioViewModel.radioUiState.value.favoriteStations ?: emptyList(),
            onItemClick = { radioStation ->
                radioViewModel.playRadio(radioStation, radioViewModel.radioUiState.value.currentStationsList)
            },
            onLikeClick = { radioStation, likeButton ->
                radioViewModel.toggleFavoriteStation(radioStation)
                if (radioViewModel.radioUiState.value.favoriteStations?.contains(radioStation) == true) {
                    likeButton.setImageResource(R.drawable.ic_favorite_filled)
                } else {
                    likeButton.setImageResource(R.drawable.ic_favorite_border)
                }
            },
            currentSong = radioViewModel.radioUiState.value.selectedPlaylist?.songList?.firstOrNull(),
            playerState = radioViewModel.radioUiState.value.playerState
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpacingItemDecoration(16))
        recyclerView.adapter = radioAdapter


        // Add scroll listener to RecyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Calculate scroll position and adjust alpha accordingly
                val scrollOffset = recyclerView.computeVerticalScrollOffset().toFloat()

                // Change the alpha value of the FrameLayout to fade out as we scroll
                val alpha = 1 - (scrollOffset / 500) // Adjust 500 as needed for speed of fade
                fixedContent.alpha = alpha.coerceIn(0f, 1f) // Keep alpha between 0 and 1
                if (fixedContent.alpha == 0f) {
                    fixedContent.elevation = 0f
                } else {
                    fixedContent.elevation = recyclerView.elevation + 1f
                }
            }
        })

        // Search Bar Text
        val searchInput: TextInputEditText = view.findViewById(R.id.search_input)
        searchInput.setHint(R.string.search_for_radiostation)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterRadioStations(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Observe the ViewModel to update UI
        lifecycleScope.launchWhenStarted {
            radioViewModel.radioUiState.collect { uiState ->
                updateUi(uiState)
            }
        }

        // Initial load of popular stations
        radioViewModel.fetchPopularStations(200)
    }

    private fun updateUi(uiState: RadioUiState) {
        radioAdapter.updateItems(uiState.currentStationsList ?: emptyList(), radioViewModel.radioUiState.value.favoriteStations ?: emptyList())

    }

    private fun filterRadioStations(query: String) {
        val filteredStations = radioViewModel.radioUiState.value.currentStationsList?.filter { station ->
            station.name.contains(query, ignoreCase = true) || station.country.contains(query, ignoreCase = true)
        } ?: emptyList()

        radioAdapter.updateItems(filteredStations, radioViewModel.radioUiState.value.favoriteStations ?: emptyList())
    }
}
