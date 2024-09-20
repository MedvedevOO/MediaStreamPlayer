package com.example.musicplayer.ui.details.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.data.SettingsKeys.isSystemDark
import com.example.musicplayer.domain.model.Album
import com.example.musicplayer.domain.model.Artist
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.ui.theme.graySurface
import com.example.musicplayer.ui.theme.typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSettingsSheet(
    content: Any,
    onDismiss: () -> Unit,
    onDetailMenuItemClick: (menuItem: String, playlistId: Int) -> Unit
) {
    var playlistId = 0
    val menuItems = when(content) {

        is Playlist -> {
            mutableMapOf(
//            Pair(stringResource(R.string.download), Icons.Default.Download),
                Pair(stringResource(R.string.play_next), Icons.AutoMirrored.Filled.QueueMusic),
                Pair(stringResource(R.string.add_to_queue), Icons.AutoMirrored.Filled.PlaylistAddCheck),
                Pair(stringResource(R.string.edit), Icons.Default.Edit)
            ).apply {
                when (content.id) {
                    0 -> {
                    }
                    1, 2 -> {}
                    else -> {
                        put(stringResource(R.string.rename), Icons.Default.DriveFileRenameOutline)
                        put(stringResource(R.string.add_tracks), Icons.AutoMirrored.Filled.PlaylistAdd)
                        put(stringResource(R.string.delete_playlist), Icons.Default.Delete)
                    }
                }
                if (content.songList.isEmpty()) {
                    remove(stringResource(R.string.edit))
                }
            }.also {
                playlistId = content.id
            }

        }

        else -> {

            mutableMapOf(
//            Pair(stringResource(R.string.download), Icons.Default.Download),
            Pair(stringResource(R.string.play_next), Icons.AutoMirrored.Filled.QueueMusic),
            Pair(stringResource(R.string.add_to_queue), Icons.AutoMirrored.Filled.PlaylistAddCheck)
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
            item { DetailContentCardItem(content) }

            menuItems.forEach { menuItem ->
                item { DetailMenuItem(
                    item = menuItem.toPair(),
                    onItemClick = {name ->
                        onDetailMenuItemClick(name, playlistId)
                    }
                )
                }
                item { HorizontalDivider(thickness = 4.dp) }
            }
        }

    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun DetailContentCardItem(content: Any) {
    val shape: Shape = RoundedCornerShape(10.dp)
    val context = LocalContext.current
    val (imagePainter,topCardText,bottomCardText) = when(content) {
        is Playlist -> {
            Triple(
                rememberAsyncImagePainter(content.artWork),
                content.name,
                stringResource(R.string.tracks_size, content.songList.size)
            )

        }
        is Artist -> {
            Triple(
                rememberAsyncImagePainter(content.photo),
                content.name,
                stringResource(R.string.tracks_size, content.songList.size)
            )

        }
        is Album -> {
            Triple(
                rememberAsyncImagePainter(content.albumCover),
                content.name,
                stringResource(R.string.tracks_size, content.songList.size)
            )

        }

        else -> {
            Triple(
                rememberAsyncImagePainter(DataProvider.getDefaultCover()),
                "Error",
                "0"
            )
        }
    }
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
                style = typography.headlineMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold ),
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
fun DetailMenuItem(item: Pair<String,ImageVector>, onItemClick: (name: String) -> Unit) {

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
        Icon(imageVector = item.second, contentDescription = item.first, modifier = Modifier
            .padding(end = 8.dp)
            .size(24.dp))
        Text(
            text = item.first,
            style = typography.headlineMedium.copy(fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

    }
}
