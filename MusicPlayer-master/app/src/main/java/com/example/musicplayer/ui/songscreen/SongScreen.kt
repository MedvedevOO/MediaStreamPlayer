package com.example.musicplayer.ui.songscreen

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.MusicControllerUiState
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.other.toTime
import com.example.musicplayer.ui.home.HomeEvent
import com.example.musicplayer.ui.home.HomeUiState
import com.example.musicplayer.ui.sharedresources.albumCoverImage
import com.example.musicplayer.ui.sharedresources.song.SongSettingsItem
import com.example.musicplayer.ui.songscreen.component.ImagePager
import com.example.musicplayer.ui.songscreen.component.SongDescription
import com.example.musicplayer.ui.theme.extensions.generateDominantColorState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    showScreen: MutableState<Boolean>,
    homeUiState: HomeUiState,
    navController: NavController,
    onHomeEvent: (HomeEvent) -> Unit,
    onEvent: (SongEvent) -> Unit,
    musicControllerUiState: MusicControllerUiState,
    onNavigateUp: () -> Unit,
) {
    if (musicControllerUiState.currentSong != null) {
        val song = try {
            homeUiState.songs!!.first {it.songUrl == musicControllerUiState.currentSong.songUrl}
        } catch (e: NoSuchElementException) {
            musicControllerUiState.currentSong
        }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showScreen.value = false },
            dragHandle = {},
            shape = RoundedCornerShape(0.dp)
        ){
            SongScreenBody(
                homeUiState = homeUiState,
                navController = navController,
                previousSong = musicControllerUiState.previousSong,
                currentSong = song,
                nextSong = musicControllerUiState.nextSong,
                onNavigateUp = onNavigateUp,
                musicControllerUiState = musicControllerUiState,
                onEvent = onEvent,
                onHomeEvent = onHomeEvent
            )
        }



    }
}

@Composable
fun SongScreenBody(
    homeUiState: HomeUiState,
    navController: NavController,
    previousSong: Song?,
    currentSong: Song,
    nextSong: Song?,
    onHomeEvent: (HomeEvent) -> Unit,
    onEvent: (SongEvent) -> Unit,
    musicControllerUiState: MusicControllerUiState,
    onNavigateUp: () -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val context = LocalContext.current

    val playPauseIcon =
        if (musicControllerUiState.playerState == PlayerState.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow

    val showSongSettings = remember { mutableStateOf(false) }
    val showAddToPlaylistDialog = remember { mutableStateOf(false) }

    var bitmap by remember { mutableStateOf<Bitmap?>(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply { eraseColor(0xFF454343.toInt()) }) }
    LaunchedEffect(currentSong) {
        bitmap = albumCoverImage(currentSong.imageUrl.toUri(), context)
    }

    bitmap?.let { image ->
        val swatch = image.generateDominantColorState()
        val dominantColor = animateColorAsState(
        targetValue = Color(swatch.rgb),
        animationSpec = tween(durationMillis = 1000),
        label = "animateColorAsState CurrentTrackScreen"
    )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)

        ) {

            SongScreenContent(
                allSongs = homeUiState.songs!!,
                previousSong = previousSong,
                currentSong = currentSong,
                nextSong = nextSong,
                dominantColor = dominantColor.value,
                currentTime = musicControllerUiState.currentPosition,
                totalTime = musicControllerUiState.totalDuration,
                playPauseIcon = playPauseIcon,
                playOrToggleSong = {
                    onEvent(if (musicControllerUiState.playerState == PlayerState.PLAYING) SongEvent.PauseSong else SongEvent.ResumeSong)
                },
                playNextSong = { onEvent(SongEvent.SkipToNextSong) },
                playPreviousSong = { onEvent(SongEvent.SkipToPreviousSong) },
                playPreviousSongForCover = {
                    onEvent(SongEvent.SeekSongToPosition(0))
                    onEvent(SongEvent.SkipToPreviousSong)
                },
                onSliderChange = { newPosition ->
                    onEvent(SongEvent.SeekSongToPosition(newPosition.toLong()))
                },
                onForward = {
                    onEvent(SongEvent.SeekSongToPosition(musicControllerUiState.currentPosition + 10 * 1000))
                },
                onRewind = {
                    musicControllerUiState.currentPosition.let { currentPosition ->
                        onEvent(SongEvent.SeekSongToPosition(if (currentPosition - 10 * 1000 < 0) 0 else currentPosition - 10 * 1000))
                    }
                },
                onClose = onNavigateUp,
                onSettingsClicked = {
                    showSongSettings.value = true
                },
                onGotoArtistClick = {
                    val author = homeUiState.artists!!.find { it.name == currentSong.artist }
                    if (homeUiState.songs.contains(currentSong)) {
                        navController.navigate("detail/artist/${author!!.id}")
                    }

                } ,
                onGotoAlbumClick = {
                    val album = homeUiState.albums!!.find { it.name == currentSong.album }
                    if (homeUiState.songs.contains(currentSong)){
                        navController.navigate("detail/album/${album!!.id}")
                    }

                },
            )

            SongSettingsItem(
                homeUiState = homeUiState,
                currentSong = musicControllerUiState.currentSong!!,
                showSongSettings = showSongSettings,
                showAddToPlaylistDialog = showAddToPlaylistDialog,
                songSettingsItem = currentSong,
                surfaceGradient = listOf(Color.Black, Color.Transparent),
                onOkAddPlaylistClick = {newPlaylist ->

                    val newSongList = newPlaylist.songList.toMutableList().apply { add(currentSong) }
                    val resultList = newPlaylist.copy(
                        songList = newSongList,
                        artWork = currentSong.imageUrl.toUri()
                    )
                    onHomeEvent(HomeEvent.AddNewPlaylist(resultList))
                    val toastText = context.getString(R.string.track_added_to_playlist, resultList.name)
                    Toast.makeText(context,toastText, Toast.LENGTH_SHORT).show()
                    showAddToPlaylistDialog.value = false

                },
                onPlaylistToAddSongChosen = {
                    onHomeEvent(HomeEvent.AddNewPlaylist(it))
                },
                onDetailMenuItemClick = {menuItem, song ->
                    showSongSettings.value = false

                    when(menuItem) {
                        context.getString(R.string.download) -> {}
                        context.getString(R.string.add_to_playlist_variant)  -> showAddToPlaylistDialog.value = true
                        context.getString(R.string.add_to_queue)  -> onHomeEvent(HomeEvent.AddSongListToQueue(listOf(song)))
                        context.getString(R.string.play_next)  -> onHomeEvent(HomeEvent.AddSongNextToCurrentSong(song))
                        context.getString(R.string.go_to_artist)  -> {
                            val author = homeUiState.artists!!.find { it.name == song.artist }
                            navController.navigate("detail/artist/${author!!.id}")
                        }
                        context.getString(R.string.go_to_album)  -> {
                            val songItem = homeUiState.songs!!.find { it.songUrl == currentSong.songUrl }
                            val album = homeUiState.albums!!.find { it.name == songItem!!.album }
                            navController.navigate("detail/album/${album!!.id}")
                        }

                    }
                }
            )
        }

    }

}

