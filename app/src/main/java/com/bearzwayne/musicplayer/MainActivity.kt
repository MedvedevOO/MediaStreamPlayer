package com.bearzwayne.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import android.widget.VideoView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.dialog
import androidx.navigation.fragment.fragment
import androidx.navigation.ui.setupWithNavController
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import com.bearzwayne.musicplayer.data.service.MusicService
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.addsongstoplaylist.AddSongsFragment
import com.bearzwayne.musicplayer.ui.details.DetailFragment
import com.bearzwayne.musicplayer.ui.details.components.DetailSettingsBottomSheetDialog
import com.bearzwayne.musicplayer.ui.editplaylist.EditSongsFragment
import com.bearzwayne.musicplayer.ui.home.HomeFragment
import com.bearzwayne.musicplayer.ui.sharedresources.song.AddToPlaylistBottomSheetDialog
import com.bearzwayne.musicplayer.ui.home.components.ChangePlaylistBottomSheetDialog
import com.bearzwayne.musicplayer.ui.library.LibraryFragment
import com.bearzwayne.musicplayer.ui.navigation.AddSongToPlaylist
import com.bearzwayne.musicplayer.ui.navigation.AddSongs
import com.bearzwayne.musicplayer.ui.navigation.ChangePlaylist
import com.bearzwayne.musicplayer.ui.navigation.CustomNavType
import com.bearzwayne.musicplayer.ui.navigation.Detail
import com.bearzwayne.musicplayer.ui.navigation.DetailSettings
import com.bearzwayne.musicplayer.ui.navigation.EditPlaylist
import com.bearzwayne.musicplayer.ui.navigation.Home
import com.bearzwayne.musicplayer.ui.navigation.Library
import com.bearzwayne.musicplayer.ui.navigation.Radio
import com.bearzwayne.musicplayer.ui.navigation.Search
import com.bearzwayne.musicplayer.ui.navigation.SongScreen
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.radio.RadioScreenFragment
import com.bearzwayne.musicplayer.ui.search.SearchFragment
import com.bearzwayne.musicplayer.ui.sharedresources.loadAlbumCoverAndSetGradient
import com.bearzwayne.musicplayer.ui.sharedresources.song.SongSettingsBottomSheetDialog
import com.bearzwayne.musicplayer.ui.songscreen.SongScreenBottomSheetDialog
import com.bearzwayne.musicplayer.ui.songscreen.SongViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var gradientBackground: View
    private lateinit var toolbar: Toolbar
    private lateinit var topBarTextView: TextView
    private lateinit var songBar: LinearLayout
    private lateinit var songBarImageView: ImageView
    private lateinit var songBarTitle: TextView
    private lateinit var songBarArtist: TextView
    private lateinit var songBarPlayButton: ImageButton
    private val sharedViewModel: SharedViewModel by viewModels()
    private val songViewModel: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val callback = this.onBackPressedDispatcher.addCallback(this) {
            navController.popBackStack()
        }
        callback.isEnabled = true

        videoView = findViewById(R.id.video_background)
        toolbar = findViewById(R.id.toolbar)
        topBarTextView = findViewById(R.id.toolbar_title)
        songBar = findViewById(R.id.song_bar)
        songBarImageView = findViewById(R.id.bar_album_art)
        songBarTitle = findViewById(R.id.bar_track_title)
        songBarArtist = findViewById(R.id.bar_track_artist)
        songBarPlayButton = findViewById(R.id.bar_play_pause_button)
        gradientBackground = findViewById(R.id.gradient_overlay)
        songBar.setOnClickListener {
            navController.navigate(SongScreen)
        }

        songBarPlayButton.setOnClickListener {
            if (sharedViewModel.musicControllerUiState.value.playerState == PlayerState.PLAYING) {
                songViewModel.pauseSong()

            } else {
                songViewModel.resumeSong()
            }
        }

        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.pulse_background}")
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            videoView.start()
            videoView.pause()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.musicControllerUiState.collect { uiState ->
                    handleVideoPlayback(uiState.playerState ?: PlayerState.STOPPED)
                    loadAlbumCoverAndSetGradient(uiState.currentSong?.imageUrl?.toUri(),baseContext,gradientBackground)
                    uiState.currentSong?.let { updateSongBar(it) }
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            topBarTextView.text = getCurrentDestinationName(destination.route)
        }


        // Define the navigation graph programmatically
        val navGraph = navController.createGraph(
            startDestination = Home
        ) {
            // Define a destination for HomeFragment
            fragment<HomeFragment, Home> {
                label = "Home"
            }

            fragment<RadioScreenFragment, Radio> {
                label = "Radio"
            }

            fragment<SearchFragment, Search> {
                label = "Search"
            }

            fragment<LibraryFragment, Library> {
                label = "Library"
            }

            fragment<DetailFragment, Detail> {
                label = "Detail screen"
            }

            fragment<AddSongsFragment, AddSongs>(
                typeMap = mapOf(typeOf<Playlist>() to CustomNavType.playlistType)
            ) {
                label = "Add Songs to playlist screen"
            }

            fragment<EditSongsFragment, EditPlaylist>(
                typeMap = mapOf(typeOf<Playlist>() to CustomNavType.playlistType)
            ) {
                label = "Edit Playlist Screen"
            }
            dialog<SongScreenBottomSheetDialog, SongScreen> {
                label = "Song Screen"
            }

            dialog<SongSettingsBottomSheetDialog, SongSettings>(typeMap = mapOf(typeOf<Song>() to CustomNavType.songType)) {
                label = "Song settings"
            }

            dialog<AddToPlaylistBottomSheetDialog, AddSongToPlaylist>(typeMap = mapOf(typeOf<Song>() to CustomNavType.songType)) {
                label = "Add song to playlist"
            }

            dialog<ChangePlaylistBottomSheetDialog, ChangePlaylist> {
                label = "Change Playlist"
            }

            dialog<DetailSettingsBottomSheetDialog, DetailSettings> {
                label = "DetailScreen Settings"
            }
        }

        // Set the graph to the NavController
        navController.graph = navGraph

        // Set up BottomNavigationView with the NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Home -> {
                    navController.navigate(Home)
                    true
                }

                R.id.Radio -> {
                    navController.navigate(Radio)
                    true
                }

                R.id.Search -> {
                    navController.navigate(Search)
                    true
                }

                R.id.Library -> {
                    navController.navigate(Library)
                    true
                }

                else -> false
            }
        }
    }

    private fun handleVideoPlayback(playerState: PlayerState) {
        when (playerState) {
            PlayerState.PLAYING -> videoView.start()
            PlayerState.PAUSED, PlayerState.STOPPED -> videoView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.destroyMediaController()
        stopService(Intent(this, MusicService::class.java))
    }


    private fun updateSongBar(song: Song) {
        Glide.with(songBarImageView.context)
            .load(song.imageUrl)
            .placeholder(R.drawable.stocksongcover) // Optional: Placeholder image
            .error(R.drawable.stocksongcover) // Optional: Error image
            .into(songBarImageView)

        songBarTitle.text = song.title
        songBarArtist.text = song.artist

        val playPauseIcon =
            if (sharedViewModel.musicControllerUiState.value.playerState == PlayerState.PLAYING) {
                getDrawable(R.drawable.ic_round_pause)
            } else {
                getDrawable(R.drawable.ic_round_play_arrow)
            }
        songBarPlayButton.setImageDrawable(playPauseIcon)
    }

    private fun getCurrentDestinationName(route: String?): String {
        return when (route) {
            Home::class.qualifiedName -> {
                toolbar.visibility = View.VISIBLE
                getString(R.string.app_name)
            }

            Radio::class.qualifiedName -> {
                toolbar.visibility = View.VISIBLE
                getString(R.string.nav_radio)
            }

            Search::class.qualifiedName -> {
                toolbar.visibility = View.VISIBLE
                getString(R.string.nav_search)
            }

            Library::class.qualifiedName -> {
                toolbar.visibility = View.VISIBLE
                getString(R.string.nav_library)
            }

            else -> {
                toolbar.visibility = View.GONE
                ""
            }
        }
    }
}