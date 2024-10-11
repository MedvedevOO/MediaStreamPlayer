package com.bearzwayne.musicplayer.ui.songscreen

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.sharedresources.loadAlbumCoverAndSetGradient
import com.bearzwayne.musicplayer.ui.songscreen.component.SongImagePagerAdapter
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel

class SongScreenFragment : Fragment(R.layout.fragment_song_screen) {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: SongImagePagerAdapter
    private lateinit var musicControllerUiState: MusicControllerUiState
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton

    private val viewModel:SharedViewModel by activityViewModels()
    val songViewModel: SongViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize UI components
        viewPager = view.findViewById(R.id.song_image_pager)
        songTitle = view.findViewById(R.id.song_title)
        songArtist = view.findViewById(R.id.song_artist)
        seekBar = view.findViewById(R.id.song_seekbar)
        currentTime = view.findViewById(R.id.current_time)
        totalTime = view.findViewById(R.id.total_time)
        playPauseButton = view.findViewById(R.id.btn_play_pause)
        nextButton = view.findViewById(R.id.btn_next)
        previousButton = view.findViewById(R.id.btn_previous)
        musicControllerUiState = viewModel.musicControllerUiState.value

        songTitle.text = musicControllerUiState.currentSong?.title
        songArtist.text = musicControllerUiState.currentSong?.artist
        currentTime.text = musicControllerUiState.currentPosition.toString()
        totalTime.text = musicControllerUiState.totalDuration.toString()
        seekBar.progress = musicControllerUiState.currentPosition.toInt()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    songViewModel.seekSongToPosition(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        playPauseButton.setOnClickListener {
            if (viewModel.musicControllerUiState.value.playerState == PlayerState.PLAYING) {
                songViewModel.pauseSong()
            } else {
                songViewModel.resumeSong()
            }
            val iconRes = if (viewModel.musicControllerUiState.value.playerState == PlayerState.PLAYING) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow
            playPauseButton.setImageResource(iconRes)
        }

        // Next button click listener
        nextButton.setOnClickListener {
            songViewModel.skipToNextSong()

        }

        // Previous button click listener
        previousButton.setOnClickListener {
            songViewModel.skipToPreviousSong()
        }

        val albumImages = listOf(
           musicControllerUiState.previousSong?.imageUrl,
           musicControllerUiState.currentSong?.imageUrl,
           musicControllerUiState.nextSong?.imageUrl
        )

        // Setup ViewPager2 with the adapter
        adapter = SongImagePagerAdapter(albumImages)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        viewPager.setCurrentItem(1,false)
        viewPager.setPageTransformer { page, position ->
            page.translationX = -position * 100f
            page.scaleY = 1 - Math.abs(position) * 0.25f
        }

        lifecycleScope.launchWhenStarted {
            viewModel.musicControllerUiState.collect { uiState ->
                loadAlbumCoverAndSetGradient(uiState.currentSong?.imageUrl?.toUri(),requireContext(),view)
                musicControllerUiState = uiState
                updateUi(uiState)

            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        if (musicControllerUiState.previousSong!= null) {
                            viewPager.setCurrentItem(0,true)
                            songViewModel.skipToPreviousSong()
                        } else {
                            viewPager.setCurrentItem(1,true)
                        }

                    }

                    2 -> {
                        if (musicControllerUiState.nextSong!= null) {
                            viewPager.setCurrentItem(2,true)
                            songViewModel.skipToNextSong()
                        } else {
                            viewPager.setCurrentItem(1,true)
                        }

                    }
                }
            }

        })
    }


    private fun updateUi(uiState: MusicControllerUiState) {
        // Observe LiveData from ViewModel and update UI accordingly

        songTitle.text = uiState.currentSong?.title
        songArtist.text = uiState.currentSong?.artist
        totalTime.text = uiState.totalDuration.toString()
        // Update ViewPager with album images here
        // Add code for ViewPager image loading
        currentTime.text = uiState.currentPosition.toString()
        seekBar.progress = uiState.currentPosition.toInt()

        val iconRes = if (uiState.playerState == PlayerState.PLAYING) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow
        playPauseButton.setImageResource(iconRes)

        val albumImages = listOf(
            uiState.previousSong?.imageUrl,
            uiState.currentSong?.imageUrl,
            uiState.nextSong?.imageUrl
        )
        adapter = SongImagePagerAdapter(albumImages)
        viewPager.adapter = adapter

        viewPager.setCurrentItem(1,false)
    }
}
