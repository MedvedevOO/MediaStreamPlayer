package com.bearzwayne.musicplayer.ui.home.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.ui.sharedresources.MusicPlayerScreenAnimatedBackground
import com.bearzwayne.musicplayer.ui.theme.MusicPlayerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi


@Composable
fun NoPermitBox(
    allPermissionsGranted: Boolean,
    shouldShowRationale: Boolean,
    launchMultiplePermissionRequest: () -> Unit,
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val buttonColors = ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.Transparent
    )
    val permissionRequestText = stringResource(id = R.string.permission_request)
    val displayTextDevice = remember { mutableStateOf(permissionRequestText) }
    fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    LaunchedEffect(allPermissionsGranted) {
        if (allPermissionsGranted) {
            onPermissionsGranted()
        }
    }
    val onClick = remember {
        {
            when {
                !allPermissionsGranted -> {
                    if (!shouldShowRationale) {
                        launchMultiplePermissionRequest()
                    } else {
                        openAppSettings()
                    }
                }
            }
        }
    }

    if (allPermissionsGranted) {
        displayTextDevice.value = stringResource(R.string.scan_tracks)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(48.dp)
                )
                Text(
                    maxLines = 2,
                    text = displayTextDevice.value,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        shadow = Shadow(
                            MaterialTheme.colorScheme.background, blurRadius = 1f
                        )
                    ),
                    modifier = Modifier

                        .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

    } else {
        displayTextDevice.value = permissionRequestText
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = buttonColors
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Default.LibraryMusic,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                        Text(
                            maxLines = 1,
                            text = displayTextDevice.value,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .align(Alignment.CenterVertically)
                                .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                if (shouldShowRationale) {
                    Text(
                        text = stringResource(R.string.permission_to_read_media),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }

}


@Preview
@Composable
fun NoTrackOrPermitBoxPreview(){
    val context = LocalContext.current
    val defaultCoverUri =
        Uri.parse("android.resource://${context.packageName}/${R.drawable.allsongsplaylist}").toString()

    val testSong = Song(
        mediaId = "0",
        title = "Title",
        artist = "Artist",
        album = "Album",
        genre = "Genre",
        year = "2024",
        songUrl = "",
        imageUrl = defaultCoverUri,
    )

    MusicPlayerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MusicPlayerScreenAnimatedBackground(currentSong = testSong, playerState = PlayerState.STOPPED)
        }
        NoPermitBox(
            allPermissionsGranted = false,
            shouldShowRationale = true,
            launchMultiplePermissionRequest ={}) {

        }
    }

}