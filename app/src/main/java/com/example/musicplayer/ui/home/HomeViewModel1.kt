//package com.example.musicplayer.ui.home
//
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.core.net.toUri
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.musicplayer.R
//import com.example.musicplayer.data.DataProvider
//import com.example.musicplayer.domain.model.Playlist
//import com.example.musicplayer.domain.model.Song
//import com.example.musicplayer.domain.usecase.AddNewPlaylistUseCase
//import com.example.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
//import com.example.musicplayer.domain.usecase.AddSongNextToCurrentUseCase
//import com.example.musicplayer.domain.usecase.AddSongsNextToCurrentUseCase
//import com.example.musicplayer.domain.usecase.AddSongsToQueueUseCase
//import com.example.musicplayer.domain.usecase.DeletePlaylistUseCase
//import com.example.musicplayer.domain.usecase.GetAlbumsUseCase
//import com.example.musicplayer.domain.usecase.GetArtistsUseCase
//import com.example.musicplayer.domain.usecase.GetCurrentPlaylistUseCase
//import com.example.musicplayer.domain.usecase.GetCurrentSongPositionUseCase
//import com.example.musicplayer.domain.usecase.GetCurrentSongUseCase
//import com.example.musicplayer.domain.usecase.GetPlaylistsUseCase
//import com.example.musicplayer.domain.usecase.GetSongsUseCase
//import com.example.musicplayer.domain.usecase.PauseSongUseCase
//import com.example.musicplayer.domain.usecase.PlaySongUseCase
//import com.example.musicplayer.domain.usecase.RenamePlaylistUseCase
//import com.example.musicplayer.domain.usecase.ResumeSongUseCase
//import com.example.musicplayer.domain.usecase.SeekSongToPositionUseCase
//import com.example.musicplayer.domain.usecase.SetPlaylistUseCase
//import com.example.musicplayer.domain.usecase.SkipToNextSongUseCase
//import com.example.musicplayer.domain.usecase.SkipToPreviousSongUseCase
//import com.example.musicplayer.other.Resource
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class HomeViewModel1 @Inject constructor(
//    private val addOrRemoveFavoriteSongUseCase: AddOrRemoveFavoriteSongUseCase,
//    private val addNewPlaylistUseCase: AddNewPlaylistUseCase,
//    private val renamePlaylistUseCase: RenamePlaylistUseCase,
//    private val deletePlaylistUseCase: DeletePlaylistUseCase,
//    private val addSongsNextToCurrentUseCase: AddSongsNextToCurrentUseCase,
//    private val addSongNextToCurrentUseCase: AddSongNextToCurrentUseCase,
//    private val addSongsToQueueUseCase: AddSongsToQueueUseCase,
//    private val getSongsUseCase: GetSongsUseCase,
//    private val getPlaylistsUseCase: GetPlaylistsUseCase,
//    private val setPlaylistUseCase: SetPlaylistUseCase,
//    private val getCurrentPlaylistUseCase: GetCurrentPlaylistUseCase,
//    private val getCurrentSongUseCase: GetCurrentSongUseCase,
//    private val getAlbumsUseCase: GetAlbumsUseCase,
//    private val getArtistsUseCase: GetArtistsUseCase,
//    private val playSongUseCase: PlaySongUseCase,
//    private val pauseSongUseCase: PauseSongUseCase,
//    private val resumeSongUseCase: ResumeSongUseCase,
//    private val seekSongToPositionUseCase: SeekSongToPositionUseCase,
//    private val skipToNextSongUseCase: SkipToNextSongUseCase,
//    private val skipToPreviousSongUseCase: SkipToPreviousSongUseCase
//) : ViewModel() {
//
//    var homeUiState by mutableStateOf(HomeUiState())
//        private set
//
//    init {
//        observeSongs()
//        observePlaylists()
//        observeSelectedPlaylist()
//        observeAlbums()
//        observeArtists()
//        observeCurrentSong()
//    }
//
//    private fun observeSongs() {
//        viewModelScope.launch {
//            getSongsUseCase.invoke().collect { resource ->
//                homeUiState = when (resource) {
//                    is Resource.Success -> homeUiState.copy(
//                        loading = false,
//                        songs = resource.data
//                    )
//
//                    is Resource.Loading -> homeUiState.copy(
//                        loading = true
//                    )
//
//                    is Resource.Error -> homeUiState.copy(
//                        loading = false,
//                        errorMessage = resource.message
//                    )
//                }
//            }
//        }
//    }
//
//    private fun observePlaylists() {
//        viewModelScope.launch {
//            getPlaylistsUseCase.invoke().collect { resource ->
//                homeUiState = when (resource) {
//                    is Resource.Success -> {
//                        if (homeUiState.selectedPlaylist?.id == -1) {
//                            resource.data?.let { playlists ->
//                                setPlaylistUseCase(playlists[0])
//                            }
//                        }
//                        homeUiState.copy(
//                            loading = false,
//                            playlists = resource.data
//                        )
//                    }
//
//                    is Resource.Loading -> homeUiState.copy(
//                        loading = true
//                    )
//
//                    is Resource.Error -> homeUiState.copy(
//                        loading = false,
//                        errorMessage = resource.message
//                    )
//                }
//            }
//        }
//    }
//
//    private fun observeSelectedPlaylist() {
//        viewModelScope.launch {
//            getCurrentPlaylistUseCase.invoke().collect { resource ->
//                homeUiState = when (resource) {
//                    is Resource.Success -> homeUiState.copy(
//                        loading = false,
//                        selectedPlaylist = resource.data
//                    )
//
//                    is Resource.Loading -> homeUiState.copy(
//                        loading = true
//                    )
//
//                    is Resource.Error -> homeUiState.copy(
//                        loading = false,
//                        errorMessage = resource.message
//                    )
//
//
//                }
//            }
//        }
//    }
//
//    private fun observeAlbums() {
//        viewModelScope.launch {
//            getAlbumsUseCase.invoke().collect { resource ->
//                homeUiState = when (resource) {
//                    is Resource.Success -> homeUiState.copy(
//                        loading = false,
//                        albums = resource.data
//                    )
//
//                    is Resource.Loading -> homeUiState.copy(
//                        loading = true
//                    )
//
//                    is Resource.Error -> homeUiState.copy(
//                        loading = false,
//                        errorMessage = resource.message
//                    )
//                }
//            }
//        }
//    }
//
//    private fun observeArtists() {
//        viewModelScope.launch {
//            getArtistsUseCase.invoke().collect { resource ->
//                homeUiState = when (resource) {
//                    is Resource.Success -> homeUiState.copy(
//                        loading = false,
//                        artists = resource.data
//                    )
//
//                    is Resource.Loading -> homeUiState.copy(
//                        loading = true
//                    )
//
//                    is Resource.Error -> homeUiState.copy(
//                        loading = false,
//                        errorMessage = resource.message
//                    )
//                }
//            }
//        }
//    }
//
//    private fun observeCurrentSong() {
//        viewModelScope.launch {
//            getCurrentSongUseCase.invoke().collect { resource ->
//                homeUiState = when (resource) {
//                    is Resource.Success -> homeUiState.copy(
//                        loading = false,
//                        selectedSong = resource.data
//                    )
//
//                    is Resource.Loading -> homeUiState.copy(
//                        loading = true
//                    )
//
//                    is Resource.Error -> homeUiState.copy(
//                        loading = false,
//                        errorMessage = resource.message
//                    )
//                }
//            }
//        }
//    }
//
//    fun onEvent(event: HomeEvent) {
//        when (event) {
//            HomeEvent.PlaySong -> playSong()
//
//            HomeEvent.PauseSong -> pauseSong()
//
//            HomeEvent.ResumeSong -> resumeSong()
//
//            HomeEvent.SeekToStartOfSong -> seekToStart()
//
//            is HomeEvent.AddNewPlaylist -> newPlaylist(event.newPlaylist)
//
//            is HomeEvent.DeletePlaylist -> deletePlaylist(event.playlist)
//
//            is HomeEvent.RenamePlaylist -> renamePlaylist(event.id, event.name)
//
//            is HomeEvent.OnSongSelected -> homeUiState =
//                homeUiState.copy(selectedSong = event.selectedSong)
//
//            is HomeEvent.OnSongLikeClick -> addOrRemoveFromFavorites(event.song)
//
//            is HomeEvent.SkipToNextSong -> skipToNextSong()
//
//            is HomeEvent.SkipToPreviousSong -> skipToPreviousSong()
//
//            is HomeEvent.OnPlaylistChange -> changePlaylist(event.newPlaylist)
//
//            is HomeEvent.AddSongListToQueue -> addSongsToQueue(event.songList)
//
//            is HomeEvent.AddSongListNextToCurrentSong -> addSongsNextToCurrentSong(event.songList)
//
//            is HomeEvent.AddSongNextToCurrentSong -> addSongNextToCurrentSong(event.song)
//
//        }
//    }
//
//    private fun playSong() {
//        homeUiState.apply {
//            selectedPlaylist?.songList?.indexOf(selectedSong)?.let { song ->
//                playSongUseCase(song)
//            }
//        }
//    }
//
//    private fun pauseSong() = pauseSongUseCase()
//
//    private fun resumeSong() = resumeSongUseCase()
//
//    private fun skipToNextSong() = skipToNextSongUseCase()
//
//    private fun skipToPreviousSong() = skipToPreviousSongUseCase()
//
//    private fun changePlaylist(newPlaylist: Playlist) {
//        viewModelScope.launch {
//            setPlaylistUseCase.invoke(newPlaylist)
//            homeUiState = homeUiState.copy(
//                loading = false,
//                selectedSong = newPlaylist.songList[0]
//            )
//            playSong()
//        }
//    }
//
//    private fun addOrRemoveFromFavorites(song: Song) = addOrRemoveFavoriteSongUseCase.invoke(song)
//
//    private fun seekToStart() = seekSongToPositionUseCase.invoke(0L)
//
//    private fun newPlaylist(newPlaylist: Playlist) = addNewPlaylistUseCase.invoke(newPlaylist)
//
//    private fun deletePlaylist(playlist: Playlist) {
//        CoroutineScope(Dispatchers.IO).launch {
//            delay(2000L)
//            // If the selected playlist is the one being deleted, switch to the first playlist
//            if (homeUiState.selectedPlaylist == playlist) {
//                homeUiState.playlists?.let { changePlaylist(it.first()) }
//            }
//            deletePlaylistUseCase.invoke(playlist)
//        }
//    }
//
//    private fun renamePlaylist(id: Int, name: String) = renamePlaylistUseCase.invoke(id, name)
//
//    private fun addSongsToQueue(songList: List<Song>) = addSongsToQueueUseCase.invoke(songList)
//
//    private fun addSongsNextToCurrentSong(songList: List<Song>) = addSongsNextToCurrentUseCase.invoke(songList)
//
//    private fun addSongNextToCurrentSong(song: Song) = addSongNextToCurrentUseCase.invoke(song)
//
//}
