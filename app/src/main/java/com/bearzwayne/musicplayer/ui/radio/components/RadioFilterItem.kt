package com.bearzwayne.musicplayer.ui.radio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bearzwayne.musicplayer.ui.theme.typography

@Composable
fun RadioFilterItem(
    filterName: String,
    onItemClick: () -> Unit
) {
    Row(

        modifier = Modifier
            .height(70.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            .clickable(onClick = { onItemClick() }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = filterName,
            style = typography.headlineSmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .basicMarquee(initialDelayMillis = 3000, repeatDelayMillis = 3000)
        )

    }
}