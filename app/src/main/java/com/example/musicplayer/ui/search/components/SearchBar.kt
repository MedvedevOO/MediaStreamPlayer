package com.example.musicplayer.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.typography


@Composable
fun SearchBar(
    descriptionText: Int,
    onValueChange: (value: String) -> Unit
) {
    var textState by remember { mutableStateOf("") }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        TextField(
            value = textState,
            onValueChange = {
                textState = it
                onValueChange(textState)
                            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.White
            ),
            placeholder = {
                Text(
                    text = stringResource(descriptionText),
                    style = typography.displaySmall.copy(fontSize = 16.sp, lineHeight = 20.sp)
                )
                          },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "SearchIcon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                           },
            modifier = Modifier
                .background(Color.Transparent)
                .weight(1f),
            textStyle = typography.displaySmall.copy(fontSize = 16.sp, lineHeight = 20.sp)
        )
    }
}
