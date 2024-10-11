package com.bearzwayne.musicplayer.ui.songscreen

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.other.toTime
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.songscreen.component.SongImagePagerAdapter
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SongScreenBottomSheetDialog : BottomSheetDialogFragment(R.layout.fragment_song_screen) {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: SongImagePagerAdapter
    private lateinit var musicControllerUiState: MusicControllerUiState
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var backButton: ImageButton
    private lateinit var playPauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var replayButton: ImageButton
    private lateinit var forwardButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var settingsButton: ImageButton

    private val viewModel:SharedViewModel by activityViewModels()
    val songViewModel: SongViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()

        val bottomSheetDialog = dialog as? com.google.android.material.bottomsheet.BottomSheetDialog
        val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        // Apply the behavior to expand the sheet
        bottomSheet?.let {
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true  // Optional: Prevent it from collapsing
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.song_image_pager)
        songTitle = view.findViewById(R.id.song_title)
        songArtist = view.findViewById(R.id.song_artist)
        seekBar = view.findViewById(R.id.song_seekbar)
        currentTime = view.findViewById(R.id.current_time)
        totalTime = view.findViewById(R.id.total_time)
        backButton = view.findViewById(R.id.btn_back)
        playPauseButton = view.findViewById(R.id.btn_play_pause)
        nextButton = view.findViewById(R.id.btn_next)
        replayButton = view.findViewById(R.id.btn_replay_10)
        forwardButton = view.findViewById(R.id.btn_forward_10)
        previousButton = view.findViewById(R.id.btn_previous)
        settingsButton = view.findViewById(R.id.settings_button)
        musicControllerUiState = viewModel.musicControllerUiState.value

        songTitle.text = musicControllerUiState.currentSong?.title
        songArtist.text = musicControllerUiState.currentSong?.artist

        currentTime.text = musicControllerUiState.currentPosition.toTime()
        totalTime.text = musicControllerUiState.totalDuration.toTime()
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

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        playPauseButton.setOnClickListener {
            if (musicControllerUiState.playerState == PlayerState.PLAYING) {
                songViewModel.pauseSong()
            } else {
                songViewModel.resumeSong()
            }
            val iconRes = if (musicControllerUiState.playerState == PlayerState.PLAYING) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow
            playPauseButton.setImageResource(iconRes)
        }
        previousButton.setOnClickListener {
            songViewModel.skipToPreviousSong()
        }
        replayButton.setOnClickListener {
            songViewModel.seekSongToPosition(if (musicControllerUiState.currentPosition - 10 * 1000 < 0) 0 else musicControllerUiState.currentPosition - 10 * 1000)
        }
        forwardButton.setOnClickListener {
            songViewModel.seekSongToPosition(musicControllerUiState.currentPosition + 10 * 1000)
        }
        nextButton.setOnClickListener {
            songViewModel.skipToNextSong()

        }

        settingsButton.setOnClickListener {
            if (musicControllerUiState.currentSong!= null) {
                findNavController().navigate(SongSettings(musicControllerUiState.currentSong!!))
            }
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
        songTitle.text = uiState.currentSong?.title
        songArtist.text = uiState.currentSong?.artist
        currentTime.text = uiState.currentPosition.toTime()
        totalTime.text = uiState.totalDuration.toTime()
        seekBar.progress = uiState.currentPosition.toInt()

        val iconRes = if (uiState.playerState == PlayerState.PLAYING) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow
        playPauseButton.setImageResource(iconRes)

        val albumImages = listOf(
            uiState.previousSong?.imageUrl,
            uiState.currentSong?.imageUrl,
            uiState.nextSong?.imageUrl
        )
        seekBar.max = uiState.totalDuration.toInt()
        adapter = SongImagePagerAdapter(albumImages)
        viewPager.adapter = adapter

        viewPager.setCurrentItem(1,false)
    }
}

