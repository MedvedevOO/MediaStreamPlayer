package com.bearzwayne.musicplayer.ui.editplaylist

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.details.DetailScreenItemUiState
import com.bearzwayne.musicplayer.ui.details.DetailScreenViewModel
import com.bearzwayne.musicplayer.ui.editplaylist.components.EditSongItemAdapter
import com.bearzwayne.musicplayer.ui.sharedresources.SpacingItemDecoration
import com.bearzwayne.musicplayer.ui.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSongsFragment : Fragment(R.layout.fragment_edit_songs) {

    private lateinit var addSongItemAdapter: EditSongItemAdapter
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailScreenItemUiState: DetailScreenItemUiState
    private lateinit var songList: MutableList<Song>

    private val detailScreenViewModel: DetailScreenViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.back_button)
        recyclerView = view.findViewById(R.id.song_list_recyclerview)
        detailScreenItemUiState = detailScreenViewModel.detailScreenItemUiState.value
        songList = detailScreenViewModel.detailScreenItemUiState.value.contentSongList.toMutableList()

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        // Initialize RecyclerView
        addSongItemAdapter = EditSongItemAdapter(
            songs = mutableListOf(),
            onDeleteSongButtonClick = {song ->
                songList.remove(song)
                val playlist = Playlist(
                    id = detailScreenItemUiState.contentId,
                    name = detailScreenItemUiState.contentName,
                    songList = songList,
                    artWork = if (songList.isNotEmpty()) songList[0].imageUrl else DataProvider.getDefaultCover().toString()
                )
                sharedViewModel.addNewPlaylist(playlist)
                detailScreenViewModel.setDetailScreenItem(detailScreenItemUiState.contentId, detailScreenItemUiState.contentName, "playlist")
                updateUi(songList)
            },
        )
        recyclerView.adapter = addSongItemAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpacingItemDecoration(16))

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            private var fromPosition: Int = RecyclerView.NO_POSITION
            private var toPosition: Int = RecyclerView.NO_POSITION

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Record the initial and final positions for reordering later
                if (fromPosition == RecyclerView.NO_POSITION) {
                    fromPosition = viewHolder.bindingAdapterPosition
                }
                toPosition = target.bindingAdapterPosition

                // Visual feedback during the drag
                addSongItemAdapter.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && fromPosition != RecyclerView.NO_POSITION && toPosition != RecyclerView.NO_POSITION) {
                    // Perform the actual reordering when drag finishes
                    val movedSong = songList.removeAt(fromPosition)
                    songList.add(toPosition, movedSong)

                    // Update the adapter with the reordered list
                    addSongItemAdapter.updateSongs(songList)

                    // Reset positions
                    fromPosition = RecyclerView.NO_POSITION
                    toPosition = RecyclerView.NO_POSITION

                    // Update playlist data in ViewModel
                    val playlist = Playlist(
                        id = detailScreenItemUiState.contentId,
                        name = detailScreenItemUiState.contentName,
                        songList = songList,
                        artWork = if (songList.isNotEmpty()) songList[0].imageUrl else DataProvider.getDefaultCover().toString()
                    )
                    sharedViewModel.addNewPlaylist(playlist)
                    detailScreenViewModel.setDetailScreenItem(detailScreenItemUiState.contentId, detailScreenItemUiState.contentName, "playlist")
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No swipe action required
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Make sure to reset visual feedback once the drag is done
                viewHolder.itemView.alpha = 1.0f
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        // Observe the UI state from the ViewModel
        lifecycleScope.launchWhenStarted {
            detailScreenViewModel.detailScreenUiState.collect{uiState->

                uiState.playlists?.get(detailScreenItemUiState.contentId)?.songList?.let {
                    updateUi(
                        it.toMutableList()
                    )
                }
            }
        }
    }

    private fun updateUi(contentSongList: MutableList<Song>) {
            addSongItemAdapter.updateSongs(contentSongList)
    }
}