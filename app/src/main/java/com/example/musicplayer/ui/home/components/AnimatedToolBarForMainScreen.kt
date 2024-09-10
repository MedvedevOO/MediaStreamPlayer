package com.example.musicplayer.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedToolBarForMainScreen(dynamicAlpha: Float) {
//    modifier = Modifier.statusBarsPadding()
    val showCloudConnectSheet = remember {
        mutableStateOf(false)
    }
    val colors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
            .graphicsLayer { alpha = dynamicAlpha }
    ) {
        IconButton(
            onClick = {
                showCloudConnectSheet.value = true
            },
            colors= colors,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                imageVector = Icons.Default.Cloud, tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {

            },
            colors= colors,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                imageVector = Icons.Default.Settings, tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        }


    }

//        if (showCloudConnectSheet.value) {
//            CloudConnectSheet(showConnectToCloudSheet = showCloudConnectSheet)
//        }
}