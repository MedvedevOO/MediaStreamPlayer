package com.example.musicplayer.ui.details


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.R
import com.example.musicplayer.data.DataProvider
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
import com.example.musicplayer.domain.usecase.AddSongsNextToCurrentUseCase
import com.example.musicplayer.domain.usecase.AddSongsToQueueUseCase
import com.example.musicplayer.domain.usecase.DeletePlaylistUseCase
import com.example.musicplayer.domain.usecase.GetAlbumUseCase
import com.example.musicplayer.domain.usecase.GetArtistUseCase
import com.example.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.example.musicplayer.domain.usecase.GetCurrentSongUseCase
import com.example.musicplayer.domain.usecase.GetPlayerStateUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistByIdUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayer.domain.usecase.GetSongsUseCase
import com.example.musicplayer.domain.usecase.PauseSongUseCase
import com.example.musicplayer.domain.usecase.PlaySongUseCase
import com.example.musicplayer.domain.usecase.RenamePlaylistUseCase
import com.example.musicplayer.domain.usecase.ResumeSongUseCase
import com.example.musicplayer.domain.usecase.SetPlaylistUseCase
import com.example.musicplayer.other.PlayerState
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
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val getPlaylistByIdUseCase: GetPlaylistByIdUseCase
) : ViewModel() {

    var detailScreenUiState by mutableStateOf(DetailScreenUiState())
        private set

    var detailScreenItemUiState by mutableStateOf(DetailScreenItemUiState())

    init {
        observeSongs()
        observePlaylists()
        observeSelectedPlaylist()
        observeCurrentSong()
        observePlayerState()
    }


    fun onEvent(event: DetailScreenEvent) {
        when (event) {
            DetailScreenEvent.PlaySong -> playSong()

            DetailScreenEvent.PauseSong -> pauseSongUseCase()

            DetailScreenEvent.ResumeSong -> resumeSongUseCase()

            DetailScreenEvent.ShufflePlay -> shufflePlay()

            DetailScreenEvent.OnPlayButtonClick -> onPlayButtonClick()

            is DetailScreenEvent.DeletePlaylist -> deletePlaylist(event.playlistName)

            is DetailScreenEvent.RenamePlaylist -> renamePlaylistUseCase(event.id, event.name)

            is DetailScreenEvent.OnSongSelected -> detailScreenUiState =
                detailScreenUiState.copy(selectedSong = event.selectedSong)

            is DetailScreenEvent.OnSongLikeClick -> addOrRemoveFavoriteSongUseCase(event.song)

            is DetailScreenEvent.OnPlaylistChange -> changePlaylist(event.newPlaylist)

            is DetailScreenEvent.AddSongListToQueue -> addSongsToQueueUseCase(event.songList)

            is DetailScreenEvent.AddSongListNextToCurrentSong -> addSongsNextToCurrentUseCase(event.songList)
            is DetailScreenEvent.SetDetailScreenItem -> setDetailScreenItem(
                event.contentId,
                event.contentName,
                event.contentType
            )

            is DetailScreenEvent.onSongListItemClick -> onSongListItemClick(event.song)
        }
    }

    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase().collect { resource ->
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
            getPlaylistsUseCase().collect { resource ->
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
            getCurrentPlaylistUseCase().collect { resource ->
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
            getCurrentSongUseCase().collect { resource ->
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
            getPlayerStateUseCase().collect { playerState ->
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
                playSongUseCase(song)
            }
        }
    }

    private fun shufflePlay() {
        val newPlaylist = detailScreenItemUiState.newPlaylist!!.copy(
            name = DataProvider.getString(R.string.shuffled, detailScreenItemUiState.contentName),
            songList = detailScreenItemUiState.contentSongList.shuffled()
        )
        onEvent(DetailScreenEvent.OnPlaylistChange(newPlaylist))
    }

    private fun changePlaylist(newPlaylist: Playlist) {
        viewModelScope.launch {
            setPlaylistUseCase(newPlaylist)
            playSongUseCase(0)
        }
    }

    private fun deletePlaylist(playlistName: String) {
        val playlistToDelete = detailScreenUiState.playlists!!.first { it.name == playlistName }
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            // If the selected playlist is the one being deleted, switch to the first playlist
            if (detailScreenUiState.selectedPlaylist == playlistToDelete) {
                detailScreenUiState.playlists?.let { changePlaylist(it.first()) }
            }
            deletePlaylistUseCase(playlistToDelete)
        }
    }

    private fun onPlayButtonClick() {
        if (detailScreenUiState.selectedPlaylist!!.name == detailScreenItemUiState.contentName) {
            if (detailScreenUiState.playerState == PlayerState.PLAYING) {
                pauseSongUseCase()
            } else {
                resumeSongUseCase()
            }
        } else {
            onEvent(DetailScreenEvent.OnPlaylistChange(detailScreenItemUiState.newPlaylist!!))
        }
    }

    private fun getAlbumAsDetailScreenItem(parameter: Any) {

        detailScreenItemUiState = detailScreenItemUiState.copy(loading = true)
        viewModelScope.launch {
            val album = when (parameter) {
                is Int -> getAlbumUseCase(parameter)
                is String -> getAlbumUseCase(parameter)
                else -> null
            }
            if (album != null) {
                detailScreenItemUiState = detailScreenItemUiState.copy(
                    loading = false,
                    contentType = "album",
                    contentId = album.id.toInt(),
                    contentName = album.name,
                    contentDescription = DataProvider.getString(
                        R.string.tracks,
                        album.songList.size
                    ),
                    contentArtworkUri = album.albumCover.toUri(),
                    contentSongList = album.songList,
                    newPlaylist = Playlist(
                        id = album.id.toInt(),
                        name = album.name,
                        songList = album.songList,
                        artWork = album.albumCover
                    )

                )
            } else {
                detailScreenItemUiState = detailScreenItemUiState.copy(
                    errorMessage = "album is null"
                )
            }
        }
    }

    private fun getArtistAsDetailScreenItem(parameter: Any) {
        detailScreenItemUiState = detailScreenItemUiState.copy(loading = true)
        viewModelScope.launch {
            val artist = when (parameter) {
                is Int -> getArtistUseCase(parameter)
                is String -> getArtistUseCase(parameter)
                else -> null
            }
            if (artist != null) {
                detailScreenItemUiState = detailScreenItemUiState.copy(
                    loading = false,
                    contentType = "artist",
                    contentId = artist.id,
                    contentName = artist.name,
                    contentDescription = DataProvider.getString(
                        R.string.albums_tracks,
                        artist.albumList.size,
                        artist.songList.size
                    ),
                    contentArtworkUri = artist.photo.toUri(),
                    contentSongList = artist.songList,
                    contentAlbumsList = artist.albumList,
                    newPlaylist = Playlist(
                        id = artist.id,
                        name = artist.name,
                        songList = artist.songList,
                        artWork = artist.photo
                    )

                )
            } else {
                detailScreenItemUiState = detailScreenItemUiState.copy(
                    errorMessage = "album is null"
                )
            }
        }
    }

    private fun getPlaylistAsDetailScreenItem(playlistId: Int) {

        detailScreenItemUiState = detailScreenItemUiState.copy(loading = true)
        viewModelScope.launch {
            val playlist = getPlaylistByIdUseCase(playlistId)
            if (playlist != null) {
                detailScreenItemUiState = detailScreenItemUiState.copy(
                    loading = false,
                    contentType = "playlist",
                    contentId = playlist.id,
                    contentName = playlist.name,
                    contentDescription = DataProvider.getString(
                        R.string.tracks,
                        playlist.songList.size
                    ),
                    contentArtworkUri = playlist.artWork.toUri(),
                    contentSongList = playlist.songList,
                    newPlaylist = Playlist(
                        id = playlist.id,
                        name = playlist.name,
                        songList = playlist.songList,
                        artWork = playlist.artWork
                    )

                )
            } else {
                detailScreenItemUiState = detailScreenItemUiState.copy(
                    errorMessage = "album is null"
                )
            }
        }
    }

    private fun setDetailScreenItem(contentId: Int?, contentName: String?, contentType: String) {
        when (contentType) {
            "album" -> getAlbumAsDetailScreenItem(contentId ?: contentName ?: 0)

            "artist" -> getArtistAsDetailScreenItem(contentId ?: contentName ?: 0)

            "playlist" -> getPlaylistAsDetailScreenItem(contentId ?:  0)

            else -> {}
        }

    }

    private fun onSongListItemClick(song: Song) {
        if (detailScreenUiState.selectedPlaylist!!.songList == detailScreenItemUiState.contentSongList) {
            detailScreenUiState = detailScreenUiState.copy(selectedSong = song)
            playSong()
        } else {
            changePlaylist(
                Playlist(
                    id = detailScreenItemUiState.contentId,
                    name = detailScreenItemUiState.contentName,
                    songList = detailScreenItemUiState.contentSongList,
                    artWork = detailScreenItemUiState.contentArtworkUri.toString()
                )
            )
        }
    }
}
