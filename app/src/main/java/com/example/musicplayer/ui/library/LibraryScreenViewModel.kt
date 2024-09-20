package com.example.musicplayer.ui.library


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.usecase.AddNewPlaylistUseCase
import com.example.musicplayer.domain.usecase.DeletePlaylistUseCase
import com.example.musicplayer.domain.usecase.GetAlbumsUseCase
import com.example.musicplayer.domain.usecase.GetArtistsUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayer.domain.usecase.RenamePlaylistUseCase
import com.example.musicplayer.other.Resource
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
            getPlaylistsUseCase.invoke().collect { resource ->
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
            getAlbumsUseCase.invoke().collect { resource ->
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
            getArtistsUseCase.invoke().collect { resource ->
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
            artWork = newPlaylist.artWork.takeIf { it != DataProvider.getDefaultCover() }
                ?: newPlaylist.songList.firstOrNull()?.imageUrl?.toUri()
                ?: newPlaylist.artWork
        )
        addNewPlaylistUseCase.invoke(resultPlaylist)
    }

    private fun deletePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)

            val newPlaylists = libraryScreenUiState.playlists!!.toMutableList().apply {
                remove(playlist)
            }

            libraryScreenUiState = libraryScreenUiState.copy(playlists = newPlaylists)

            deletePlaylistUseCase.invoke(playlist)
        }
    }

    private fun renamePlaylist(id: Int, name: String) {
        renamePlaylistUseCase.invoke(id, name)
    }

}
