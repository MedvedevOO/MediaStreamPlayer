package com.bearzwayne.musicplayer.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.utils.SettingsKeys
import com.bearzwayne.musicplayer.data.utils.SettingsKeys.dataStore
import com.bearzwayne.musicplayer.ui.library.components.Title
import com.bearzwayne.musicplayer.ui.theme.ColorPallet
import com.bearzwayne.musicplayer.ui.theme.typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsSheet(onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = {},
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column {
                AppSettingsToolBar(sheetState, onDismissRequest)
                Title(text = stringResource(R.string.general))
                ThemeDropdownMenu()
                ColorPalletDropdownMenu()
            }
        }
    }
}

@Composable
fun ThemeDropdownMenu() {
    val context = LocalContext.current
    val themeExpanded = remember { mutableStateOf(false) }
    val themeItems = listOf(
        stringResource(R.string.match_system),
        stringResource(R.string.dark), stringResource(R.string.light)
    )
    val initialTheme = stringResource(R.string.match_system)
    val selectedThemeItem = context.dataStore.data
        .map { preferences ->
            preferences[SettingsKeys.THEME_KEY] ?: initialTheme
        }.collectAsState(initial = initialTheme)

    val colors = ButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(Modifier.height(2.dp))
        Button(
            onClick = { themeExpanded.value = true },
            colors = colors,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Brush,
                    contentDescription = "Theme icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.theme, selectedThemeItem.value),
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }
        DropdownMenu(
            expanded = themeExpanded.value,
            onDismissRequest = { themeExpanded.value = false },
            offset = DpOffset(x = 56.dp, y = 0.dp)
        ) {
            themeItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        CoroutineScope(Dispatchers.Default).launch {
                            SettingsKeys.saveThemeSetting(context, item)
                        }
                        themeExpanded.value = false
                    },
                    modifier = Modifier.padding(horizontal = 2.dp),
                    contentPadding = PaddingValues(8.dp),
                    text = {
                        Text(
                            text = item,
                            style = typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
        HorizontalDivider(Modifier.height(2.dp))

    }
}


@Composable
fun ColorPalletDropdownMenu() {
    val context = LocalContext.current
    val colorPalletExpanded = remember { mutableStateOf(false) }
    val defaultColorPalette = stringResource(id = R.string.orange)
    val selectedColorPalletItem = context.dataStore.data
        .map { preferences ->
            preferences[SettingsKeys.SELECTED_COLOR_PALETTE] ?: defaultColorPalette
        }.collectAsState(initial = defaultColorPalette)
    val selectedColorPalletItemText = when (selectedColorPalletItem.value) {
        ColorPallet.PURPLE.name -> stringResource(R.string.purple)
        ColorPallet.GREEN.name -> stringResource(R.string.green)
        ColorPallet.ORANGE.name -> stringResource(R.string.orange)
        ColorPallet.BLUE.name -> stringResource(R.string.blue)
        else -> defaultColorPalette
    }
    val colors = ButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.LightGray
    )
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Button(
            onClick = { colorPalletExpanded.value = true },
            colors = colors,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "ColorPalette icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(
                        R.string.color_palette,
                        selectedColorPalletItemText
                    ),
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }
        DropdownMenu(
            expanded = colorPalletExpanded.value,
            onDismissRequest = { colorPalletExpanded.value = false },
            offset = DpOffset(x = 56.dp, y = 0.dp)
        ) {
            ColorPallet.entries.forEach { item ->
                val name = when (item) {
                    ColorPallet.PURPLE -> stringResource(R.string.purple)
                    ColorPallet.GREEN -> stringResource(R.string.green)
                    ColorPallet.ORANGE -> stringResource(R.string.orange)
                    ColorPallet.BLUE -> stringResource(R.string.blue)
                }
                DropdownMenuItem(
                    onClick = {
                        CoroutineScope(Dispatchers.Default).launch {
                            SettingsKeys.saveSelectedColorPalette(context, item)
                        }
                        colorPalletExpanded.value = false
                    },
                    modifier = Modifier.padding(horizontal = 2.dp),
                    contentPadding = PaddingValues(8.dp),
                    text = {
                        Text(
                            text = name,
                            style = typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        HorizontalDivider(Modifier.height(2.dp))

    }
}

