package com.bearzwayne.musicplayer.ui.details


import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.AddSongsNextToCurrentUseCase
import com.bearzwayne.musicplayer.domain.usecase.AddSongsToQueueUseCase
import com.bearzwayne.musicplayer.domain.usecase.DeletePlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetAlbumUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetArtistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetCurrentSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlayerStateUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlaylistByIdUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.bearzwayne.musicplayer.domain.usecase.GetSongsUseCase
import com.bearzwayne.musicplayer.domain.usecase.PauseSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.PlaySongUseCase
import com.bearzwayne.musicplayer.domain.usecase.RenamePlaylistUseCase
import com.bearzwayne.musicplayer.domain.usecase.ResumeSongUseCase
import com.bearzwayne.musicplayer.domain.usecase.SetPlaylistUseCase
import com.bearzwayne.musicplayer.other.PlayerState
import com.bearzwayne.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _detailScreenUiState = MutableStateFlow(DetailScreenUiState())
    val detailScreenUiState: StateFlow<DetailScreenUiState> = _detailScreenUiState.asStateFlow()


    private val _detailScreenItemUiState = MutableStateFlow(DetailScreenItemUiState())
    val detailScreenItemUiState: StateFlow<DetailScreenItemUiState> = _detailScreenItemUiState.asStateFlow()
    init {
        observeSongs()
        observePlaylists()
        observeSelectedPlaylist()
        observeCurrentSong()
        observePlayerState()
    }
    private fun observeSongs() {
        viewModelScope.launch {
            getSongsUseCase().collect { resource ->
                _detailScreenUiState.value = when (resource) {
                    is Resource.Success -> _detailScreenUiState.value.copy(
                        loading = false,
                        songs = resource.data
                    )

                    is Resource.Loading -> _detailScreenUiState.value.copy(
                        loading = true
                    )

                    is Resource.Error -> _detailScreenUiState.value.copy(
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
                _detailScreenUiState.value = when (resource) {
                    is Resource.Success -> {
                        _detailScreenUiState.value.copy(
                            loading = false,
                            playlists = resource.data
                        )
                    }

                    is Resource.Loading -> _detailScreenUiState.value.copy(
                        loading = true
                    )

                    is Resource.Error -> _detailScreenUiState.value.copy(
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
                _detailScreenUiState.value = when (resource) {
                    is Resource.Success -> _detailScreenUiState.value.copy(
                        loading = false,
                        selectedPlaylist = resource.data
                    )

                    is Resource.Loading -> _detailScreenUiState.value.copy(
                        loading = true
                    )

                    is Resource.Error -> _detailScreenUiState.value.copy(
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
                _detailScreenUiState.value = when (resource) {
                    is Resource.Success -> _detailScreenUiState.value.copy(
                        loading = false,
                        selectedSong = resource.data
                    )

                    is Resource.Loading -> _detailScreenUiState.value.copy(
                        loading = true
                    )

                    is Resource.Error -> _detailScreenUiState.value.copy(
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
                _detailScreenUiState.value = _detailScreenUiState.value.copy(
                    loading = false,
                    playerState = playerState
                )
            }
        }
    }

    private fun playSong() {
        _detailScreenUiState.value.apply {
            selectedPlaylist?.songList?.indexOf(selectedSong)?.let { song ->
                playSongUseCase(song)
            }
        }
    }

    fun shufflePlay() {
        val newPlaylist = _detailScreenItemUiState.value.newPlaylist!!.copy(
            name = DataProvider.getString(R.string.shuffled, _detailScreenItemUiState.value.contentName),
            songList = _detailScreenItemUiState.value.contentSongList.shuffled()
        )
        changePlaylist(newPlaylist)
    }

    fun onLikeClick(song: Song) {
        addOrRemoveFavoriteSongUseCase(song)
    }

    private fun changePlaylist(newPlaylist: Playlist) {
        viewModelScope.launch {
            setPlaylistUseCase(newPlaylist)
            playSongUseCase(0)
        }
    }

    fun addSongsToQueue(songList: List<Song>) {
        addSongsToQueueUseCase(songList)
    }

    fun addSongListNextToCurrentSong(songList: List<Song>) {
        addSongsNextToCurrentUseCase(songList)
    }

    fun renamePlaylist(id: Int, name: String) {
        renamePlaylistUseCase(id, name)
    }
    fun deletePlaylist(playlistName: String) {
        val playlistToDelete = _detailScreenUiState.value.playlists!!.first { it.name == playlistName }
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            // If the selected playlist is the one being deleted, switch to the first playlist
            if (_detailScreenUiState.value.selectedPlaylist == playlistToDelete) {
                _detailScreenUiState.value.playlists?.let { changePlaylist(it.first()) }
            }
            deletePlaylistUseCase(playlistToDelete)
        }
    }

    fun onPlayButtonClick() {
        if (_detailScreenUiState.value.selectedPlaylist!!.name == _detailScreenItemUiState.value.contentName) {
            if (_detailScreenUiState.value.playerState == PlayerState.PLAYING) {
                pauseSongUseCase()
            } else {
                resumeSongUseCase()
            }
        } else {
            changePlaylist(_detailScreenItemUiState.value.newPlaylist!!)
        }
    }

    private fun getAlbumAsDetailScreenItem(parameter: Any) {

        _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(loading = true)
        viewModelScope.launch {
            val album = when (parameter) {
                is Int -> getAlbumUseCase(parameter)
                is String -> getAlbumUseCase(parameter)
                else -> null
            }
            if (album != null) {
                _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(
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
                _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(
                    errorMessage = "album is null"
                )
            }
        }
    }

    private fun getArtistAsDetailScreenItem(parameter: Any) {
        _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(loading = true)
        viewModelScope.launch {
            val artist = when (parameter) {
                is Int -> getArtistUseCase(parameter)
                is String -> getArtistUseCase(parameter)
                else -> null
            }
            if (artist != null) {
                _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(
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
                _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(
                    errorMessage = "album is null"
                )
            }
        }
    }

    private fun getPlaylistAsDetailScreenItem(playlistId: Int) {

        _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(loading = true)
        viewModelScope.launch {
            val playlist = getPlaylistByIdUseCase(playlistId)
            if (playlist != null) {
                _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(
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
                _detailScreenItemUiState.value = _detailScreenItemUiState.value.copy(
                    errorMessage = "album is null"
                )
            }
        }
    }

    fun setDetailScreenItem(contentId: Int?, contentName: String?, contentType: String) {
        when (contentType) {
            "album" -> getAlbumAsDetailScreenItem(contentId ?: contentName ?: 0)

            "artist" -> getArtistAsDetailScreenItem(contentId ?: contentName ?: 0)

            "playlist" -> getPlaylistAsDetailScreenItem(contentId ?: 0)

            else -> {}
        }

    }

    fun onSongListItemClick(song: Song) {
        if (_detailScreenUiState.value.selectedPlaylist!!.songList == _detailScreenItemUiState.value.contentSongList) {
            _detailScreenUiState.value = _detailScreenUiState.value.copy(selectedSong = song)
            playSong()
        } else {
            changePlaylist(
                Playlist(
                    id = _detailScreenItemUiState.value.contentId,
                    name = _detailScreenItemUiState.value.contentName,
                    songList = _detailScreenItemUiState.value.contentSongList,
                    artWork = _detailScreenItemUiState.value.contentArtworkUri.toString()
                )
            )
        }
    }
}
