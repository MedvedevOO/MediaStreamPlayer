package com.example.musicplayer.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsToolBar(
    sheetState: SheetState,
    showAppSettings: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val colors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(start = 8.dp, top = 32.dp, bottom = 8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 8.dp, top = 32.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch{
                      sheetState.hide()
                      showAppSettings.value = false
                  }


                          },
                colors = colors,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            }
        }
    }
}
