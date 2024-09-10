package com.example.musicplayer.ui.details.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.other.PlayerState
import com.example.musicplayer.ui.theme.modifiers.verticalGradientBackground

@Composable
fun AnimatedToolBar(
    contentName: String,
    selectedPlaylist: Playlist,
    playerState: PlayerState,
    scrollState: ScrollState, surfaceGradient: List<Color>,
    onPlayButtonClick: () -> Unit,
    onNavigateUp: () -> Unit) {
    val colors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )
    Column {
        Box {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(72.dp)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .verticalGradientBackground(
                        if (Dp(scrollState.value.toFloat()) < 1054.dp)
                            listOf(Color.Transparent, Color.Transparent) else surfaceGradient
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {

                IconButton(
                    onClick = onNavigateUp
                    ,
                    colors= colors,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        imageVector = Icons.Default.ArrowBackIosNew, tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier
                    .width(256.dp)
                    .weight(1f))
//                IconButton(
//                    onClick = {
//                        currentScreen.value = NavType.SEARCH
//                        activity?.finish()
//                    },
//                    colors= colors,
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Icon(
//                        modifier = Modifier
//                            .size(24.dp)
//                            .clip(CircleShape),
//                        imageVector = Icons.Default.Search, tint = MaterialTheme.colorScheme.onSurface,
//                        contentDescription = null
//                    )
//                }

                Spacer(modifier = Modifier.width(8.dp))

//                IconButton(
//                    onClick = {
//                        onSettingsClicked()
//                    },
//                    colors= colors,
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Icon(
//                        modifier = Modifier
//                            .size(24.dp)
//                            .clip(CircleShape),
//                        imageVector = Icons.Default.MoreVert, tint = MaterialTheme.colorScheme.onSurface,
//                        contentDescription = null
//                    )
//                }


            }

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {
                Text(

                    text = contentName, // vanish-able text on top
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .width(250.dp)
                        .padding(vertical = 8.dp)
                        .alpha(((scrollState.value + 0.001f) / 1000).coerceIn(0f, 1f))
                )

                if (scrollState.value >= 1044) {
                    PlayButton(
                        contentName = contentName,
                        selectedPlaylist = selectedPlaylist,
                        playerState = playerState,
                        onPlayButtonClick = onPlayButtonClick

                    )
                }

            }

        }

    }

}