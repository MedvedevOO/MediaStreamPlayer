package com.bearzwayne.musicplayer.ui.sharedresources.songBar


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.navigation.Detail
import com.bearzwayne.musicplayer.ui.navigation.SongSettings
import com.bearzwayne.musicplayer.ui.songscreen.SongEvent
import com.bearzwayne.musicplayer.ui.songscreen.SongScreen
import com.bearzwayne.musicplayer.ui.songscreen.SongViewModel
import com.bearzwayne.musicplayer.ui.theme.typography

@Composable
fun SongBar(
    modifier: Modifier = Modifier,
    musicControllerUiState: MusicControllerUiState,
    navController: NavHostController,
) {
    val songViewModel: SongViewModel = hiltViewModel()
    val onEvent = songViewModel::onEvent
    val showPlayer = remember { mutableStateOf(false) }
    val albumArtPainter: Painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = musicControllerUiState.currentSong?.imageUrl?.toUri())
            .apply(
                block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    placeholder(R.drawable.stocksongcover)
                    error(R.drawable.stocksongcover)
                })
            .build()
    )
    val playPauseIcon = if (musicControllerUiState.playerState == PlayerState.PLAYING) {
        Icons.Default.Pause
    } else {
        Icons.Default.PlayArrow
    }

    if (showPlayer.value) {
        SongScreen(
            onEvent = songViewModel::onEvent,
            musicControllerUiState = musicControllerUiState,
            onSongListItemSettingsClick = {
                navController.navigate(
                    SongSettings(
                        it
                    )
                )
            },
            onDismissRequest = {
                showPlayer.value = false
            },
            onGotoArtistClick = {
                navController.navigate(
                    Detail(
                        type = "artist",
                        name = musicControllerUiState.currentSong?.artist
                    )
                )
                showPlayer.value = false
            },
            onGotoAlbumClick = {
                navController.navigate(
                    Detail(
                        type = "album",
                        name = musicControllerUiState.currentSong?.album
                    )
                )
                showPlayer.value = false
            }
        )
    }

    Row(
        modifier = modifier
            .clickable { showPlayer.value = !showPlayer.value }
            .semantics { this.contentDescription = "SongBar" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .padding(start = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = albumArtPainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .zIndex(1f)
            )
            Spacer(modifier = Modifier.size(6.dp))
            TrackInfoPager(
                onEvent = onEvent,
                previousSong = musicControllerUiState.previousSong,
                song = musicControllerUiState.currentSong,
                nextSong = musicControllerUiState.nextSong
            )
        }

        IconButton(onClick = {
            if (musicControllerUiState.playerState == PlayerState.PLAYING) {
                onEvent(SongEvent.PauseSong)
            } else {
                onEvent(SongEvent.ResumeSong)
            }
        },
            modifier = Modifier.semantics { this.contentDescription = "play/pause" }
        ) {
            Icon(
                imageVector = playPauseIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .zIndex(1f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackInfoPager(
    onEvent: (SongEvent) -> Unit,
    previousSong: Song?,
    song: Song?,
    nextSong: Song?
) {
    val albumCoverSnapshotStateList: SnapshotStateMap<Int, Song?> =
        SnapshotStateMap<Int, Song?>().apply {
            put(0, previousSong)
            put(1, song)
            put(2, nextSong)
        }

    val state =
        rememberPagerState(initialPage = 1, pageCount = { albumCoverSnapshotStateList.size })

    LaunchedEffect(state.settledPage) {
        val newTrack = albumCoverSnapshotStateList[state.settledPage]

        if (newTrack != null) {
            when (state.settledPage) {
                0 -> {
                    onEvent(SongEvent.SeekToStartOfSong)
                    onEvent(SongEvent.SkipToPreviousSong)
                }

                2 -> onEvent(SongEvent.SkipToNextSong)
            }
        } else {
            state.animateScrollToPage(1)
        }
        state.scrollToPage(1)
    }

    Box(
        modifier = Modifier
            .width(224.dp),
        contentAlignment = Alignment.Center

    ) {
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            HorizontalPager(
                state = state,
            ) { page ->

                albumCoverSnapshotStateList[page % albumCoverSnapshotStateList.size]?.let { content ->
                    Column(
                        modifier = Modifier
                            .height(76.dp)
                    ) {
                        Text(
                            text = content.title,
                            style = typography.headlineMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            modifier = Modifier
                                .basicMarquee(
                                    initialDelayMillis = 3000,
                                    repeatDelayMillis = 3000
                                )
                                .semantics { this.contentDescription = "BarTitle" }
                        )

                        Text(
                            text = content.artist,
                            style = typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .basicMarquee(
                                    initialDelayMillis = 3000,
                                    repeatDelayMillis = 3000
                                )
                                .semantics { this.contentDescription = "BarArtist" }
                        )
                    }
                }
            }
        }
    }
}
