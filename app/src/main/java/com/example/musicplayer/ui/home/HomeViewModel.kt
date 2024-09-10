package com.example.musicplayer.ui.home


import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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
import com.example.musicplayer.domain.usecase.AddMediaItemsUseCase
import com.example.musicplayer.domain.usecase.AddNewPlaylistUseCase
import com.example.musicplayer.domain.usecase.AddOrRemoveFavoriteSongUseCase
import com.example.musicplayer.domain.usecase.AddSongNextToCurrentUseCase
import com.example.musicplayer.domain.usecase.AddSongsNextToCurrentUseCase
import com.example.musicplayer.domain.usecase.AddSongsToQueueUseCase
import com.example.musicplayer.domain.usecase.DeletePlaylistUseCase
import com.example.musicplayer.domain.usecase.GetAlbumsUseCase
import com.example.musicplayer.domain.usecase.GetArtistsUseCase
import com.example.musicplayer.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayer.domain.usecase.GetSongsUseCase
import com.example.musicplayer.domain.usecase.PauseSongUseCase
import com.example.musicplayer.domain.usecase.PlaySongUseCase
import com.example.musicplayer.domain.usecase.RenamePlaylistUseCase
import com.example.musicplayer.domain.usecase.ResumeSongUseCase
import com.example.musicplayer.domain.usecase.SeekSongToPositionUseCase
import com.example.musicplayer.domain.usecase.SkipToNextSongUseCase
import com.example.musicplayer.domain.usecase.SkipToPreviousSongUseCase
import com.example.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val addOrRemoveFavoriteSongUseCase: AddOrRemoveFavoriteSongUseCase,
    private val addNewPlaylistUseCase: AddNewPlaylistUseCase,
    private val renamePlaylistUseCase: RenamePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val addSongsNextToCurrentUseCase: AddSongsNextToCurrentUseCase,
    private val addSongNextToCurrentUseCase: AddSongNextToCurrentUseCase,
    private val addSongsToQueueUseCase: AddSongsToQueueUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val getPlaylistUseCase: GetPlaylistsUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getArtistsUseCase: GetArtistsUseCase,
    private val addMediaItemsUseCase: AddMediaItemsUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val seekSongToPositionUseCase: SeekSongToPositionUseCase,
    private val skipToNextSongUseCase: SkipToNextSongUseCase,
    private val skipToPreviousSongUseCase: SkipToPreviousSongUseCase
) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())
        private set
    private var contentObserver: ContentObserver

