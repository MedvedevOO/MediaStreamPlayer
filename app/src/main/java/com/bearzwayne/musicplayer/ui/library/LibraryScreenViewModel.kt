package com.bearzwayne.musicplayer.ui.library


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.data.DataProvider
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // Expose the UI state as StateFlow
    private val _libraryScreenUiState = MutableStateFlow(LibraryScreenUiState())
    val libraryScreenUiState: StateFlow<LibraryScreenUiState> = _libraryScreenUiState

    init {
        observePlaylists()
        observeAlbums()
        observeArtists()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
                            loading = false,
                            playlists = resource.data
                        )
                    }
                    is Resource.Loading -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
                        loading = true
                    )
                    is Resource.Error -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
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
                when (resource) {
                    is Resource.Success -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
                        loading = false,
                        albums = resource.data
                    )
                    is Resource.Loading -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
                        loading = true
                    )
                    is Resource.Error -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
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
                when (resource) {
                    is Resource.Success -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
                        loading = false,
                        artists = resource.data
                    )
                    is Resource.Loading -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
                        loading = true
                    )
                    is Resource.Error -> _libraryScreenUiState.value = _libraryScreenUiState.value.copy(
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

    fun newPlaylist(newPlaylist: Playlist) {
        viewModelScope.launch {
            val resultPlaylist = newPlaylist.copy(
                artWork = newPlaylist.artWork.takeIf { it != DataProvider.getDefaultCover().toString() }
                    ?: newPlaylist.songList.firstOrNull()?.imageUrl
                    ?: newPlaylist.artWork
            )
            addNewPlaylistUseCase(resultPlaylist)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000L)
            val newPlaylists: MutableList<Playlist> = _libraryScreenUiState.value.playlists!!.toMutableList().apply {
                remove(playlist)
            }

            // Update the state with the new playlists list
            _libraryScreenUiState.value = _libraryScreenUiState.value.copy(playlists = newPlaylists)

            // Call the delete use case
            deletePlaylistUseCase(playlist)
        }
    }

    fun renamePlaylist(id: Int, name: String) {
        viewModelScope.launch {
            renamePlaylistUseCase(id, name)
        }
    }
}
