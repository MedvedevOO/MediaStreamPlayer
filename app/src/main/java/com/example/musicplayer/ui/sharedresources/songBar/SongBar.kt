package com.example.musicplayer.ui.sharedresources.songBar


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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.songscreen.SongEvent
import com.example.musicplayer.ui.theme.typography

@Composable
fun SongBar(
    modifier: Modifier = Modifier,
    onEvent: (SongEvent) -> Unit,
    playerState: PlayerState?,
    previousSong: Song?,
    song: Song?,
    nextSong: Song?
) {
//TODO: Заменить иконки на радио
    val albumArtPainter: Painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = song?.imageUrl?.toUri())
            .apply(
                block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    placeholder(R.drawable.stocksongcover)
                    error(R.drawable.stocksongcover)
                })
            .build()
    )
    val playPauseIcon = if (playerState == PlayerState.PLAYING) {
        Icons.Default.Pause
    } else {
        Icons.Default.PlayArrow
    }


    Row(
        modifier = modifier,
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
            TrackInfoPager(onEvent, previousSong, song, nextSong)
        }


        Icon(
            imageVector = playPauseIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    if (playerState == PlayerState.PLAYING) {
                        onEvent(SongEvent.PauseSong)
                    } else {
                        onEvent(SongEvent.ResumeSong)
                    }
                }

                .size(28.dp)
                .clip(CircleShape)
                .zIndex(1f)
        )

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

    val isScrollEnabled = remember {
        mutableStateOf(true)
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
                userScrollEnabled = isScrollEnabled.value,
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
                            modifier = Modifier.basicMarquee(
                                initialDelayMillis = 5000,
                                delayMillis = 5000
                            )
                        )
                        Text(
                            text = content.artist,
                            style = typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.basicMarquee(
                                initialDelayMillis = 5000,
                                delayMillis = 5000
                            )
                        )
                    }


                }

            }
        }

    }

}
