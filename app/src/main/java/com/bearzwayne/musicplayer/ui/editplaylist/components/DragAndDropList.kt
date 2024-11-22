package com.bearzwayne.musicplayer.ui.editplaylist.components


import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.bearzwayne.musicplayer.domain.model.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
fun DragAndDropList(
    editedSongList: MutableList<Song>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragAndDropListState(onMove = onMove)

    LazyColumn(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropListState.onDrag(offset = offset)

                        if (overScrollJob?.isActive == true)
                            return@detectDragGesturesAfterLongPress

                        dragDropListState
                            .checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overScrollJob = scope.launch {
                                    dragDropListState.lazyListState.scrollBy(it)
                                }
                            } ?: kotlin.run { overScrollJob?.cancel() }
                    },
                    onDragStart = { offset -> dragDropListState.onDragStart(offset) },
                    onDragEnd = { dragDropListState.onDragInterrupted() },
                    onDragCancel = { dragDropListState.onDragInterrupted() }
                )
            }
            .fillMaxSize()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        state = dragDropListState.lazyListState
    ) {
        itemsIndexed(editedSongList) { index, song ->
            val isDragging = index == dragDropListState.currentIndexOfDraggedItem
            val offset = if (isDragging) dragDropListState.elementDisplacement else 0f
            val scale = if (isDragging) 1.1f else 1f

            Column(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = offset!!
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                EditPlaylistSongItem(
                    song = song,
                    onDeleteClicked = { editedSongList.remove(song) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