init {
    contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            getSong()

        }
    }
    context.contentResolver.registerContentObserver(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        true,
        contentObserver
    )
}



    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.PlaySong -> playSong()

            HomeEvent.PauseSong -> pauseSong()

            HomeEvent.ResumeSong -> resumeSong()

            HomeEvent.FetchSong -> getSong()

            HomeEvent.SeekToStartOfSong -> seekToStart()

            is HomeEvent.AddNewPlaylist -> newPlaylist(event.newPlaylist)

            is HomeEvent.DeletePlaylist -> deletePlaylist(event.playlist)

            is HomeEvent.RenamePlaylist -> renamePlaylist(event.id, event.name)

            is HomeEvent.OnSongSelected -> homeUiState =
                homeUiState.copy(selectedSong = event.selectedSong)

            is HomeEvent.OnSongLikeClick -> addOrRemoveFromFavorites(event.song)

            is HomeEvent.SkipToNextSong -> skipToNextSong()

            is HomeEvent.SkipToPreviousSong -> skipToPreviousSong()

            is HomeEvent.OnPlaylistChange -> changePlaylist(event.newPlaylist)

            is HomeEvent.AddSongListToQueue -> addSongsToQueue(event.songList)

            is HomeEvent.AddSongListNextToCurrentSong -> addSongsNextToCurrentSong(event.songList)

            is HomeEvent.AddSongNextToCurrentSong -> addSongNextToCurrentSong(event.song)

        }
    }

    private fun getSong() {
        homeUiState = homeUiState.copy(loading = true)

        viewModelScope.launch {
            getSongsUseCase().catch {
                homeUiState = homeUiState.copy(
                    loading = false,
                    errorMessage = it.message
                )
            }.collect {
                homeUiState = when (it) {
                    is Resource.Success -> {
                        it.data?.let { songs ->

                            addMediaItemsUseCase(songs)
                        }

                        homeUiState.copy(
                            loading = false,
                            songs = it.data
                        )
                    }

                    is Resource.Loading -> {
                        homeUiState.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is Resource.Error -> {
                        homeUiState.copy(
                            loading = false,
                            errorMessage = it.message
                        )
                    }
                }
            }
            getPlaylists()
            getArtists()
            getAlbums()
        }
    }

    private fun getPlaylists() {
        homeUiState = homeUiState.copy(loading = true)

        viewModelScope.launch {
            getPlaylistUseCase().catch {
                homeUiState = homeUiState.copy(
                    loading = false,
                    errorMessage = it.message
                )
            }.collect {
                homeUiState = when (it) {
                    is Resource.Success -> {

                        homeUiState.copy(
                            loading = false,
                            selectedPlaylist = it.data?.get(0),
                            playlists = it.data
                        )
                    }

                    is Resource.Loading -> {
                        homeUiState.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is Resource.Error -> {
                        homeUiState.copy(
                            loading = false,
                            errorMessage = it.message
                        )
                    }
                }
            }
        }
    }

    private fun getArtists() {
        homeUiState = homeUiState.copy(loading = true)

        viewModelScope.launch {
            getArtistsUseCase().catch {
                homeUiState = homeUiState.copy(
                    loading = false,
                    errorMessage = it.message
                )
            }.collect {
                homeUiState = when (it) {
                    is Resource.Success -> {

                        homeUiState.copy(
                            loading = false,
                            artists = it.data
                        )
                    }

                    is Resource.Loading -> {
                        homeUiState.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is Resource.Error -> {
                        homeUiState.copy(
                            loading = false,
                            errorMessage = it.message
                        )
                    }
                }
            }
        }
    }
    private fun getAlbums() {
        homeUiState = homeUiState.copy(loading = true)

        viewModelScope.launch {
            getAlbumsUseCase().catch {
                homeUiState = homeUiState.copy(
                    loading = false,
                    errorMessage = it.message
                )
            }.collect {
                homeUiState = when (it) {
                    is Resource.Success -> {
                        homeUiState.copy(
                            loading = false,
                            albums = it.data
                        )
                    }

                    is Resource.Loading -> {
                        homeUiState.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is Resource.Error -> {
                        homeUiState.copy(
                            loading = false,
                            errorMessage = it.message
                        )
                    }
                }
            }
        }
    }

    private fun playSong() {
        homeUiState.apply {
            selectedPlaylist?.songList?.indexOf(selectedSong)?.let { song ->
                playSongUseCase(song)
            }
        }
    }

    private fun pauseSong() = pauseSongUseCase()

    private fun resumeSong() = resumeSongUseCase()

    private fun skipToNextSong() = skipToNextSongUseCase {
        homeUiState = homeUiState.copy(selectedSong = it)
    }

    private fun skipToPreviousSong() = skipToPreviousSongUseCase {
        homeUiState = homeUiState.copy(selectedSong = it)
    }

    private fun changePlaylist(newPlaylist: Playlist){
        homeUiState = homeUiState.copy(loading = true)
        viewModelScope.launch {
            addMediaItemsUseCase.invoke(newPlaylist.songList)
            homeUiState = homeUiState.copy(
                loading = false,
                selectedPlaylist = newPlaylist,
                selectedSong = newPlaylist.songList[0]
            )
            playSong()
        }
    }

    private fun addOrRemoveFromFavorites(song: Song) {
        addOrRemoveFavoriteSongUseCase.invoke(song)

        viewModelScope.launch {
            getPlaylistUseCase().catch {
                homeUiState = homeUiState.copy(
                    loading = false,
                    errorMessage = it.message
                )
            }.collect {
                homeUiState = when (it) {
                    is Resource.Success -> {

                        homeUiState.copy(
                            loading = false,
                            playlists = it.data
                        )
                    }

                    is Resource.Loading -> {
                        homeUiState.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is Resource.Error -> {
                        homeUiState.copy(
                            loading = false,
                            errorMessage = it.message
                        )
                    }
                }
            }
        }
    }

    private fun seekToStart() {
        seekSongToPositionUseCase.invoke(0L)
    }

    private fun newPlaylist(newPlaylist: Playlist) {
        val resultPlaylist = newPlaylist.copy(
            artWork = newPlaylist.artWork.takeIf { it != DataProvider.getDefaultCover() }
                ?: newPlaylist.songList.firstOrNull()?.imageUrl?.toUri()
                ?: newPlaylist.artWork
        )
        addNewPlaylistUseCase.invoke(resultPlaylist)
        getPlaylists()
    }

    private fun deletePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)

            val newPlaylists = homeUiState.playlists!!.toMutableList().apply {
                remove(playlist)
            }

            // If the selected playlist is the one being deleted, switch to the first playlist
            if (homeUiState.selectedPlaylist == playlist) {
                changePlaylist(newPlaylists.first())
            }

            homeUiState = homeUiState.copy(playlists = newPlaylists)

            deletePlaylistUseCase.invoke(playlist)
        }
    }

    private fun renamePlaylist(id: Int, name: String) {
        homeUiState.playlists!!.first { it.id == id }.apply {
            this.name = name
            renamePlaylistUseCase.invoke(this)
        }
    }

    private fun addSongsToQueue(songList: List<Song>) {
        val songsToAdd = songList.toMutableList()
        val newPlaylist = homeUiState.selectedPlaylist!!.songList.toMutableList()

        val iterator = songsToAdd.iterator()
        while (iterator.hasNext()) {
            val song = iterator.next()
            if (newPlaylist.contains(song)) {
                iterator.remove() // Remove the current element from the list safely
            }
        }

        if (songsToAdd.isNotEmpty()) {
            newPlaylist.addAll(songsToAdd)
            val resultPlaylist = homeUiState.selectedPlaylist
            homeUiState = homeUiState.copy(
                selectedPlaylist = resultPlaylist!!.copy(
                    name = DataProvider.getString(R.string.mix),
                    songList = newPlaylist
                )
            )
        }
        addSongsToQueueUseCase.invoke(songsToAdd)

    }

    private fun addSongsNextToCurrentSong(songList: List<Song>) {
        val songsToAdd = songList.toMutableList()
        val newPlaylist = homeUiState.selectedPlaylist!!.songList.toMutableList()
        val index = if (homeUiState.selectedSong != null ) homeUiState.selectedPlaylist!!.songList.indexOf(homeUiState.selectedSong) + 1 else 1
        val iterator = songsToAdd.iterator()
        while (iterator.hasNext()) {
            val song = iterator.next()
            if (newPlaylist.contains(song)) {
                iterator.remove() // Remove the current element from the list safely
            }
        }

        if (songsToAdd.isNotEmpty()) {
            newPlaylist.addAll(index ,songsToAdd)
            val resultPlaylist = homeUiState.selectedPlaylist
            homeUiState = homeUiState.copy(
                selectedPlaylist = resultPlaylist!!.copy(
                    name = DataProvider.getString(R.string.mix),
                    songList = newPlaylist
                )
            )
        }
        addSongsNextToCurrentUseCase.invoke(songsToAdd)

    }

    private fun addSongNextToCurrentSong(song: Song) {
        val newPlaylist = homeUiState.selectedPlaylist!!.songList.toMutableList()
        val index = if (homeUiState.selectedSong != null ) homeUiState.selectedPlaylist!!.songList.indexOf(homeUiState.selectedSong) + 1 else 1
        if (newPlaylist.contains(song)) {
            newPlaylist.remove(song)
        }
        newPlaylist.add(index,song)

        val resultPlaylist = homeUiState.selectedPlaylist
        homeUiState = homeUiState.copy(
            selectedPlaylist = resultPlaylist!!.copy(
                name = DataProvider.getString(R.string.mix),
                songList = newPlaylist
                )
            )

        addSongNextToCurrentUseCase.invoke(song)

    }
}
