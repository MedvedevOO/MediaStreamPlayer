package com.example.musicplayer.ui.editplaylist.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R

@Composable
fun EditPlaylistTopBar(onBackClick: () -> Unit, onOkClick: () -> Unit) {
    val activity = (LocalContext.current as? ComponentActivity)

    val colors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(72.dp)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            colors= colors,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                imageVector = Icons.Outlined.ArrowBackIosNew, tint = Color.Red,
                contentDescription = null
            )
        }

        Text(
            text = stringResource(R.string.reorder_or_delete_tracks),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f)
        )
        IconButton(
            onClick = onOkClick,
            colors= colors,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                imageVector = Icons.Default.Done, tint = Color.Green,
                contentDescription = null
            )
        }
    }




}