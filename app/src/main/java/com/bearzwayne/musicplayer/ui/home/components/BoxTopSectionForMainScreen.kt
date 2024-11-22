package com.bearzwayne.musicplayer.ui.home.components


import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.other.MusicControllerUiState
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.home.HomeEvent
import com.bearzwayne.musicplayer.ui.home.HomeUiState
import com.bearzwayne.musicplayer.ui.library.components.LibraryHorizontalCardItem
import com.bearzwayne.musicplayer.ui.theme.typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxTopSectionForMainScreen(
    homeUiState: HomeUiState,
    musicControllerUiState: MusicControllerUiState,
    onEvent: (HomeEvent) -> Unit,
    dynamicAlphaForTopPart: Float) {
    val showChangePlaylistDialog = remember { mutableStateOf(false) }
    var playerIcon by remember { mutableStateOf(Icons.Default.PlayArrow) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val playButtonColors = ButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.Transparent)

    val changePlaylistButtonColors = ButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.Transparent)

    val nextTrackInfo = remember {mutableStateOf("")}

    playerIcon = if(musicControllerUiState.playerState == PlayerState.PLAYING){
        Icons.Default.Pause
    } else {
        Icons.Default.PlayArrow
    }
    if(musicControllerUiState.nextSong != null && musicControllerUiState.playerState == PlayerState.PLAYING) {
        nextTrackInfo.value = "${musicControllerUiState.nextSong.artist} - ${musicControllerUiState.nextSong.title}"
    } else {
        nextTrackInfo.value = ""
    }

    Box(
        modifier = Modifier
            .height(screenHeight - 260.dp)
            .offset(y = (-120).dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = dynamicAlphaForTopPart }
        ) {
            Button(
                onClick = {
                    if (musicControllerUiState.playerState == PlayerState.PLAYING) {
                        onEvent(HomeEvent.PauseSong)
                    } else {
                        onEvent(HomeEvent.ResumeSong)
                    }
                          },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = playButtonColors
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = playerIcon, tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
                Text(
                    maxLines = 1,
                    text = homeUiState.selectedPlaylist!!.name,
                    style = typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        shadow = Shadow(
                            MaterialTheme.colorScheme.background, blurRadius = 1f
                        )
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Button(
                onClick = { showChangePlaylistDialog.value = !showChangePlaylistDialog.value
                },
                modifier = Modifier.padding(top = 8.dp),
                shape = CircleShape,
                colors = changePlaylistButtonColors
            ) {
                Text(
                    stringResource(id = R.string.change_playlist), style = typography.bodySmall.copy(fontSize = 14.sp, shadow = Shadow(
                        MaterialTheme.colorScheme.background, blurRadius = 1f)
                    ))
            }

            if(showChangePlaylistDialog.value) {
                ModalBottomSheet(
                    sheetState = rememberModalBottomSheetState(),
                    onDismissRequest = {
                        showChangePlaylistDialog.value = false
                    },
                ) {
                    LazyColumn {
                        homeUiState.playlists?.let {
                            itemsIndexed(it.toList()) { _, item->
                                if (item.songList.isNotEmpty()) {
                                    LibraryHorizontalCardItem(item) {
                                        onEvent(HomeEvent.OnPlaylistChange(item))
                                        showChangePlaylistDialog.value = false
                                    }
                                }

                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (nextTrackInfo.value.isNotBlank()){
                Text(
                    text = stringResource(id = R.string.next_track),
                    style = typography.bodySmall.copy(color = Color.LightGray),
                    modifier = Modifier
                        .height(24.dp)
                        .padding(4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }


            Text(
                text = nextTrackInfo.value,
                style = typography.bodySmall.copy(color = Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }

}
