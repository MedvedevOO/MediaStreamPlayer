package com.bearzwayne.musicplayer.ui.details

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.toRoute
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.navigation.Detail
import com.bearzwayne.musicplayer.ui.navigation.DetailSettings
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.sharedresources.SpacingItemDecoration
import com.bearzwayne.musicplayer.ui.sharedresources.loadAlbumCoverAndSetGradient
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongListAdapter
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var fixedContent: LinearLayout
    private lateinit var detailControlsLayout: ConstraintLayout
    private lateinit var contentImageView: ImageView
    private lateinit var topBarTextView: TextView
    private lateinit var contentNameTextView: TextView
    private lateinit var contentDescriptionTextView: TextView

    private lateinit var songListAdapter: SongListAdapter
    private lateinit var backButton: ImageButton
    private lateinit var settingsButton: ImageButton
    private lateinit var shuffleButton: ImageButton
    private lateinit var playPauseButton: ImageButton
    private lateinit var topPlayButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var musicControllerUiState: MusicControllerUiState
    private lateinit var screenUiState: DetailScreenUiState
    private lateinit var itemUiState: DetailScreenItemUiState


    private val viewModel: DetailScreenViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val args = navController.currentBackStackEntry?.toRoute<Detail>()
        if (args != null) {
            viewModel.setDetailScreenItem(args.id,args.name, args.type)
        }
        screenUiState = viewModel.detailScreenUiState.value
        itemUiState = viewModel.detailScreenItemUiState.value
        musicControllerUiState = sharedViewModel.musicControllerUiState.value

        lifecycleScope.launchWhenStarted {
            combine(
                viewModel.detailScreenUiState,
                viewModel.detailScreenItemUiState
            ) { detailScreenUiState, detailScreenItemUiState ->
                detailScreenUiState to detailScreenItemUiState
            }.collect { (detailScreenUiState, detailScreenItemUiState) ->
                screenUiState = detailScreenUiState
                itemUiState = detailScreenItemUiState
                loadAlbumCoverAndSetGradient(detailScreenItemUiState.contentArtworkUri,requireContext(),view)
                updateUi(itemUiState, screenUiState)
            }
        }
        fixedContent = view.findViewById(R.id.fixed_content)
        detailControlsLayout = view.findViewById(R.id.detail_controls)
        contentImageView = view.findViewById(R.id.image_view_pager_item)
        topBarTextView = view.findViewById(R.id.top_bar_name)
        contentNameTextView = view.findViewById(R.id.detail_controls_content_name)
        contentDescriptionTextView = view.findViewById(R.id.detail_controls_content_description)
        recyclerView = view.findViewById(R.id.song_list_recyclerview)
        backButton = view.findViewById(R.id.back_button)
        topPlayButton = view.findViewById(R.id.top_play_pause_button)
        settingsButton = view.findViewById(R.id.settings_button)
        shuffleButton = view.findViewById(R.id.shuffle_button)
        playPauseButton = view.findViewById(R.id.play_pause_button)
        contentImageView.setImageURI(itemUiState.contentArtworkUri)



        backButton.setOnClickListener {
            navController.popBackStack()
        }

        topPlayButton.setOnClickListener {
            viewModel.onPlayButtonClick()
        }

        settingsButton.setOnClickListener {
            navController.navigate(DetailSettings)
        }

        shuffleButton.setOnClickListener {
            viewModel.shufflePlay()
        }

        playPauseButton.setOnClickListener {
            viewModel.onPlayButtonClick()
        }

        songListAdapter = SongListAdapter(
            allSongsList = screenUiState.songs ?: emptyList(),
            playerState = screenUiState.playerState,
            currentSong = screenUiState.selectedSong,
            favoriteSongs = screenUiState.playlists?.firstOrNull { it.name == DataProvider.getFavoritesName() }?.songList
                ?: emptyList(),
            songs = itemUiState.contentSongList,
            onSongClick = { song ->
                viewModel.onSongListItemClick(song)
            },
            onSongLikeClick = { song ->
                viewModel.onLikeClick(song)
            },
            onSongSettingsClick = {
                findNavController().navigate(SongSettings(song = it))
            }
        )
        recyclerView.adapter = songListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpacingItemDecoration(16))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollOffset = recyclerView.computeVerticalScrollOffset().toFloat()
                val alpha = 1 - (scrollOffset / 500)
                val alfaForTopBar = 0 + (scrollOffset/500)
                topBarTextView.alpha = alfaForTopBar.coerceIn(0f,1f)
                topPlayButton.alpha  = alfaForTopBar.coerceIn(0f,1f)
                contentImageView.alpha = alpha.coerceIn(0f, 1f)
                contentNameTextView.alpha  = alpha.coerceIn(0f, 1f)
                settingsButton.alpha = alpha.coerceIn(0f, 1f)
                shuffleButton.alpha = alpha.coerceIn(0f, 1f)
                playPauseButton.alpha = alpha.coerceIn(0f, 1f)
                contentDescriptionTextView.alpha = alpha.coerceIn(0f, 1f)
                if (contentImageView.alpha == 0f) {
                    fixedContent.elevation = 0f
                } else {
                    fixedContent.elevation = recyclerView.elevation + 1f
                }
            }
        })
        lifecycleScope.launchWhenStarted {
            sharedViewModel.musicControllerUiState.collect { uiState ->
                musicControllerUiState = uiState
            }
        }


    }

    private fun updateUi(uiState: DetailScreenItemUiState, fullUiState: DetailScreenUiState) {
        songListAdapter.updateSongs(
            uiState.contentSongList,
            fullUiState.playlists?.firstOrNull { it.name == DataProvider.getFavoritesName() }?.songList
                ?: emptyList(),
            musicControllerUiState.playerState,
            musicControllerUiState.currentSong
        )

        val iconRes = if (fullUiState.playerState == PlayerState.PLAYING) {
            R.drawable.ic_round_pause
        } else {
            R.drawable.ic_round_play_arrow
        }
        topBarTextView.text = itemUiState.contentName
        contentNameTextView.text = itemUiState.contentName
        contentDescriptionTextView.text = itemUiState.contentDescription
        topPlayButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes))
        playPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes))
    }
}
