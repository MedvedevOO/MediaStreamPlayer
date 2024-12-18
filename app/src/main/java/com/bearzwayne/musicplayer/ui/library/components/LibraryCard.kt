package com.bearzwayne.musicplayer.ui.library.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.theme.graySurface
import com.bearzwayne.musicplayer.ui.theme.typography
import com.bearzwayne.musicplayer.ui.theme.util.isSystemDark

@SuppressLint("SuspiciousIndentation")
@Composable
fun LibraryHorizontalCardItem(
    content: Any,
    onClick: (content: Any) -> Unit
) {
    val context = LocalContext.current
    var imagePainter: Painter = painterResource(id = R.drawable.stocksongcover)
    var topCardText = ""
    var bottomCardText = ""
    var shape: Shape = RoundedCornerShape(10.dp)
    when (content) {
        is Song -> {
            imagePainter = rememberAsyncImagePainter(content.imageUrl.toUri())
            topCardText = content.artist
            bottomCardText = content.title
        }

        is Playlist -> {
            imagePainter = if (content.id == 0 || content.id == 2 || content.songList.isEmpty()) {
                rememberAsyncImagePainter(content.artWork)
            } else {
                rememberAsyncImagePainter(content.songList.first().imageUrl)
            }

            topCardText = content.name

            bottomCardText = stringResource(R.string.tracks, content.songList.size)
        }

        is Album -> {
            imagePainter = rememberAsyncImagePainter(content.albumCover)
            topCardText = content.name
            bottomCardText = stringResource(R.string.by_author, content.artist)
        }

        is Artist -> {
            imagePainter = rememberAsyncImagePainter(content.photo)
            topCardText = content.name
            bottomCardText = stringResource(
                R.string.tracks_from_albums,
                content.songList.size,
                content.albumList.size
            )
            shape = CircleShape
        }
    }

    val cardColor = if (isSystemDark(context)) graySurface else MaterialTheme.colorScheme.background
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(cardColor.copy(alpha = 0.0f))
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = {
                onClick(content)
            })


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
                maxLines = 1,
                style = typography.headlineMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .basicMarquee(repeatDelayMillis = 3000, initialDelayMillis = 3000)
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
fun LibraryVerticalCardItem(
    content: Any,
    onLibraryCardItemClick: (content: Any) -> Unit
) {
    val context = LocalContext.current
    var imagePainter: Painter = painterResource(id = R.drawable.stocksongcover)
    var shape: Shape = RoundedCornerShape(10.dp)
    var name = ""
    when (content) {
        is Playlist -> {
            imagePainter = rememberAsyncImagePainter(content.artWork)
            name = content.name
        }

        is Album -> {
            imagePainter = rememberAsyncImagePainter(content.albumCover)
            name = content.name
        }

        is Artist -> {
            imagePainter = rememberAsyncImagePainter(content.photo)
            shape = CircleShape
            name = content.name
        }
    }

    val cardColor = if (isSystemDark(context)) graySurface else MaterialTheme.colorScheme.background
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(136.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .background(cardColor.copy(alpha = 0.0f))
            .clip(shape)
            .clickable(onClick = {
                onLibraryCardItemClick(content)

            })
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(shape),

            contentScale = ContentScale.Crop
        )
        Text(
            text = name, maxLines = 3,
            style = typography.headlineSmall.copy(fontSize = 12.sp),
            modifier = Modifier
                .padding(top = 8.dp)
                .basicMarquee(repeatDelayMillis = 3000, iterations = Int.MAX_VALUE)


        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun LibraryHorizontalAddPlaylistItem(onItemClicked: () -> Unit) {
    val imagePainter: Painter = painterResource(id = R.drawable.addplaylist)
    val cardText = stringResource(R.string.create_playlist)
    val context = LocalContext.current
    val cardColor = if (isSystemDark(context)) graySurface else MaterialTheme.colorScheme.background

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(cardColor.copy(alpha = 0.0f))
            .clickable(onClick = onItemClicked)

    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = cardText,
            style = typography.headlineSmall.copy(fontSize = 14.sp),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
