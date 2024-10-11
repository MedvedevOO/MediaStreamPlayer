package com.bearzwayne.musicplayer.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.data.PermissionHandler
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.navigation.ChangePlaylist
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.sharedresources.SpacingItemDecoration
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongListAdapter
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var fixedContent: FrameLayout
    private lateinit var songListAdapter: SongListAdapter
    private lateinit var playPauseButton: LinearLayout
    private lateinit var playPauseIcon: ImageView
    private lateinit var getPermissionButton: LinearLayout
    private lateinit var noTracksText: LinearLayout
    private lateinit var changePlaylistButton: MaterialButton
    private lateinit var currentPlaylistTextView: TextView
    private lateinit var nextTrackTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var musicControllerUiState: MusicControllerUiState
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>


    private val viewModel: HomeViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fixedContent = view.findViewById(R.id.home_fixed_content)
        getPermissionButton = view.findViewById(R.id.get_permissions_button)
        noTracksText = view.findViewById(R.id.no_tracks)
        recyclerView = view.findViewById(R.id.song_list_recyclerview)
        playPauseButton = view.findViewById(R.id.play_pause_button)
        playPauseIcon = view.findViewById(R.id.play_pause_icon)
        changePlaylistButton = view.findViewById(R.id.change_playlist_button)
        currentPlaylistTextView = view.findViewById(R.id.playlist_name)
        nextTrackTextView = view.findViewById(R.id.next_track_details)
        if (PermissionHandler.areAllPermissionsGranted(view.context)) {
            onPermissionsGranted()
        } else {
            requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val allPermissionsGranted = permissions.entries.all { it.value }
                if (allPermissionsGranted) {
                    onPermissionsGranted()
                } else {
                    // Show rationale if needed
                    getPermissionButton.visibility = View.VISIBLE
                    playPauseIcon.visibility = View.GONE
                    playPauseButton.visibility = View.GONE
                    getPermissionButton.setOnClickListener {
                        openAppSettings()
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_AUDIO)) {
                            //TODO: Show rationale
                        }
                    }

                }
            }
            requestPermissionLauncher.launch(PermissionHandler.permissions().toTypedArray())
        }



    }

    private fun onPermissionsGranted() {

        songListAdapter = SongListAdapter(
            allSongsList = viewModel.homeUiState.value.songs ?: emptyList(),
            playerState = viewModel.homeUiState.value.playerState,
            currentSong = viewModel.homeUiState.value.selectedSong,
            favoriteSongs = viewModel.homeUiState.value.playlists?.firstOrNull { it.name == DataProvider.getFavoritesName() }?.songList
                ?: emptyList(),
            songs = viewModel.homeUiState.value.selectedPlaylist?.songList ?: emptyList(),
            onSongClick = { song ->
                viewModel.selectSong(song)
                viewModel.playSong()
            },
            onSongLikeClick = { song ->
                viewModel.likeSong(song)
            },
            onSongSettingsClick = {
                findNavController().navigate(SongSettings(song = it))
            }
        )
        // Set up RecyclerView
        recyclerView.adapter = songListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpacingItemDecoration(16))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Calculate scroll position and adjust alpha accordingly
                val scrollOffset = recyclerView.computeVerticalScrollOffset().toFloat()

                // Change the alpha value of the FrameLayout to fade out as we scroll
                val alpha = 1 - (scrollOffset / 500) // Adjust 500 as needed for speed of fade
                fixedContent.alpha = alpha.coerceIn(0f, 1f)
                if (fixedContent.alpha == 0f) {
                    fixedContent.elevation = 0f
                } else {
                    fixedContent.elevation = recyclerView.elevation + 1f
                }
            }
        })
        // Observe the ViewModel state
        lifecycleScope.launchWhenStarted {
            sharedViewModel.musicControllerUiState.collect { uiState ->
                musicControllerUiState = uiState
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.homeUiState.collect { uiState ->
                updateUi(uiState)
            }
        }

        // Handle play/pause button clicks
        playPauseButton.setOnClickListener {
            val playerState = viewModel.homeUiState.value.playerState
            if (playerState == PlayerState.PLAYING) {
                viewModel.pauseSong()
            } else {
                viewModel.resumeSong()
            }
        }

        // Handle change playlist button clicks
        changePlaylistButton.setOnClickListener {
            findNavController().navigate(ChangePlaylist)
        }
    }

    // Update UI based on the state from ViewModel
    private fun updateUi(uiState: HomeUiState) {

        if (uiState.songs?.isNotEmpty() == true) {
            getPermissionButton.visibility = View.GONE
            playPauseIcon.visibility = View.VISIBLE
            playPauseButton.visibility = View.VISIBLE
            noTracksText.visibility = View.GONE
        } else {
            getPermissionButton.visibility = View.GONE
            playPauseIcon.visibility = View.GONE
            playPauseButton.visibility = View.GONE
            noTracksText.visibility = View.VISIBLE
        }
        // Update RecyclerView adapter with the new song list
        songListAdapter.updateSongs(
            uiState.selectedPlaylist?.songList ?: emptyList(),
            uiState.playlists?.firstOrNull { it.name == DataProvider.getFavoritesName() }?.songList
                ?: emptyList(),
            uiState.playerState,
            uiState.selectedSong
        )

        val iconRes = if (uiState.playerState == PlayerState.PLAYING) {
            R.drawable.ic_round_pause
        } else {
            R.drawable.ic_round_play_arrow
        }
        playPauseIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes))
        currentPlaylistTextView.text = uiState.selectedPlaylist?.name ?: "No Playlist Set"
        val nextTrackInfo =
            "${sharedViewModel.musicControllerUiState.value.nextSong?.artist} - ${sharedViewModel.musicControllerUiState.value.nextSong?.title}"
        nextTrackTextView.text = nextTrackInfo
    }

    override fun onResume() {
        super.onResume()
        if (PermissionHandler.areAllPermissionsGranted(requireContext())) {
            viewModel.fetchData()
            onPermissionsGranted()
        }

    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", "com.bearzwayne.musicplayer", null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
