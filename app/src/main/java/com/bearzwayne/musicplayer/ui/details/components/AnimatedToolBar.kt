package com.bearzwayne.musicplayer.ui.details.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.theme.modifiers.verticalGradientBackground

@Composable
fun AnimatedToolBar(
    contentName: String,
    selectedPlaylist: Playlist,
    playerState: PlayerState?,
    scrollState: ScrollState, surfaceGradient: List<Color>,
    onPlayButtonClick: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val colors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
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

                Button(
                    elevation = ButtonDefaults.buttonElevation(),
                    shape = CircleShape,
                    onClick = onNavigateUp,
                    contentPadding = PaddingValues(0.dp),
                    colors = colors,
                    modifier = Modifier.size(48.dp)
                )
                {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "PLay")
                }
                Text(
                    text = contentName, // vanish-able text on top
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .width(250.dp)
                        .padding(vertical = 8.dp)
                        .alpha(((scrollState.value + 0.001f) / 1000).coerceIn(0f, 1f))
                )
                if (scrollState.value >= 1054) {
                    PlayButton(
                        contentName = contentName,
                        selectedPlaylist = selectedPlaylist,
                        playerState = playerState,
                        onPlayButtonClick = onPlayButtonClick

                    )
                } else {
                    Spacer(modifier = Modifier.fillMaxWidth()
                        .height(48.dp))
                }
            }
        }

    }

}