package com.bearzwayne.musicplayer.ui.search

import com.bearzwayne.musicplayer.domain.model.Song

sealed class SearchScreenEvent {
    data class PlaySong(val song: Song): SearchScreenEvent()//
    data class OnSongLikeClick(val song: Song) : SearchScreenEvent()//
}