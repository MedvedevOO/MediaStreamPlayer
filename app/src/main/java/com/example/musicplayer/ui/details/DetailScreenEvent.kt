package com.example.musicplayer.ui.details

import androidx.media3.session.legacy.AudioAttributesCompat.AttributeContentType
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Song

sealed class DetailScreenEvent {
    data object PlaySong : DetailScreenEvent()
    data object PauseSong : DetailScreenEvent()
    data object ResumeSong : DetailScreenEvent()
    data object ShufflePlay : DetailScreenEvent()
    data object OnPlayButtonClick: DetailScreenEvent()
    data class AddSongListToQueue(val songList: List<Song>): DetailScreenEvent()
    data class AddSongListNextToCurrentSong(val songList: List<Song>): DetailScreenEvent()
    data class DeletePlaylist(val playlistName: String): DetailScreenEvent()
    data class RenamePlaylist(val id: Int, val name: String): DetailScreenEvent()
    data class OnPlaylistChange(val newPlaylist: Playlist): DetailScreenEvent()
    data class OnSongSelected(val selectedSong: Song) : DetailScreenEvent()
    data class OnSongLikeClick(val song: Song) : DetailScreenEvent()
    data class SetDetailScreenItem(val contentId: Int?, val contentName: String?, val contentType: String): DetailScreenEvent()
    data class onSongListItemClick(val song: Song): DetailScreenEvent()

}