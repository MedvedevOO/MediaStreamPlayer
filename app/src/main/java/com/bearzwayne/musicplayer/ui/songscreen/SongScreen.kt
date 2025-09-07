package com.bearzwayne.musicplayer.ui.songscreen

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.other.toTime
import com.bearzwayne.musicplayer.ui.sharedresources.albumCoverImage
import com.bearzwayne.musicplayer.ui.songscreen.component.ImagePager
import com.bearzwayne.musicplayer.ui.songscreen.component.SongDescription
import com.bearzwayne.musicplayer.ui.theme.bestOrange
import com.bearzwayne.musicplayer.ui.theme.extensions.generateDominantColorState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    onEvent: (SongEvent) -> Unit,
    musicControllerUiState: MusicControllerUiState,
    onSongListItemSettingsClick: (song: Song) -> Unit,
    onDismissRequest: () -> Unit,
    onGotoArtistClick: () -> Unit,
    onGotoAlbumClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = {},
        shape = RoundedCornerShape(0.dp)
    ) {
        SongScreenBody(
            allSongs = musicControllerUiState.songs ?: emptyList(),
            previousSong = musicControllerUiState.previousSong,
            currentSong = musicControllerUiState.currentSong!!,
            nextSong = musicControllerUiState.nextSong,
            playerState = musicControllerUiState.playerState,
            currentPosition = musicControllerUiState.currentPosition,
            totalDuration = musicControllerUiState.totalDuration,
            onNavigateUp = {
                scope.launch {
                    sheetState.hide()
                    onDismissRequest()
                }
            },
            onEvent = onEvent,
            onSongListItemSettingsClick = onSongListItemSettingsClick,
            onGotoAlbumClick = onGotoAlbumClick,
            onGotoArtistClick = onGotoArtistClick
        )
    }
}

@Composable
fun SongScreenBody(
    allSongs: List<Song>,
    previousSong: Song?,
    currentSong: Song,
    nextSong: Song?,
    playerState: PlayerState?,
    currentPosition: Long,
    totalDuration: Long,
    onEvent: (SongEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onSongListItemSettingsClick: (song: Song) -> Unit,
    onGotoArtistClick: () -> Unit,
    onGotoAlbumClick: () -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val context = LocalContext.current
    val playPauseIcon =
        if (playerState == PlayerState.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow
    var bitmap by remember {
        mutableStateOf<Bitmap?>(
            createBitmap(100, 100).apply { eraseColor(0xFF454343.toInt()) })
    }

    LaunchedEffect(currentSong) {
        bitmap = albumCoverImage(currentSong.imageUrl.toUri(), context)
    }

    bitmap.let { image ->
        val swatch: Palette.Swatch = image?.generateDominantColorState()
            ?: Palette.Swatch(bestOrange.value.toInt(), 1000)
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
                allSongs = allSongs,
                previousSong = previousSong,
                currentSong = currentSong,
                nextSong = nextSong,
                dominantColor = dominantColor.value,
                currentTime = currentPosition,
                totalTime = totalDuration,
                playPauseIcon = playPauseIcon,
                playOrToggleSong = {
                    onEvent(if (playerState == PlayerState.PLAYING) SongEvent.PauseSong else SongEvent.ResumeSong)
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
                    onEvent(SongEvent.SeekSongToPosition(currentPosition + 10 * 1000))
                },
                onRewind = {
                    currentPosition.let { currentPosition ->
                        onEvent(SongEvent.SeekSongToPosition(if (currentPosition - 10 * 1000 < 0) 0 else currentPosition - 10 * 1000))
                    }
                },
                onClose = onNavigateUp,
                onSettingsClicked = onSongListItemSettingsClick,
                onGotoArtistClick = onGotoArtistClick,
                onGotoAlbumClick = onGotoAlbumClick
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
    onSettingsClicked: (song: Song) -> Unit,
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

    val sliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(
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
        visible = true
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
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(
                            onClick = onClose,
                            colors = IconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.LightGray
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .semantics { contentDescription = "exit from songScreen" },
                                imageVector = Icons.Default.KeyboardArrowDown,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }
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
                                Text(
                                    currentTime.toTime(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    totalTime.toTime(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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




