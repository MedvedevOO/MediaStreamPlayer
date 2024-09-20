package com.example.musicplayer.ui.songscreen.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.Song

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
    modifier: Modifier = Modifier,
    previousSong: Song?,
    currentSong: Song,
    nextSong: Song?,
    playNextSong: () -> Unit,
    playPreviousSong: () -> Unit
) {


    val previous = remember { mutableStateOf(previousSong) }
    val current = remember { mutableStateOf(currentSong) }
    val next = remember { mutableStateOf(nextSong) }

    val albumCoverSnapshotStateList: SnapshotStateMap<Int, Song?> = SnapshotStateMap<Int, Song?>().apply {
        put(0,previous.value)
        put(1,current.value)
        put(2,next.value)
    }
    val state = rememberPagerState(initialPage = 1, pageCount = { albumCoverSnapshotStateList.size})
    val isScrollEnabled = remember {
        mutableStateOf(true)
    }

    LaunchedEffect(currentSong) {
        when(currentSong.songUrl) {
            previous.value?.songUrl -> state.animateScrollToPage(0)
            next.value?.songUrl -> state.animateScrollToPage(2)
        }
        previous.value = previousSong
        current.value = currentSong
        next.value = nextSong

        state.scrollToPage(1)
    }

    LaunchedEffect(state.settledPage) {
        val newTrack = albumCoverSnapshotStateList[state.settledPage]

        if (newTrack != null) {
            when(state.settledPage) {
                0 -> playPreviousSong()
                2 -> playNextSong()
            }
        } else {
            state.animateScrollToPage(1)
        }

    }



    HorizontalPager(
        state = state,
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = isScrollEnabled.value,
        contentPadding = PaddingValues(horizontal = 28.dp),  // Adjust this value to control the visibility of neighboring pages
        pageSpacing = 16.dp , // Adjust this value to control the space between items
    ) { page ->

        albumCoverSnapshotStateList[page % albumCoverSnapshotStateList.size]?.let { content ->
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = content.imageUrl.toUri())
                            .apply(
                                block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                    placeholder(R.drawable.stocksongcover)
                                    error(R.drawable.stocksongcover)
                                })
                            .build()
                        ),
//                    rememberAsyncImagePainter(model = content.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(320.dp)
                        .clip(RoundedCornerShape(10.dp))

                )
            }
        }
    }



}