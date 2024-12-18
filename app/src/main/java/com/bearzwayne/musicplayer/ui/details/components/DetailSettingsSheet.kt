package com.bearzwayne.musicplayer.ui.details.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.ui.details.DetailScreenItemUiState
import com.bearzwayne.musicplayer.ui.theme.graySurface
import com.bearzwayne.musicplayer.ui.theme.typography
import com.bearzwayne.musicplayer.ui.theme.util.isSystemDark


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSettingsSheet(
    contentUiState: DetailScreenItemUiState,
    onDismiss: () -> Unit,
    onDetailMenuItemClick: (menuItem: String) -> Unit
) {
    val menuItems = when (contentUiState.contentType) {

        "playlist" -> {
            mutableMapOf(
                Pair(stringResource(R.string.play_next), Icons.AutoMirrored.Filled.QueueMusic),
                Pair(
                    stringResource(R.string.add_to_queue),
                    Icons.AutoMirrored.Filled.PlaylistAddCheck
                ),
                Pair(stringResource(R.string.edit), Icons.Default.Edit)
            ).apply {
                when (contentUiState.contentId) {
                    0 -> {
                    }

                    1, 2 -> {}
                    else -> {
                        put(stringResource(R.string.rename), Icons.Default.DriveFileRenameOutline)
                        put(
                            stringResource(R.string.add_tracks),
                            Icons.AutoMirrored.Filled.PlaylistAdd
                        )
                        put(stringResource(R.string.delete_playlist), Icons.Default.Delete)
                    }
                }
                if (contentUiState.contentSongList.isEmpty()) {
                    remove(stringResource(R.string.edit))
                }
            }

        }

        else -> {

            mutableMapOf(
                Pair(stringResource(R.string.play_next), Icons.AutoMirrored.Filled.QueueMusic),
                Pair(
                    stringResource(R.string.add_to_queue),
                    Icons.AutoMirrored.Filled.PlaylistAddCheck
                )
            )
        }
    }

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item { DetailContentCardItem(contentUiState) }

            menuItems.forEach { menuItem ->
                item {
                    DetailMenuItem(
                        item = menuItem.toPair(),
                        onItemClick = { name ->
                            onDetailMenuItemClick(name)
                        }
                    )
                }
                item { HorizontalDivider(thickness = 2.dp) }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }


    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun DetailContentCardItem(contentUiState: DetailScreenItemUiState) {
    val shape: Shape = RoundedCornerShape(10.dp)
    val context = LocalContext.current
    val (imagePainter, topCardText, bottomCardText) = Triple(
        rememberAsyncImagePainter(contentUiState.contentArtworkUri),
        contentUiState.contentName,
        contentUiState.contentDescription
    )
    val cardColor = if (isSystemDark(context)) graySurface else MaterialTheme.colorScheme.background
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .background(cardColor.copy(alpha = 0.0f))
            .clip(shape)


    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(shape),
            contentScale = ContentScale.Crop
        )

        Column {
            Text(
                text = topCardText,
                style = typography.headlineMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = bottomCardText,
                style = typography.titleLarge.copy(fontSize = 14.sp),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun DetailMenuItem(item: Pair<String, ImageVector>, onItemClick: (name: String) -> Unit) {

    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                onItemClick(item.first)
            }
    ) {
        Icon(
            imageVector = item.second, contentDescription = item.first, modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp)
        )
        Text(
            text = item.first,
            style = typography.headlineMedium.copy(fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

    }
}
