package com.bearzwayne.musicplayer.ui.library


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.data.utils.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.usecase.AddNewPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.DeletePlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetAlbumsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetArtistsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.bearzwayne.musicplayer.domain.usecase.RenamePlaylistUseCase
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryScreenViewModel @Inject constructor(
    private val addNewPlaylistUseCase: AddNewPlaylistUseCase,
    private val renamePlaylistUseCase: RenamePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getArtistsUseCase: GetArtistsUseCase,
) : ViewModel() {
    var libraryScreenUiState by mutableStateOf(LibraryScreenUiState())
        private set

    init {
        observePlaylists()
        observeAlbums()
        observeArtists()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase().collect { resource ->
                libraryScreenUiState = when (resource) {
                    is Resource.Success -> {
                        libraryScreenUiState.copy(
                            loading = false,
                            playlists = resource.data
                        )
                    }

                    is Resource.Loading -> libraryScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> libraryScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observeAlbums() {
        viewModelScope.launch {
            getAlbumsUseCase().collect { resource ->
                libraryScreenUiState = when (resource) {
                    is Resource.Success -> libraryScreenUiState.copy(
                        loading = false,
                        albums = resource.data
                    )

                    is Resource.Loading -> libraryScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> libraryScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observeArtists() {
        viewModelScope.launch {
            getArtistsUseCase().collect { resource ->
                libraryScreenUiState = when (resource) {
                    is Resource.Success -> libraryScreenUiState.copy(
                        loading = false,
                        artists = resource.data
                    )

                    is Resource.Loading -> libraryScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> libraryScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }


    fun onEvent(event: LibraryScreenEvent) {
        when (event) {

            is LibraryScreenEvent.AddNewPlaylist -> newPlaylist(event.newPlaylist)

            is LibraryScreenEvent.DeletePlaylist -> deletePlaylist(event.playlist)

            is LibraryScreenEvent.RenamePlaylist -> renamePlaylist(event.id, event.name)

        }
    }


    private fun newPlaylist(newPlaylist: Playlist) {
        val resultPlaylist = newPlaylist.copy(
            artWork = newPlaylist.artWork.takeIf { it != DataProvider.getDefaultCover().toString() }
                ?: newPlaylist.songList.firstOrNull()?.imageUrl
                ?: newPlaylist.artWork
        )
        addNewPlaylistUseCase(resultPlaylist)
    }

    private fun deletePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)

            val newPlaylists = libraryScreenUiState.playlists!!.toMutableList().apply {
                remove(playlist)
            }

            libraryScreenUiState = libraryScreenUiState.copy(playlists = newPlaylists)

            deletePlaylistUseCase(playlist)
        }
    }

    private fun renamePlaylist(id: Int, name: String) {
        renamePlaylistUseCase(id, name)
    }

}
