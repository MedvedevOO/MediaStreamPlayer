package com.example.musicplayer.ui.songscreen

//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.animateColorAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Pause
//import androidx.compose.material.icons.filled.PlayArrow
//import androidx.compose.material.icons.filled.SkipNext
//import androidx.compose.material.icons.filled.SkipPrevious
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonColors
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.IconButtonColors
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.SheetState
//import androidx.compose.material3.Slider
//import androidx.compose.material3.Text
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.rememberUpdatedState
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshots.SnapshotStateMap
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.net.toUri
//import coil.compose.rememberAsyncImagePainter
//import com.example.musicplayer.R
//import com.example.musicplayer.data.DataProvider
//import com.example.musicplayer.domain.model.Song
//import com.example.musicplayer.musicPlayer.model.MusicPlayerData
//import com.example.musicplayer.musicPlayer.model.MusicPlayerData.currentPlaylist
//import com.example.musicplayer.musicPlayer.model.MusicPlayerData.currentTrackAndState
//import com.example.musicplayer.other.MusicControllerUiState
//import com.example.musicplayer.song.model.TrackState
//import com.example.musicplayer.song.ui.SongSettingsItem
//import com.example.musicplayer.ui.home.HomeUiState
//import com.example.musicplayer.ui.theme.extensions.generateDominantColorState
//import com.example.musicplayer.ui.theme.modifiers.verticalGradientBackground
//import com.example.musicplayer.ui.theme.typography
//import com.example.musicplayer.ui.home.HomeViewModel
//import com.example.musicplayer.ui.sharedresources.albumCoverImage
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CurrentTrackScreen(
//    onEvent: (SongEvent) -> Unit,
//    musicControllerUiState: MusicControllerUiState,
//    onNavigateUp: () -> Unit,
//    onGotoArtistClick: () -> Unit,
//    onGotoAlbumClick: () -> Unit
//) {
//    val image = albumCoverImage(
//        image = try {
//           musicControllerUiState.currentSong?.imageUrl!!.toUri()
//        } catch (e: Exception) {
//            DataProvider.getDefaultCover()
//        }
//    )
//    val swatch = image.generateDominantColorState()
//
//    val dominantColor = animateColorAsState(
//        targetValue = Color(swatch.rgb),
//        animationSpec = tween(durationMillis = 1000),
//        label = "animateColorAsState CurrentTrackScreen"
//    )
//
//    val surfaceColor = MaterialTheme.colorScheme.surface
//    val dominantGradient by rememberUpdatedState(newValue = listOf(dominantColor.value, surfaceColor))
//    val currentPosition = musicControllerUiState.currentPosition.toFloat()
//    val totalDuration = musicControllerUiState.totalDuration.toFloat()
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    ModalBottomSheet(
//        sheetState = sheetState,
//        onDismissRequest = { onNavigateUp() },
//        dragHandle = {},
//        shape = RoundedCornerShape(0.dp)
//    ){
//        Box(modifier = Modifier
//            .fillMaxSize()
//            .verticalGradientBackground(dominantGradient)
//        ) {
//            Column {
//                CurrentTrackTopBar(
//                    sheetState = sheetState
//                )
//                CurrentTrackBoxTopSection(
//                    viewModel = viewModel,
//                    showScreen = showScreen,
//                    onPrevious = onPrevious,
//                    onNext = onNext,
//                    onGotoArtistClick = { onGotoArtistClick() },
//                    onGotoAlbumClick = {onGotoAlbumClick()}
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//                Slider(
//                    value = currentPosition.floatValue,
//                    valueRange = 0f..totalDuration,
//                    onValueChange = { currentPosition.floatValue = it },
//                    onValueChangeFinished = { onSliderChange(currentPosition.floatValue) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 32.dp)
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//                CurrentTrackTimeRow(currentPosition.floatValue)
//                Spacer(modifier = Modifier.height(32.dp))
//                PlayerControls(onPlayPauseClick, onPrevious, onNext)
//            }
//        }
//    }
//}
//
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CurrentTrackTopBar(sheetState: SheetState){
//    val scope = rememberCoroutineScope()
//    Row(
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .padding(top = 24.dp, bottom = 8.dp)
//            .fillMaxWidth()
//            .background(Color.Transparent)
//    ) {
//        IconButton(
//            onClick = {
//                scope.launch {
//                    sheetState.hide()
//                }
//            },
//            colors= IconButtonColors(
//                containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
//                contentColor = MaterialTheme.colorScheme.onSurface,
//                disabledContainerColor = Color.Gray,
//                disabledContentColor = Color.LightGray
//            ),
//            modifier = Modifier.size(48.dp)
//        ) {
//            Icon(
//                modifier = Modifier
//                    .size(36.dp)
//                    .clip(CircleShape),
//                imageVector = Icons.Default.KeyboardArrowDown, tint = MaterialTheme.colorScheme.onSurface,
//                contentDescription = null
//            )
//        }
//    }
//}
//
//@Composable
//fun CurrentTrackBoxTopSection(viewModel: HomeViewModel, showScreen: MutableState<Boolean>, onPrevious: () -> Unit, onNext: () -> Unit,  onGotoArtistClick: () -> Unit, onGotoAlbumClick: () -> Unit) {
//    val currentTrack = remember { mutableStateOf(currentTrackAndState!!.song) }
//    val context = LocalContext.current
//    val showSongSettings = remember { mutableStateOf(false) }
//    val showAddToPlaylistDialog = remember { mutableStateOf(false) }
//    var visible by remember { mutableStateOf(true) }
//
//    LaunchedEffect(currentTrackAndState!!.song) {
//        if (currentTrack.value != currentTrackAndState!!.song) {
//            visible = false
//            delay(1000L)
//            currentTrack.value = currentTrackAndState!!.song
//        }
//        visible =true
//    }
//
//    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
//        ImagePager(onPrevious, onNext)
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .height(70.dp)
//        ) {
//            this@Column.AnimatedVisibility(
//                visible = visible,
//                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
//                exit = fadeOut(animationSpec = tween(durationMillis = 500))
//            ) {
//                SongDescription(
//                    showScreen = showScreen ,
//                    song = currentTrack.value,
//                    onSettingsClicked = { showSongSettings.value = !showSongSettings.value},
//                    onGotoArtistClick = {onGotoArtistClick()},
//                    onGotoAlbumClick = {onGotoAlbumClick()}
//                )
//            }
//        }
//    }
//
//    if (showSongSettings.value) {
//        SongSettingsItem(
//            viewModel = viewModel,
//            showSongSettings = showSongSettings,
//            showAddToPlaylistDialog = showAddToPlaylistDialog,
//            songSettingsItem = currentTrack,
//            surfaceGradient = listOf(MaterialTheme.colorScheme.background)
//        )
//    }
//}
//
//
////@OptIn(ExperimentalFoundationApi::class)
////@Composable
////fun ImagePager(homeUiState: HomeUiState, onPrevious: () -> Unit, onNext: () -> Unit) {
////    val currentTrack = remember {
////        mutableStateOf(homeUiState.selectedSong)
////    }
////    val currentTrackIndex = homeUiState.selectedPlaylist!!.songList.indexOf(homeUiState.selectedSong)
////    var previousTrack: Song? = if (currentTrackIndex > 0) {
////        homeUiState.selectedPlaylist!!.songList[currentTrackIndex - 1]
////    } else {null}
////
////    var nextTrack: Song? = if (currentTrackIndex < homeUiState.selectedPlaylist!!.songList.size - 1) {
////        homeUiState.selectedPlaylist!!.songList[currentTrackIndex + 1]
////
////    } else {
////        null
////    }
////    val albumCoverSnapshotStateList: SnapshotStateMap<Int, Song?> = SnapshotStateMap<Int, Song?>().apply {
////        put(0,previousTrack)
////        put(1,currentTrack.value)
////        put(2,nextTrack)
////    }
////
////    val isScrollEnabled = remember {
////        mutableStateOf(true)
////    }
////
////
////    val state = rememberPagerState(initialPage = 1, pageCount = { albumCoverSnapshotStateList.size})
////
////    LaunchedEffect(state.settledPage) {
////        val newTrack = albumCoverSnapshotStateList[state.settledPage]
////
////        if (newTrack != null) {
////            when(state.settledPage) {
////                0 -> onPrevious()
////                2 -> onNext()
////            }
////        } else {
////            state.animateScrollToPage(1)
////        }
////        isScrollEnabled.value = false
////        delay(250)
////        isScrollEnabled.value = true
////    }
////
////    LaunchedEffect(homeUiState.selectedSong) {
////        when(homeUiState.selectedSong) {
////            previousTrack -> state.animateScrollToPage(0)
////            nextTrack ->  state.animateScrollToPage(2)
////        }
////        currentTrack.value = homeUiState.selectedSong
////
////        previousTrack = if (currentTrackIndex > 0) {
////            homeUiState.selectedPlaylist!!.songList[currentTrackIndex - 1]
////        } else { null }
////
////        nextTrack = if (currentTrackIndex < homeUiState.selectedPlaylist!!.songList.size - 1) {
////            homeUiState.selectedPlaylist!!.songList[currentTrackIndex + 1]
////        } else { null }
////
////        albumCoverSnapshotStateList.apply {
////            put(0,previousTrack)
////            put(1,currentTrack.value)
////            put(2,nextTrack)
////        }
////        state.scrollToPage(1)
////    }
////
////        HorizontalPager(
////            state = state,
//////            pageSize = PageSize.Fixed(320.dp),
////            verticalAlignment = Alignment.CenterVertically,
////            userScrollEnabled = isScrollEnabled.value,
////            contentPadding = PaddingValues(horizontal = 28.dp),  // Adjust this value to control the visibility of neighboring pages
////            pageSpacing = 16.dp , // Adjust this value to control the space between items
////        ) { page ->
////
////            albumCoverSnapshotStateList[page % albumCoverSnapshotStateList.size]?.let { content ->
////                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
////                    Image(
////                        painter = rememberAsyncImagePainter(model = content.imageUrl),
////                        contentDescription = null,
////                        modifier = Modifier
////                            .size(320.dp)
////                            .clip(RoundedCornerShape(10.dp))
////
////                    )
////                }
////            }
////        }
////}
//
//@Composable
//fun CurrentTrackTimeRow(currentTime : Float) {
//    val totalTime = currentTrackAndState!!.song.duration.toFloat()
//    val currentTimeFormatted = formatTime(currentTime.toLong())
//    val totalTimeFormatted = formatTime(totalTime.toLong())
//
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 40.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(text = currentTimeFormatted, style = typography.titleSmall)
//        Text(text = totalTimeFormatted, style = typography.titleSmall)
//
//    }
//
//}
//
//@Composable
//fun PlayerControls(onPlayPauseClick: () -> Unit, onPrevious: () -> Unit, onNext: () -> Unit) {
//
//    val isEnabled = remember { mutableStateOf(true) }
//
//    LaunchedEffect(currentTrackAndState!!.song) {
//        isEnabled.value = false
//        delay(500)
//        isEnabled.value = true
//    }
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        PlayerPreviousTrackButton(isEnabled.value, onPrevious)
//        Spacer(modifier = Modifier.size(32.dp))
//        PlayerPlayButton(onPlayPauseClick)
//        Spacer(modifier = Modifier.size(32.dp))
//        PlayerNextTrackButton(isEnabled.value, onNext)
//    }
//}
//
//@Composable
//fun PlayerPreviousTrackButton(isEnabled:Boolean, onPrevious: () -> Unit) {
////    val buttonColor = Color(parseColor("#FFA500"))
//    val buttonIcon =Icons.Default.SkipPrevious
//    val buttonColors = ButtonColors(
//        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
//        contentColor = MaterialTheme.colorScheme.onSurface,
//        disabledContentColor = Color.Gray,
//        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
//    )
//
//
//    Button(
//        elevation = ButtonDefaults.buttonElevation(),
//        enabled = isEnabled,
//        shape = CircleShape,
//        onClick = onPrevious,
//        contentPadding = PaddingValues(0.dp),
//        colors = buttonColors,
//        modifier = Modifier.size(48.dp)
//    ) {
//        Icon(imageVector = buttonIcon, contentDescription = stringResource(R.string.previous_track_button))
//    }
//
//
//}
//
//@Composable
//fun PlayerNextTrackButton(isEnabled: Boolean, onNext: () -> Unit) {
////    val buttonColor = Color(parseColor("#FFA500"))
//    val buttonIcon =Icons.Default.SkipNext
//    val buttonColors = ButtonColors(
//        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
//        contentColor = MaterialTheme.colorScheme.onSurface,
//        disabledContentColor = Color.Gray,
//        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
//    )
//    Button(
//        enabled = isEnabled,
//        elevation = ButtonDefaults.buttonElevation(),
//        shape = CircleShape,
//        onClick = onNext,
//        contentPadding = PaddingValues(0.dp),
//        colors = buttonColors,
//        modifier = Modifier.size(48.dp)
//    ) {
//        Icon(imageVector = buttonIcon, contentDescription = stringResource(R.string.next_track_button))
//    }
//
//
//}
//
//@Composable
//fun PlayerPlayButton(onPlayPauseClick: () -> Unit) {
////    val buttonColor = Color(parseColor("#FFA500"))
//    val buttonIcon = remember { mutableStateOf(Icons.Default.PlayArrow) }
//    val buttonColors = ButtonColors(
//        containerColor = MaterialTheme.colorScheme.primary,
//        contentColor = MaterialTheme.colorScheme.onSurface,
//        disabledContentColor = Color.Gray,
//        disabledContainerColor = Color.LightGray
//    )
//
//    if (currentTrackAndState!!.state == TrackState.PLAYING) {
//        buttonIcon.value = Icons.Default.Pause
//    } else {
//        buttonIcon.value = Icons.Default.PlayArrow
//    }
//    Button(
//        elevation = ButtonDefaults.buttonElevation(),
//        shape = CircleShape,
//        onClick = onPlayPauseClick,
//        contentPadding = PaddingValues(0.dp),
//        colors = buttonColors,
//        modifier = Modifier.size(64.dp)
//    ) {
//        Icon(imageVector = buttonIcon.value, contentDescription = "PLay")
//    }
//
//
//}
//
//
//fun formatTime(duration: Long) : String {
//    val minutes = duration / 60000
//    val seconds = (duration % 60000) / 1000
//    return String.format("%02d:%02d", minutes, seconds)
//}
//
//
//@Composable
//fun SongDescription(showScreen: MutableState<Boolean>, song: Song, onSettingsClicked: (song: Song) -> Unit, onGotoArtistClick: () -> Unit, onGotoAlbumClick: () -> Unit) {
//
//    val  context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    Row(
//        modifier = Modifier
//            .height(70.dp)
//            .fillMaxWidth()
//            .padding(start = 40.dp, top = 8.dp, end = 32.dp)
//            .clip(RoundedCornerShape(5.dp))
//            .background(Color.Transparent)
//        ,
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//
//        Column(modifier = Modifier.height(70.dp)) {
//            Text(
//                text = song.title,
//                style = typography.headlineMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
//                color = MaterialTheme.colorScheme.onSurface,
//                modifier = Modifier
//                    .clickable {
//                        onGotoAlbumClick()
//                        scope.launch {
//                            delay(200)
//                            showScreen.value = false
//                        }
//                    }
//            )
//            Text(
//                text = song.artist,
//                style = typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier
//                    .clickable {
//                        onGotoArtistClick()
//
//                        scope.launch {
//                            delay(200)
//                            showScreen.value = false
//                        }
//                    },
//            )
//        }
//
//        Icon(
//            imageVector = Icons.Default.MoreVert,
//            contentDescription = null,
//            tint = MaterialTheme.colorScheme.onSurface,
//            modifier = Modifier
//                .clickable {
//                    onSettingsClicked(song)
//                }
//                .clip(CircleShape)
//        )
//    }
//}