@Composable
fun SongScreenContent(
    allSongs: List<Song>,
    previousSong: Song?,
    currentSong: Song,
    nextSong: Song?,
    dominantColor: Color,
    currentTime: Long,
    totalTime: Long,
    playPauseIcon: ImageVector,
    playOrToggleSong: () -> Unit,
    playNextSong: () -> Unit,
    playPreviousSong: () -> Unit,
    playPreviousSongForCover: () -> Unit,
    onSliderChange: (Float) -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onClose: () -> Unit,
    onSettingsClicked: () -> Unit,
    onGotoArtistClick: () -> Unit,
    onGotoAlbumClick: () -> Unit


) {
    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(
            dominantColor, MaterialTheme.colorScheme.background
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background
        )
    }

    val sliderColors = if (isSystemInDarkTheme()) {
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.onBackground,
            inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.6f
            ),
        )
    } else SliderDefaults.colors(
        thumbColor = dominantColor,
        activeTrackColor = dominantColor,
        inactiveTrackColor = dominantColor.copy(
            alpha = 0.6f
        ),
    )
    val currentTrack = remember { mutableStateOf(currentSong) }

    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(currentSong) {
        if (currentTrack.value != currentSong) {
            visible = false
            delay(1000L)
            currentTrack.value = currentSong
        }
        visible =true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = gradientColors,
                            endY = LocalConfiguration.current.screenHeightDp.toFloat() * LocalDensity.current.density
                        )
                    )
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column {
                    IconButton(
                        onClick = onClose
                    ) {
                        Image(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Close",
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ImagePager(
                        previousSong = previousSong,
                        currentSong = currentSong,
                        nextSong = nextSong,
                        playNextSong = playNextSong,
                        playPreviousSong = playPreviousSongForCover
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.height(70.dp)) {
                        this@Column.AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                            exit = fadeOut(animationSpec = tween(durationMillis = 500))
                        ) {

                            SongDescription(
                                allSongs = allSongs,
                                song = currentTrack.value,
                                onSettingsClicked = onSettingsClicked,
                                onGotoArtistClick = onGotoArtistClick,
                                onGotoAlbumClick = onGotoAlbumClick
                            )
                        }
                    }


                    Column(
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {

                            Slider(
                                value = currentTime.toFloat(),
                                modifier = Modifier.fillMaxWidth(),
                                valueRange = 0f..totalTime.toFloat(),
                                colors = sliderColors,
                                onValueChange = onSliderChange,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                                    Text(
                                        currentTime.toTime(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                                    Text(
                                        totalTime.toTime(), style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipPrevious,
                                contentDescription = "Skip Previous",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = playPreviousSong)
                                    .padding(4.dp)
                                    .size(36.dp)
                            )

                            Icon(
                                imageVector = Icons.Rounded.Replay10,
                                contentDescription = "Replay 10 seconds",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onRewind)
                                    .padding(4.dp)
                                    .size(36.dp)
                            )
                            Icon(
                                imageVector = playPauseIcon,
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable(onClick = playOrToggleSong)
                                    .size(64.dp)
                                    .padding(8.dp)
                            )
                            Icon(
                                imageVector = Icons.Rounded.Forward10,
                                contentDescription = "Forward 10 seconds",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onForward)
                                    .padding(4.dp)
                                    .size(36.dp)
                            )
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = "Skip Next",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = playNextSong)
                                    .padding(4.dp)
                                    .size(36.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}




