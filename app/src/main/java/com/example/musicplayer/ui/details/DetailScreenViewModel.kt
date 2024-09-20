package com.example.musicplayer.ui.details


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
import com.example.musicplayer.domain.usecase.AddSongsNextToCurrentUseCase
import com.example.musicplayer.domain.usecase.AddSongsToQueueUseCase
import com.example.musicplayer.domain.usecase.DeletePlaylistUseCase
import com.example.musicplayer.domain.usecase.GetAlbumByIdUseCase
import com.example.musicplayer.domain.usecase.GetArtistByIdUseCase
import com.example.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.example.musicplayer.domain.usecase.GetCurrentSongUseCase
import com.example.musicplayer.domain.usecase.GetPlayerStateUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayer.domain.usecase.GetSongsUseCase
import com.example.musicplayer.domain.usecase.PauseSongUseCase
import com.example.musicplayer.domain.usecase.PlaySongUseCase
import com.example.musicplayer.domain.usecase.RenamePlaylistUseCase
import com.example.musicplayer.domain.usecase.ResumeSongUseCase
import com.example.musicplayer.domain.usecase.SetPlaylistUseCase
import com.example.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val addOrRemoveFavoriteSongUseCase: AddOrRemoveFavoriteSongUseCase,
    private val renamePlaylistUseCase: RenamePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val addSongsNextToCurrentUseCase: AddSongsNextToCurrentUseCase,
    private val addSongsToQueueUseCase: AddSongsToQueueUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val setPlaylistUseCase: SetPlaylistUseCase,
    private val getCurrentPlaylistUseCase: GetCurrentPlaylistUseCase,
    private val getCurrentSongUseCase: GetCurrentSongUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val getPlayerStateUseCase: GetPlayerStateUseCase,
    private val getAlbumByIdUseCase: GetAlbumByIdUseCase,
    private val getArtistByIdUseCase: GetArtistByIdUseCase
) : ViewModel() {

    var detailScreenUiState by mutableStateOf(DetailScreenUiState())
        private set

    init {
        observeSongs()
        observePlaylists()
        observeSelectedPlaylist()
        observeCurrentSong()
        observePlayerState()
    }


    fun onEvent(event: DetailScreenEvent): Any? {
        when (event) {
            DetailScreenEvent.PlaySong -> playSong()

            DetailScreenEvent.PauseSong -> pauseSongUseCase.invoke()

            DetailScreenEvent.ResumeSong -> resumeSongUseCase.invoke()

            is DetailScreenEvent.DeletePlaylist -> deletePlaylist(event.playlistName)

            is DetailScreenEvent.RenamePlaylist -> renamePlaylistUseCase.invoke(event.id, event.name)

            is DetailScreenEvent.OnSongSelected -> detailScreenUiState =
                detailScreenUiState.copy(selectedSong = event.selectedSong)

            is DetailScreenEvent.OnSongLikeClick -> addOrRemoveFavoriteSongUseCase.invoke(event.song)

            is DetailScreenEvent.OnPlaylistChange -> changePlaylist(event.newPlaylist)

            is DetailScreenEvent.AddSongListToQueue -> addSongsToQueueUseCase.invoke(event.songList)

            is DetailScreenEvent.AddSongListNextToCurrentSong -> addSongsNextToCurrentUseCase.invoke(event.songList)

            is DetailScreenEvent.FindAlbumById -> return getAlbumByIdUseCase.invoke(event.id)

            is DetailScreenEvent.FindArtistById -> return getArtistByIdUseCase.invoke(event.id)
        }

        return null
    }

    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase.invoke().collect { resource ->
                detailScreenUiState = when (resource) {
                    is Resource.Success -> detailScreenUiState.copy(
                        loading = false,
                        songs = resource.data
                    )

                    is Resource.Loading -> detailScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> detailScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase.invoke().collect { resource ->
                detailScreenUiState = when (resource) {
                    is Resource.Success -> {
                        detailScreenUiState.copy(
                            loading = false,
                            playlists = resource.data
                        )
                    }
                    is Resource.Loading -> detailScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> detailScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observeSelectedPlaylist() {
        viewModelScope.launch {
            getCurrentPlaylistUseCase.invoke().collect { resource ->
                detailScreenUiState = when (resource) {
                    is Resource.Success -> detailScreenUiState.copy(
                        loading = false,
                        selectedPlaylist = resource.data
                    )

                    is Resource.Loading -> detailScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> detailScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )


                }
            }
        }
    }

    private fun observeCurrentSong() {
        viewModelScope.launch {
            getCurrentSongUseCase.invoke().collect { resource ->
                detailScreenUiState = when (resource) {
                    is Resource.Success -> detailScreenUiState.copy(
                        loading = false,
                        selectedSong = resource.data
                    )

                    is Resource.Loading -> detailScreenUiState.copy(
                        loading = true
                    )

                    is Resource.Error -> detailScreenUiState.copy(
                        loading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            getPlayerStateUseCase.invoke().collect { playerState ->
                detailScreenUiState = detailScreenUiState.copy(
                    loading = false,
                    playerState = playerState
                )
            }
        }
    }

    private fun playSong() {
        detailScreenUiState.apply {
            selectedPlaylist?.songList?.indexOf(selectedSong)?.let { song ->
                playSongUseCase.invoke(song)
            }
        }
    }

    private fun changePlaylist(newPlaylist: Playlist) {
        viewModelScope.launch {
            setPlaylistUseCase.invoke(newPlaylist)
            playSong()
        }
    }

    private fun deletePlaylist(playlistName: String) {
        val playlistToDelete = detailScreenUiState.playlists!!.first{ it.name == playlistName}
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            // If the selected playlist is the one being deleted, switch to the first playlist
            if (detailScreenUiState.selectedPlaylist == playlistToDelete) {
                detailScreenUiState.playlists?.let { changePlaylist(it.first()) }
            }
            deletePlaylistUseCase.invoke(playlistToDelete)
        }
    }


}
