package com.example.musicplayer.ui.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.data.PermissionHandler
import com.example.musicplayer.ui.theme.typography
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun NoTracksOrPermitBox(
    storagePermissionsState: MultiplePermissionsState,
    onPermissionButtonClick : () -> Unit
) {
    val buttonColors = ButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.Transparent)
    val permissionRequestText = stringResource(id = R.string.permission_request)
    val displayTextDevice = remember{ mutableStateOf(permissionRequestText) }
    val displayTextCloud = stringResource(id = R.string.connect_cloud)
    val showConnectToCloudSheet = remember { mutableStateOf(false) }
    val onClick = remember {
        {
            if (!storagePermissionsState.allPermissionsGranted) {
                storagePermissionsState.launchMultiplePermissionRequest()
            }

            if (storagePermissionsState.allPermissionsGranted) {
                onPermissionButtonClick()

            }
        }
    }


    if (storagePermissionsState.allPermissionsGranted) {
        displayTextDevice.value = stringResource(R.string.scan_tracks)
    } else {
        displayTextDevice.value = permissionRequestText
    }
    Box(
        modifier = Modifier.fillMaxSize()
        ,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = buttonColors
            ) {
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.LibraryMusic, tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                    Text(
                        maxLines = 2,
                        text = displayTextDevice.value,
                        style = typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            shadow = Shadow(
                                MaterialTheme.colorScheme.background, blurRadius = 1f
                            )
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .basicMarquee(initialDelayMillis = 5000, delayMillis = 5000),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

//            Button(
//                onClick = { showConnectToCloudSheet.value = true },
//                modifier = Modifier.fillMaxWidth(),
//                shape = CircleShape,
//                colors = buttonColors
//            ) {
//                Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
//                    Icon(
//                        modifier = Modifier.size(48.dp),
//                        imageVector = Icons.Default.Cloud, tint = MaterialTheme.colorScheme.onSurface,
//                        contentDescription = null
//                    )
//                    Text(
//                        maxLines = 2,
//                        text = displayTextCloud,
//                        style = typography.headlineLarge.copy(
//                            fontWeight = FontWeight.ExtraBold,
//                            fontSize = 16.sp,
//                            shadow = Shadow(
//                                MaterialTheme.colorScheme.background, blurRadius = 1f
//                            )
//                        ),
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .basicMarquee(initialDelayMillis = 5000, delayMillis = 5000),
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                }
//
//            }
            if (storagePermissionsState.shouldShowRationale) {
                Text(text = stringResource(R.string.permission_to_read_media), modifier = Modifier.padding(horizontal = 32.dp))
            }

//            if (showConnectToCloudSheet.value) {
//                CloudConnectSheet(showConnectToCloudSheet)
//            }

        }
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun NoTrackOrPermitBoxPreview(){
    val storagePermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionHandler.permissions()
    )
    NoTracksOrPermitBox(storagePermissionsState) {}
}